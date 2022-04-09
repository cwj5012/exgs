package wang.ioai.exgs.exec.game.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.core.data.GData;
import wang.ioai.exgs.core.data.Opcode;
import wang.ioai.exgs.exec.game.INetMessage;
import wang.ioai.exgs.core.net.Dispatch;
import wang.ioai.exgs.core.net.msg.NetMessage;

public class SystemHandle implements INetMessage {
    private static final Logger logger = LoggerFactory.getLogger(SystemHandle.class);

    public void init() {
        Dispatch.registMessage(Opcode.Hello, this);
        Dispatch.registMessage(Opcode.Ping, this);
        Dispatch.registMessage(Opcode.Pong, this);
        Dispatch.registMessage(Opcode.CmdReq, this);
        Dispatch.registMessage(Opcode.EchoReq, this);
        Dispatch.registMessage(Opcode.EnterSceneReq, this);
        Dispatch.registMessage(Opcode.ExitSceneReq, this);
        Dispatch.registMessage(Opcode.MoveReq, this);
        Dispatch.registMessage(Opcode.SceneInfoReq, this);
    }

    @Override
    public void onMessage(NetMessage msg) throws Exception {
        var player = GData.channelToUser.get(msg.getChannel().id());
        if (player != null && player.auth == 1) {
            player.system.onMessage(msg);
        } else {
            logger.warn("player not found from channel {}", msg.getChannel().id().asShortText());
        }
    }
}
