package wang.ioai.exgs.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.data.GData;

public class Boot {
    private static final Logger logger = LoggerFactory.getLogger(Boot.class);
    private static final String userDir = System.getProperty("user.dir");

    public void init() {
        logger.info("user.dir: {}", userDir);
        logger.info("pid: {}", ProcessHandle.current().pid());
        GData.init();
        GData.tick.init();
        GData.netServer.init();
    }

    public void run() {
        GData.tick.run();
        GData.netServer.start();
    }
}
