package wang.ioai.exgs.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wang.ioai.exgs.core.config.Config;
import wang.ioai.exgs.core.net.NetServer;
import wang.ioai.exgs.core.util.CommonPath;

public class MasterBoot {
    private static final Logger logger = LoggerFactory.getLogger(MasterBoot.class);

    public static Config config;
    public static NetServer netServer;

    public void init() {
        logger.info("user.dir: {}", CommonPath.userPath());
        logger.info("pid: {}", ProcessHandle.current().pid());

        config = new Config();
        netServer = new NetServer();

        config.load(CommonPath.configPath());
    }

    public void run() {

    }
}
