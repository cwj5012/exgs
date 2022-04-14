package wang.ioai.exgs.exec.master;

import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.core.BaseServer;
import wang.ioai.exgs.core.config.Config;
import wang.ioai.exgs.core.define.EStatus;
import wang.ioai.exgs.core.net.ChannelContainer;
import wang.ioai.exgs.core.system.NodeInfo;
import wang.ioai.exgs.core.util.CommonPath;

public class MasterBoot {
    private static final Logger logger = LoggerFactory.getLogger(MasterBoot.class);

    public Config config;
    public BaseServer baseServer;
    public ChannelContainer channelContainer;

    public MasterBoot() {
        var nodeInfo = new NodeInfo();
        nodeInfo.status = EStatus.boot;
        nodeInfo.node_id = 1;
        nodeInfo.node_type = 4;
        nodeInfo.updateStartTime();

        baseServer = new BaseServer(nodeInfo);
        config = new Config();
        channelContainer = new ChannelContainer();
        // authHandler = new AuthHandler(this);
        // authModule = new AuthModule();
    }

    public void init() {
        config.load(CommonPath.configPath());
        baseServer.init(config.server.manager, channelContainer);
        // authHandler.init(baseServer.netServer.dispatch);
        // authModule.init();
    }

    public void run() {
        baseServer.run();
    }
}
