package wang.ioai.exgs.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.data.GData;

public class Boot {
    private static final Logger logger = LoggerFactory.getLogger(Boot.class);

    public void init() {
        logger.info("user.dir: {}", System.getProperty("user.dir"));
        logger.info("pid: {}", ProcessHandle.current().pid());
        // 添加到 GData
        GData.setBoot(this);
        // 初始化所有组件
        GData.init();
    }

    public void run() {
        GData.tick.run();
        GData.netServer.start();
    }
}
