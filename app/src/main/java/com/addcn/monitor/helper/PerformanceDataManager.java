package com.addcn.monitor.helper;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.view.Choreographer;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * @author Gentlman
 * @time 2022/12/2 18:37
 * @desc
 */
public class PerformanceDataManager {

    /**
     * 信息采集时间（内存和CPU）
     */
    private static final int NORMAL_SAMPLING_TIME = 500;

    /**
     * FPS 采集时间
     */
    private static final int FPS_SAMPLING_TIME = 1000;

    /**
     * 默认的最大帧率 60
     */
    private static final int MAX_FRAME_RATE = 60;

    /**
     * Handle 消息
     */
    private static final int MSG_CPU = 1;
    private static final int MSG_MEMORY = 2;
    private static final int MSG_NET_FLOW = 4;


    private RandomAccessFile mProcStatFile;
    private RandomAccessFile mAppStartFile;
    private Long mLastCpuTime;
    private Long mLastAppCpuTime;

    /**
     * 是否是Android8.0以上
     */
    private boolean mAboveAndroidO;

    /**
     * CPU 使用百分比
     */
    private float mLastCpuRate = 0;

    /**
     * 当前使用内存
     */
    private float mLastMemoryRate;

    /**
     * 最大内存
     */
    private float mMaxMemory;

    /**
     * 最大帧率
     */
    private int mMaxFrameRate = MAX_FRAME_RATE;

    /**
     * 当前的帧率
     */
    private int mLastFrameRate = mMaxFrameRate;


    private Context mContext;
    private ActivityManager mActivityManager;
    private WindowManager mWindowManager;
    /**
     * 子线程Handler
     */
    private HandlerThread mHandlerThread;
    private Handler mNormalHandler;

    /**
     * 主线程Handler
     */
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    /**
     * 获取帧率的任务
     */
    private FrameRateRunnable mRateRunnable = new FrameRateRunnable();

    /**
     * 数据回调
     */
    IPerformanceDataCallback mCallback;

    private static class Holder {
        private static final PerformanceDataManager INSTANCE = new PerformanceDataManager();
    }

    private PerformanceDataManager() {
    }

    public static PerformanceDataManager getInstance() {
        return Holder.INSTANCE;
    }


