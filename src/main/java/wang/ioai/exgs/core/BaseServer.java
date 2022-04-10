package wang.ioai.exgs.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.core.config.ServerBaseConfig;
import wang.ioai.exgs.core.define.EStatus;
import wang.ioai.exgs.core.net.ChannelContainer;
import wang.ioai.exgs.core.net.NetServer;
import wang.ioai.exgs.core.net.handler.BaseHandler;
import wang.ioai.exgs.core.system.NodeInfo;
import wang.ioai.exgs.core.util.CommonPath;

public class BaseServer {
    private static final Logger logger = LoggerFactory.getLogger(BaseServer.class);

    public NetServer netServer;
    public BaseHandler baseHandler;
    public NodeInfo nodeInfo;

    public BaseServer(NodeInfo nodeInfo) {
        logger.info("login server start.");
        logger.info("user.dir: {}", CommonPath.userPath());
        logger.info("pid: {}", ProcessHandle.current().pid());

        this.nodeInfo = nodeInfo;

        netServer = new NetServer();
        baseHandler = new BaseHandler(netServer, this.nodeInfo);
    }

    public void init(ServerBaseConfig config, ChannelContainer channelContainer) {
        nodeInfo.status = EStatus.init;

        netServer.init(config, channelContainer);
        baseHandler.init(netServer.dispatch);
    }

    public void run() {
        nodeInfo.status = EStatus.running;
        netServer.start();
    }
}
