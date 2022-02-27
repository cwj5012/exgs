package wang.ioai.exgs.game.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.data.GData;
import wang.ioai.exgs.data.Opcode;
import wang.ioai.exgs.game.INetMessage;
import wang.ioai.exgs.net.Dispatch;
import wang.ioai.exgs.net.msg.NetMessage;

public class HeartBeatHandle implements INetMessage {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatHandle.class);

    public void init() {
        Dispatch.registMessage(Opcode.HeartBeat, this);
    }

    @Override
    public void onMessage(NetMessage msg) throws Exception {
        var player = GData.channelToUser.get(msg.getChannel().id());
        if (player != null && player.auth == 1) {
            player.heartBeat.onMessage(msg);
        }
    }
}
