package com.addcn.monitor.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Gentlman
 * @time 2022/12/8 17:59
 * @desc
 */
public class CustomExecutors {

    private static final long defaultThreadKeepAliveTime = 5000L;


    public static ExecutorService newFixedThreadPool(int nThreads, String className) {
        return newFixedThreadPool(nThreads, null, className);
    }

    public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory factory, String className) {
        return getOptimizedExecutorService(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), factory, className);
    }

    public static ExecutorService newSingleThreadExecutor(String className) {
        return newSingleThreadExecutor(null, className);
    }

    public static ExecutorService newSingleThreadExecutor(
            ThreadFactory threadFactory,
            String className) {
        return getOptimizedExecutorService(
                1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory, className
        );
    }

    public static ExecutorService newCachedThreadPool(String className) {
        return newCachedThreadPool(null, className);
    }

    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory, String className) {
        return getOptimizedExecutorService(
                0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                threadFactory, className
        );
    }


    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, String className) {
        return newScheduledThreadPool(corePoolSize, null, className);
    }

    public static ScheduledExecutorService newScheduledThreadPool(
            int corePoolSize,
            ThreadFactory threadFactory,
            String className
    ) {
        return getOptimizedScheduledExecutorService(corePoolSize, threadFactory, className);
    }


    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String className) {
        return newSingleThreadScheduledExecutor(null, className);
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(
            ThreadFactory threadFactory,
            String className
    ) {
        return getOptimizedScheduledExecutorService(1, threadFactory, className);
    }

    private static ExecutorService getOptimizedExecutorService(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, String className) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new NamedThreadFactory(threadFactory, className));
        executor.setKeepAliveTime(defaultThreadKeepAliveTime, TimeUnit.MILLISECONDS);
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    private static ScheduledExecutorService getOptimizedScheduledExecutorService(
            int threadSize,
            ThreadFactory threadFactory,
            String className
    ) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
                threadSize,
                new NamedThreadFactory(threadFactory, className)
        );
        executor.setKeepAliveTime(defaultThreadKeepAliveTime, TimeUnit.MILLISECONDS);
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }


    private static class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger threadId = new AtomicInteger(0);

        ThreadFactory threadFactory;
        String className;

        NamedThreadFactory(ThreadFactory threadFactory, String className) {
            this.threadFactory = threadFactory;
            this.className = className;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread originThread = null;
            if (threadFactory != null) {
                originThread = threadFactory.newThread(runnable);
            }
            String threadName = className + "-" + threadId.getAndIncrement();
            if (originThread != null) {
                threadName += ("-" + originThread.getName());
            }
            Thread thread = originThread;
            if (thread == null) {
                thread = new Thread(runnable);
            }
            thread.setName(threadName);
            if (thread.isDaemon()) {
                thread.setDaemon(false);
            }

            if (thread.getPriority() != Thread.NORM_PRIORITY) {
                thread.setPriority(Thread.NORM_PRIORITY);
            }
            return thread;
        }


    }
}
