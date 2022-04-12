package wang.ioai.exgs.core.net.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.common.pb.ProtoDebug;
import wang.ioai.exgs.core.data.Opcode;
import wang.ioai.exgs.core.net.Dispatch;
import wang.ioai.exgs.core.net.NetServer;
import wang.ioai.exgs.core.net.msg.NetMessage;
import wang.ioai.exgs.core.net.msg.ProtoMessage;
import wang.ioai.exgs.core.system.NodeInfo;
import wang.ioai.exgs.exec.game.INetMessage;
import wang.ioai.exgs.exec.master.MasterBoot;

public class BaseHandler implements INetMessage {
    private static final Logger logger = LoggerFactory.getLogger(BaseHandler.class);

    private final NetServer netServer;
    private NodeInfo nodeInfo;

    public BaseHandler(NetServer netServer) {
        this.netServer = netServer;

    }

    public BaseHandler(NetServer netServer, NodeInfo nodeInfo) {
        this.netServer = netServer;
        this.nodeInfo = nodeInfo;
    }

    public void init(Dispatch dispatch) {
        dispatch.registMessage1(Opcode.Ping, this);
        dispatch.registMessage1(Opcode.Pong, this);
        dispatch.registMessage1(Opcode.EchoReq, this);
        dispatch.registMessage1(Opcode.CmdReq, this);
    }

    @Override
    public void onMessage(NetMessage msg) throws Exception {
        switch (msg.getOpcode()) {
            case Opcode.Ping -> {
                var rpb = ProtoDebug.Ping.parseFrom(msg.getBuf());
                var spb = ProtoDebug.Pong.newBuilder().setId(rpb.getId()).build();
                var smsg = new ProtoMessage(spb, Opcode.Pong);
                msg.getChannel().writeAndFlush(smsg);
            }
            case Opcode.Pong -> {
                var pb = ProtoDebug.Pong.parseFrom(msg.getBuf());
                logger.debug("pong: {}", pb.getId());
            }
            case Opcode.EchoReq -> {
                var rpb = ProtoDebug.Echo.parseFrom(msg.getBuf());
                // logger.debug("echo: {}", rpb.getText());
                var spb = ProtoDebug.Echo.newBuilder().setText(rpb.getText()).build();
                var smsg = new ProtoMessage(spb, Opcode.EchoRet);
                msg.getChannel().writeAndFlush(smsg);
            }
            case Opcode.CmdReq -> {
                var pb = ProtoDebug.Cmd.parseFrom(msg.getBuf());
                var cmd = pb.getText();
                if (cmd.equals("exit")) {
                    logger.info("receive command exit, server will stop now.");
                    netServer.close();
                }
                if (cmd.equals("status")) {
                    var spb = ProtoDebug.Cmd.newBuilder().setText(nodeInfo.toString()).build();
                    var smsg = new ProtoMessage(spb, Opcode.CmdRet);
                    msg.getChannel().writeAndFlush(smsg);
                }
                if (cmd.equals("channels")) {
                    var spb = ProtoDebug.Cmd.newBuilder().setText(netServer.channelContainer.DebugChannels()).build();
                    var smsg = new ProtoMessage(spb, Opcode.CmdRet);
                    msg.getChannel().writeAndFlush(smsg);
                }
            }
            default -> logger.warn("Unexpected value: {}", msg.getOpcode());
        }
    }
}
