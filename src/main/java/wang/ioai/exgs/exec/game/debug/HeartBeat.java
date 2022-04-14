package wang.ioai.exgs.exec.game.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.core.data.GData;
import wang.ioai.exgs.core.data.Opcode;
import wang.ioai.exgs.exec.game.INetMessage;
import wang.ioai.exgs.exec.game.Player;
import wang.ioai.exgs.core.net.msg.NetMessage;
import wang.ioai.exgs.core.net.msg.ProtoMessage;
import wang.ioai.exgs.common.pb.ProtoDebug;

public class HeartBeat implements INetMessage {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeat.class);
    private final Player player;
    private final int timeout = 5000; // 超时，毫秒
    public long last_time = 0; // 上次心跳包时间，毫秒

    public HeartBeat(Player player) {
        this.player = player;
    }

    @Override
    public void onMessage(NetMessage msg) throws Exception {
        switch (msg.getOpcode()) {
            case Opcode.HeartBeat -> {
                last_time = java.lang.System.currentTimeMillis();
                // var pb = ProtoDebug.HeartBeat.parseFrom(msg.getBuf());
                // logger.debug("HeartBeat: {}", pb.getTime());
                var spb = ProtoDebug.HeartBeat.newBuilder().setTime(last_time).build();
                var smsg = new ProtoMessage(spb, Opcode.HeartBeat);
                msg.getChannel().writeAndFlush(smsg);
            }
            default -> {
            }
        }
    }

    public void check(long current_time) {
        if (current_time - last_time >= timeout) {
            // 心跳包超时
            if (player.auth == 1) {
                // logger.warn("{} heart beat time out.", player.toString());
                // 移除连接
                var channel = GData.userToChannel.remove(player.info.id);
                if (channel != null) {
                    GData.channelToUser.remove(channel.id());
                    GData.channels.remove(channel.id());
                    logger.warn("player {} close socket {}.", player.info.id, channel.id().asShortText());
                    channel.close();
                }
            }
        }
    }
}
