package wang.ioai.exgs.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolErrors extends ThreadPoolExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolErrors.class);

    public ThreadPoolErrors(int corePoolSize) {
        super(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        // super.afterExecute(r, t);
        if (t != null) {
            logger.error("{}", t.getMessage());
        }
    }
}
