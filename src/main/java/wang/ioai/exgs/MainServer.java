package wang.ioai.exgs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.data.GData;

import java.nio.file.Paths;

public class MainServer {
    private static final Logger logger = LoggerFactory.getLogger(MainServer.class);
    private static final String userDir = System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {
        logger.info("user.dir: {}", userDir);
        GData.init();
        GData.config.load(Paths.get(userDir, "config/server.json").toString());
        GData.tick.init();
        GData.tick.run();
        GData.netServer.init();
        GData.netServer.start();
    }
}
