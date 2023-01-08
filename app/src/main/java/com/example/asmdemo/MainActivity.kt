package com.example.asmdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.addcn.monitor.activity.MethodThreadActivity
import com.addcn.monitor.click.ViewDoubleClickCheck
import com.addcn.monitor.login.LoginHook
import com.addcn.monitor.login.NeedLogin
import com.addcn.monitor.record.MethodRecordManager


class MainActivity : AppCompatActivity() {

//    private val newScheduledThreadPool = Executors.newScheduledThreadPool(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        MethodRecordManager.recordMethodStart("MainActivity", "onCreate", "savedInstanceState")
        setContentView(R.layout.activity_main)
//        test()
//        findViewById<TextView>(R.id.tv_name).setOnClickListener {
//            MethodRecordManager.stop()
//            startActivity(Intent(this, MethodThreadActivity::class.java))
//        }
//
//        newScheduledThreadPool.execute {
//            while (true) {
//                println(Thread.currentThread().name)
//                Thread.sleep(500)
//            }
//        };
        //方法耗时收集
        findViewById<Button>(R.id.btn_method_record).setOnClickListener {
            MethodRecordManager.stop()
            startActivity(Intent(this, MethodThreadActivity::class.java))
        }
        //大图检测
        findViewById<Button>(R.id.btn_large_image).setOnClickListener {
            startActivity(Intent(this, LargeImageActivity::class.java))
        }

        //日志替换
        findViewById<Button>(R.id.btn_log).setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }
        //点击防抖
        findViewById<Button>(R.id.btn_click).setOnClickListener {
            startActivity(Intent(this, ClickActivity::class.java))
        }
        //线程治理
        findViewById<Button>(R.id.btn_thread).setOnClickListener {
            startActivity(Intent(this, ThreadActivity::class.java))
        }
        //登录拦截
        findViewById<Button>(R.id.btn_login).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    @NeedLogin
    fun startUserHouse() {

    }
}

//    @MethodRecord
//    fun test() {
////        Log.d("TAG11", "123123123")
////        Log.i("TAG11", "123123123")
////        Log.v("TAG11", "123123123")
////        Log.e("TAG11", "123123123")
////        Log.d("TAG11", "123123123")
////        Log.d("TAG11", "123123123")
////        Log.d("TAG11", "123123123")
//        Thread.sleep(100)
//    }
//
//
//    override fun onStart() {
//        super.onStart()
//        Thread.sleep(50)
//    }
//}