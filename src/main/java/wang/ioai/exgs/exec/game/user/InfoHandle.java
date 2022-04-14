package wang.ioai.exgs.exec.game.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.core.data.GData;
import wang.ioai.exgs.exec.game.INetMessage;
import wang.ioai.exgs.core.net.msg.NetMessage;

public class InfoHandle  implements INetMessage {
    private static final Logger logger = LoggerFactory.getLogger(InfoHandle.class);

    public void init() {
        // Dispatch.registMessagePlayer(Opcode.HeartBeat, this);
    }

    @Override
    public void onMessage(NetMessage msg) throws Exception {
        var player = GData.channelToUser.get(msg.getChannel().id());
        if (player != null && player.auth == 1) {
            player.info.onMessage(msg);
        } else {
            logger.warn("player not found from channel {}", msg.getChannel().id().asShortText());
        }
    }
}

