package wang.ioai.exgs.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterBoot {
    private static final Logger logger = LoggerFactory.getLogger(MasterBoot.class);

    public void init() {
        logger.info("user.dir: {}", System.getProperty("user.dir"));
        logger.info("pid: {}", ProcessHandle.current().pid());
    }

    public void run() {

    }
}
