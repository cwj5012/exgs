package wang.ioai.exgs.exec.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.core.config.Config;
import wang.ioai.exgs.core.net.NetServer;
import wang.ioai.exgs.core.define.EStatus;
import wang.ioai.exgs.core.net.handler.BaseHandler;
import wang.ioai.exgs.core.system.NodeInfo;
import wang.ioai.exgs.core.util.CommonPath;

public class RouteBoot {
    private static final Logger logger = LoggerFactory.getLogger(RouteBoot.class);

    public static Config config;
    public static NetServer netServer;
    public static BaseHandler baseHandler;
    public static NodeInfo nodeInfo;

    public RouteBoot() {
        logger.info("route server start.");
        logger.info("user.dir: {}", CommonPath.userPath());
        logger.info("pid: {}", ProcessHandle.current().pid());

        nodeInfo = new NodeInfo();
        nodeInfo.status = EStatus.boot;
        nodeInfo.node_id = 1;
        nodeInfo.node_type = 1;
        nodeInfo.updateStartTime();

        config = new Config();
        netServer = new NetServer();
        baseHandler = new BaseHandler(netServer);
    }

    public void init() {
        nodeInfo.status = EStatus.init;
        config.load(CommonPath.configPath());
        netServer.init(config.server.route);
        baseHandler.init(netServer.dispatch);
    }

    public void run() {
        nodeInfo.status = EStatus.running;
        netServer.start();
    }
}
