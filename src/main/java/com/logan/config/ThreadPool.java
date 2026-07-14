package com.logan.config;

import com.logan.utils.LogUtils;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    // 线程池--用来压缩图片使用
    public static ThreadPoolExecutor asyncPool;

    public static void init() {
        asyncExecutor();
    }

    public static ThreadPoolExecutor asyncExecutor() {
        if (asyncPool == null) {
            int corePoolSize = 4;
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            if (availableProcessors > corePoolSize) {
                corePoolSize = availableProcessors;
            }
            asyncPool = new ThreadPoolExecutor(
                    corePoolSize, corePoolSize * 2, 5, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(corePoolSize * 1000), new ThreadPoolExecutor.CallerRunsPolicy());
        }

        LogUtils.info("asyncPool init. corePoolSize: " + asyncPool.getCorePoolSize() + " queue size: " + asyncPool.getQueue().size());
        return asyncPool;
    }
}
