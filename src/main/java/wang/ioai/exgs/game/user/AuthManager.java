package wang.ioai.exgs.game.user;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.core.data.GData;
import wang.ioai.exgs.core.data.Opcode;
import wang.ioai.exgs.game.INetMessage;
import wang.ioai.exgs.game.Player;
import wang.ioai.exgs.game.PlayerContainer;
import wang.ioai.exgs.core.net.Dispatch;
import wang.ioai.exgs.core.net.msg.NetMessage;
import wang.ioai.exgs.core.net.msg.ProtoMessage;
import wang.ioai.exgs.core.pb.ProtoDebug;

import java.util.HashMap;

public class AuthManager implements INetMessage {
    private static final Logger logger = LoggerFactory.getLogger(AuthManager.class);

    private static final HashMap<String, String> password = new HashMap<>();

    public AuthManager() {
        Dispatch.registMessage(Opcode.AuthUserReq, this);
    }

    @Override
    public void onMessage(NetMessage msg) throws Exception {
        switch (msg.getOpcode()) {
            case Opcode.AuthUserReq -> {
                var pb = ProtoDebug.AuthUserReq.parseFrom(msg.getBuf());
                // logger.debug("{}", pb.toString());
                var col = GData.db.getCollection("auth")
                        .find(new Document("name", pb.getUsername())).first();
                // logger.warn("{}", col);
                if (col == null) {
                    var spb = ProtoDebug.AuthUserRet.newBuilder()
                            .setResult(1)
                            .setMessge("user name or password is error.")
                            .build();
                    var smsg = new ProtoMessage(spb, Opcode.AuthUserRet);
                    msg.getChannel().writeAndFlush(smsg);
                    return;
                }
                var spb = ProtoDebug.AuthUserRet.newBuilder()
                        .setResult(0)
                        .setUsername(col.getString("name"))
                        .setUid(col.getLong("id"))
                        .setMessge("auth ok.")
                        .build();
                var smsg = new ProtoMessage(spb, Opcode.AuthUserRet);
                msg.getChannel().writeAndFlush(smsg);

                var ch = GData.channels.get(msg.getChannel().id());
                if (ch != null) {
                    var uid = col.getLong("id");
                    var userChannel = GData.userToChannel.get(uid);
                    if (userChannel != null) {
                        // 断开旧链接
                        GData.channelToUser.remove(userChannel.id());
                        logger.warn("close user old channel {}.", userChannel.id().asShortText());
                        userChannel.close();
                        GData.userToChannel.remove(uid);
                    }
                    // 已认证连接加入
                    var player = PlayerContainer.players.get(uid);
                    if (player == null) {
                        // 用户未缓存
                        player = new Player();
                        player.auth = 1;
                        player.info.id = uid;
                        player.info.name = col.getString("name");
                        player.heartBeat.last_time = System.currentTimeMillis();
                        PlayerContainer.players.put(uid, player);
                    }
                    player.auth = 1;
                    player.heartBeat.last_time = System.currentTimeMillis();

                    GData.userToChannel.put(uid, ch.channel);
                    GData.channelToUser.put(ch.channel.id(), player);
                    GData.channels.remove(msg.getChannel().id());
                    logger.debug("user {} id {} channel {} auth ok.",
                            col.getString("name"),
                            col.getLong("id"),
                            msg.getChannel().id().asShortText());
                } else {
                    logger.warn("channel {} not found.", msg.getChannel().id().asShortText());
                }
            }
            default -> {
                logger.warn("Unexpected value: {}", msg.getOpcode());
            }
        }
    }
}
