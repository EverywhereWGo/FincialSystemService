package com.ruoyi.system.service.finance.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 财务系统线程池工具类
 * 
 * @author ruoyi
 */
public class FinanceThreadPoolUtil {

    private static final Logger log = LoggerFactory.getLogger(FinanceThreadPoolUtil.class);

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 5;

    /**
     * 最大线程数
     */
    private static final int MAX_POOL_SIZE = 10;

    /**
     * 队列大小
     */
    private static final int QUEUE_CAPACITY = 100;

    /**
     * 线程池维护线程所允许的空闲时间
     */
    private static final long KEEP_ALIVE_TIME = 60L;

    /**
     * 线程池对象
     */
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAX_POOL_SIZE,
        KEEP_ALIVE_TIME,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(QUEUE_CAPACITY),
        new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 执行任务
     * 
     * @param task 任务
     */
    public static void execute(Runnable task) {
        if (task == null) {
            return;
        }
        EXECUTOR.execute(task);
    }

    /**
     * 关闭线程池（应用关闭时调用）
     */
    public static void shutdown() {
        if (!EXECUTOR.isShutdown()) {
            EXECUTOR.shutdown();
            try {
                if (!EXECUTOR.awaitTermination(60, TimeUnit.SECONDS)) {
                    EXECUTOR.shutdownNow();
                    if (!EXECUTOR.awaitTermination(60, TimeUnit.SECONDS)) {
                        log.error("线程池未能完全终止");
                    }
                }
            } catch (InterruptedException ie) {
                EXECUTOR.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
} 