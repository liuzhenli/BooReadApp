package com.liuzhenli.reader.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 *
 * @author liuzhenli 2020/11/15
 * Email: 848808263@qq.com
 */
public class ThreadUtils {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static ThreadUtils instance = new ThreadUtils();
    private ExecutorService executorService;

    private ThreadUtils() {
    }

    public static ThreadUtils getInstance() {
        return instance;
    }


    private ThreadFactory mThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ReadThread#" + mCount.getAndIncrement());
        }
    };


    public ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
                    //线程池中的任务队列，该队列主要用来存储已经被提交但是尚未执行的任务。存储在这里的任务是由ThreadPoolExecutor的execute方法提交来的。
                    new LinkedBlockingQueue<>(50),
                    //为线程池提供创建新线程的功能，这个我们一般使用默认即可。
                    mThreadFactory
            );
        }
        return executorService;
    }
}