    public void init(Context context) {
        mContext = context.getApplicationContext();
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (mWindowManager != null) {
            mMaxFrameRate = (int) mWindowManager.getDefaultDisplay().getRefreshRate();
        } else {
            mMaxFrameRate = MAX_FRAME_RATE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAboveAndroidO = true;
        }

        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("handler-thread");
            mHandlerThread.start();
        }
        if (mNormalHandler == null) {
            mNormalHandler = new Handler(mHandlerThread.getLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == MSG_CPU) {
                        executeCpuData();
                        mNormalHandler.sendEmptyMessageDelayed(MSG_CPU, NORMAL_SAMPLING_TIME);
                    } else if (msg.what == MSG_MEMORY) {
                        executeMemoryData();
                        mNormalHandler.sendEmptyMessageDelayed(MSG_MEMORY, NORMAL_SAMPLING_TIME);
                    }
                }
            };
        }
    }

    public void setPerformanceDataCallback(IPerformanceDataCallback callback) {
        mCallback = callback;
    }


    /**
     * 开始收集帧率数据
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startMonitorFrameInfo() {
        //开启定时任务
        mMainHandler.postDelayed(mRateRunnable, FPS_SAMPLING_TIME);
        Choreographer.getInstance().postFrameCallback(mRateRunnable);
    }


    /**
     * 停止收集帧率数据
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stopMonitorFrameInfo() {
        Choreographer.getInstance().removeFrameCallback(mRateRunnable);
        mMainHandler.removeCallbacks(mRateRunnable);
    }

    /**
     * 开始收集CPU数据
     */
    public void startMonitorCPUInfo() {
        mNormalHandler.sendEmptyMessageDelayed(MSG_CPU, NORMAL_SAMPLING_TIME);
    }

    /**
     * 停止收集CPU数据
     */
    public void stopMonitorCPUInfo() {
        mNormalHandler.removeMessages(MSG_CPU);
    }

    public void startMonitorMemoryInfo() {
        if (mMaxMemory == 0) {
            mMaxMemory = mActivityManager.getMemoryClass();
        }
        mNormalHandler.sendEmptyMessageDelayed(MSG_MEMORY, NORMAL_SAMPLING_TIME);
    }

    public void stopMonitorMemoryInfo() {
        mNormalHandler.removeMessages(MSG_MEMORY);
    }


    /**
     * 获取CPU数据
     */
    private void executeCpuData() {
        if (mAboveAndroidO) {
            mLastCpuRate = getCpuDataForO();
        } else {
            mLastCpuRate = getCpuData();
        }
        callCpuData();
    }

    /**
     * Android 8.0以上获取CPU数据
     *
     * @return CPU 使用率
     */
    private float getCpuDataForO() {
        java.lang.Process process = null;
        try {
            process = Runtime.getRuntime().exec("top -n 1");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int cpuIndex = -1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (TextUtils.isEmpty(line)) {
                    continue;
                }
                int tempIndex = getCpuIndex(line);
                if (tempIndex != -1) {
                    cpuIndex = tempIndex;
                    continue;
                }
                if (line.startsWith(String.valueOf(Process.myPid()))) {
                    if (cpuIndex == -1) {
                        continue;
                    }
                    String[] param = line.split("\\s+");
                    if (param.length <= cpuIndex) {
                        continue;
                    }
                    String cpu = param[cpuIndex];
                    if (cpu.endsWith("%")) {
                        cpu = cpu.substring(0, cpu.lastIndexOf("%"));
                    }
                    return Float.parseFloat(cpu) / Runtime.getRuntime().availableProcessors();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return 0;
    }

    /**
     * Android 8.0以下获取CPU数据
     *
     * @return CPU 使用率
     */
    private float getCpuData() {
        long cpuTime;
        long appTime;
        float value = 0.0f;
        try {
            if (mProcStatFile == null || mAppStartFile == null) {
                mProcStatFile = new RandomAccessFile("/proc/stat", "r");
                mAppStartFile = new RandomAccessFile("/proc/" + Process.myPid() + "/stat", "r");
            } else {
                mProcStatFile.seek(0L);
                mAppStartFile.seek(0L);
            }
            String procStatString = mProcStatFile.readLine();
            String appStatString = mAppStartFile.readLine();
            String[] procStats = procStatString.split(" ");
            String[] appStats = appStatString.split(" ");
            cpuTime = Long.parseLong(procStats[2]) + Long.parseLong(procStats[3]) + Long.parseLong(procStats[4]) + Long.parseLong(procStats[5]) + Long.parseLong(procStats[6]) + Long.parseLong(procStats[7]) + Long.parseLong(procStats[8]);
            appTime = Long.parseLong(appStats[13]) + Long.parseLong(appStats[14]);
            if (mLastCpuTime == null && mLastAppCpuTime == null) {
                mLastCpuTime = cpuTime;
                mLastAppCpuTime = appTime;
                return value;
            }
            value = ((float) (appTime - mLastAppCpuTime) / (float) (cpuTime - mLastCpuTime)) * 100f;
            mLastCpuTime = cpuTime;
            mLastAppCpuTime = appTime;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private int getCpuIndex(String line) {
        if (line.contains("CPU")) {
            String[] titles = line.split("\\s+");
            for (int i = 0; i < titles.length; i++) {
                if (titles[i].contains("CPU")) {
                    return i;
                }
            }
        }

        return -1;
    }


    /**
     * 获取内存使用
     */
    private void executeMemoryData() {
        mLastMemoryRate = getMemoryData();
        callMemoryData();
    }

    private float getMemoryData() {
        float mem = 0.0F;
        try {
            Debug.MemoryInfo memInfo = null;
            //28 为Android P
            if (Build.VERSION.SDK_INT > 28) {
                // 统计进程的内存信息 totalPss
                memInfo = new Debug.MemoryInfo();
                Debug.getMemoryInfo(memInfo);
            } else {
                //从Android Q开始，对于普通应用，该方法只会返回以调用者uid的身份运行的进程的内存信息；
                //没有其他进程内存信息可用，将为零。同样对于Android Q，此接口允许的采样率有很大限制，如果调用速度快于限制，您将收到与上一次调用相同的数据。
                Debug.MemoryInfo[] memInfoList = mActivityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
                if (memInfoList != null && memInfoList.length > 0) {
                    memInfo = memInfoList[0];
                }
            }
            int totalPss = 0;
            if (memInfo != null) {
                totalPss = memInfo.getTotalPss();
            }
            if (totalPss >= 0) {
                // Mem in MB
                mem = totalPss / 1024.0F;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mem;
    }

    /**
     * 获取最大内存
     *
     * @return 最大内存
     */
    public float getMaxMemory() {
        return mMaxMemory;
    }


    /**
     * 读取fps的线程
     */
    private class FrameRateRunnable implements Runnable, Choreographer.FrameCallback {
        private int totalFramesPerSecond;

        @Override
        public void run() {
            mLastFrameRate = totalFramesPerSecond;
            if (mLastFrameRate > mMaxFrameRate) {
                mLastFrameRate = mMaxFrameRate;
            }
            callFpsData();
            totalFramesPerSecond = 0;
            //1s中统计一次
            mMainHandler.postDelayed(this, FPS_SAMPLING_TIME);
        }

        //
        @Override
        public void doFrame(long frameTimeNanos) {
            totalFramesPerSecond++;
            Choreographer.getInstance().postFrameCallback(this);
        }
    }


    private void callCpuData() {
        if (mCallback != null) {
            mCallback.onCpuCallback(mLastCpuRate);
        }
    }

    private void callMemoryData() {
        if (mCallback != null) {
            mCallback.onMemoryCallback(mLastMemoryRate);
        }
    }

    private void callFpsData() {
        if (mCallback != null) {
            mCallback.onFpsCallback(mLastFrameRate);
        }
    }

    public interface IPerformanceDataCallback {
        void onCpuCallback(float cpuRate);

        void onMemoryCallback(float memoryRate);

        void onFpsCallback(float frameRate);
    }


}
