package wang.ioai.exgs.exec.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.core.BaseServer;
import wang.ioai.exgs.core.config.Config;
import wang.ioai.exgs.core.define.EStatus;
import wang.ioai.exgs.core.net.ChannelContainer;
import wang.ioai.exgs.core.system.NodeInfo;
import wang.ioai.exgs.core.util.CommonPath;
import wang.ioai.exgs.exec.login.handler.AuthHandler;
import wang.ioai.exgs.exec.login.module.AuthModule;

public class LoginBoot {
    private static final Logger logger = LoggerFactory.getLogger(LoginBoot.class);

    public Config config;
    public BaseServer baseServer;
    public AuthHandler authHandler;
    public AuthModule authModule;
    public ChannelContainer channelContainer;

    public LoginBoot() {
        var nodeInfo = new NodeInfo();
        nodeInfo.status = EStatus.boot;
        nodeInfo.node_id = 1;
        nodeInfo.node_type = 4;
        nodeInfo.updateStartTime();

        config = new Config();
        channelContainer = new ChannelContainer();
        baseServer = new BaseServer(nodeInfo);
        authHandler = new AuthHandler(this);
        authModule = new AuthModule();
    }

    public void init() {
        config.load(CommonPath.configPath());
        baseServer.init(config.server.login, channelContainer);
        authHandler.init(baseServer.netServer.dispatch);
        authModule.init();
    }

    public void run() {
        baseServer.run();
    }
}
