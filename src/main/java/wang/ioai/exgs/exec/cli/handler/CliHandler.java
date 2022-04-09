package wang.ioai.exgs.exec.cli.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.core.data.Opcode;
import wang.ioai.exgs.core.net.Dispatch;
import wang.ioai.exgs.core.net.msg.NetMessage;
import wang.ioai.exgs.core.net.msg.ProtoMessage;
import wang.ioai.exgs.exec.game.INetMessage;
import wang.ioai.exgs.common.pb.ProtoDebug;

public class CliHandler implements INetMessage {
    private static final Logger logger = LoggerFactory.getLogger(CliHandler.class);

    public void init(Dispatch dispatch) {
        dispatch.registMessage1(Opcode.Ping, this);
        dispatch.registMessage1(Opcode.Pong, this);
        dispatch.registMessage1(Opcode.CmdReq, this);
        dispatch.registMessage1(Opcode.CmdRet, this);
        dispatch.registMessage1(Opcode.EchoReq, this);
        dispatch.registMessage1(Opcode.EchoRet, this);
    }

    @Override
    public void onMessage(NetMessage msg) throws Exception {
        switch (msg.getOpcode()) {
            case Opcode.EchoReq -> {
                var rpb = ProtoDebug.Echo.parseFrom(msg.getBuf());
                var spb = ProtoDebug.Echo.newBuilder().setText(rpb.getText()).build();
                var smsg = new ProtoMessage(spb, Opcode.EchoRet);
                msg.getChannel().writeAndFlush(smsg);
            }
            case Opcode.EchoRet -> {
                var rpb = ProtoDebug.Echo.parseFrom(msg.getBuf());
                logger.debug("echo: {}", rpb.getText());
            }
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
            case Opcode.CmdReq -> {
                var pb = ProtoDebug.Cmd.parseFrom(msg.getBuf());
                var cmd = pb.getText();
                if (cmd.equals("exit")) {
                    logger.info("receive command exit, server will stop now.");
                    // GData.netServer.close();
                }
            }
            case Opcode.CmdRet -> {
                var pb = ProtoDebug.Cmd.parseFrom(msg.getBuf());
                logger.debug("/ ret: {}", pb.getText());
            }
            default -> {
                logger.warn("Unexpected value: {}", msg.getOpcode());
            }
        }
    }
}
