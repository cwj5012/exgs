package wang.ioai.exgs.core.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import wang.ioai.exgs.core.data.GData;
import wang.ioai.exgs.exec.game.Player;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelContainer {
    // 未认证连接，netty channel id -> netty channel
    public final ConcurrentHashMap<ChannelId, GData.ChannelInfo> channels = new ConcurrentHashMap<>();
    // 已认证连接，user id -> netty channel
    public final ConcurrentHashMap<Long, Channel> userToChannel = new ConcurrentHashMap<>();
    // 已认证连接，user id -> netty channel
    public final ConcurrentHashMap<ChannelId, Player> channelToUser = new ConcurrentHashMap<>();

    public String DebugChannels() {
        StringBuilder sb = new StringBuilder("\n");
        for (var entry : channels.entrySet()) {
            sb.append(entry.getValue().channel).append("\n");
        }
        return sb.toString();
    }
}
