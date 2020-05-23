package org.feuyeux.knowhow.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author feuyeux
 */
@Slf4j
public class ThreadCoon {
    private static final int CORE_POOL_SIZE = 50;
    private static final int QUEUE_SIZE = 50;
    private static final int MAX_POOL_SIZE = 500;
    private static final long KEEP_ALIVE_TIME = 5L;
    private static final String NAME_FORMAT = "media-pool-%d";
    private static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(NAME_FORMAT).build();

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(QUEUE_SIZE),
            threadFactory,
            new ThreadPoolExecutor.AbortPolicy());

    public static <T> Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }
}
