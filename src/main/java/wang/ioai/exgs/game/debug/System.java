package wang.ioai.exgs.game.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.core.data.GData;
import wang.ioai.exgs.core.data.Opcode;
import wang.ioai.exgs.game.INetMessage;
import wang.ioai.exgs.game.Player;
import wang.ioai.exgs.core.net.msg.NetMessage;
import wang.ioai.exgs.core.net.msg.ProtoMessage;
import wang.ioai.exgs.core.pb.ProtoDebug;

public class System implements INetMessage {
    private static final Logger logger = LoggerFactory.getLogger(System.class);
    private final Player player;

    public System(Player player) {
        this.player = player;
    }

    @Override
    public void onMessage(NetMessage msg) throws Exception {
        switch (msg.getOpcode()) {
            case Opcode.Hello -> {
                var pb = ProtoDebug.Hello.parseFrom(msg.getBuf());
                logger.debug("hello: {}", pb.getText());
            }
            case Opcode.Echo -> {
                var rpb = ProtoDebug.Echo.parseFrom(msg.getBuf());
                // logger.debug("echo: {}", rpb.getText());
                var spb = ProtoDebug.Echo.newBuilder().setText(rpb.getText()).build();
                var smsg = new ProtoMessage(spb, Opcode.Echo);
                msg.getChannel().writeAndFlush(smsg);
            }
            case Opcode.Ping -> {
                var pb = ProtoDebug.Ping.parseFrom(msg.getBuf());
                logger.debug("ping: {}", pb.getId());
            }
            case Opcode.Pong -> {
                var pb = ProtoDebug.Pong.parseFrom(msg.getBuf());
                logger.debug("pong: {}", pb.getId());
            }
            case Opcode.Cmd -> {
                var pb = ProtoDebug.Cmd.parseFrom(msg.getBuf());
                var cmd = pb.getText();
                if (cmd.equals("exit")) {
                    logger.info("receive command exit, server will stop now.");
                    GData.netServer.close();
                }
            }
            case Opcode.EnterSceneReq -> {
                var pb = ProtoDebug.EnterSceneReq.parseFrom(msg.getBuf());
                var sceneId = pb.getId();
                // if (player.scene.currentId == sceneId) {
                //     logger.warn("player is in the scene {}, can't enter again.", sceneId);
                //     return;
                // }
                player.scene.currentId = sceneId;
                // 进入场景 ok
                var spb = ProtoDebug.EnterSceneRet.newBuilder().setId(sceneId).build();
                var smsg = new ProtoMessage(spb, Opcode.EnterSceneRet);
                msg.getChannel().writeAndFlush(smsg);
                // 更新场景信息
                GData.sceneManager.add(player.info.id);
            }
            case Opcode.SceneInfoReq -> {
                // 同步场景信息
                var pb = ProtoDebug.EnterSceneReq.parseFrom(msg.getBuf());
                var sceneId = pb.getId();
                var spb = ProtoDebug.SceneInfoRet.newBuilder()
                        .setId(sceneId).addAllInfo(GData.sceneManager.getInfo()).build();
                var smsg = new ProtoMessage(spb, Opcode.SceneInfoRet);
                msg.getChannel().writeAndFlush(smsg);
            }
            case Opcode.EnterSceneRet -> {

            }
            case Opcode.MoveReq -> {
                var pb = ProtoDebug.MoveReq.parseFrom(msg.getBuf());
                logger.debug(pb.toString());
                {
                    // 回复玩家本身
                    var info = GData.sceneManager.update(player.info.id, pb.getX(), pb.getY());
                    var spb = ProtoDebug.MoveRet.newBuilder().setX(info.x).setY(info.y).build();
                    var smsg = new ProtoMessage(spb, Opcode.MoveRet);
                    msg.getChannel().writeAndFlush(smsg);
                }

                {
                    // 广播
                    var spb = ProtoDebug.MoveSync.newBuilder()
                            .setId(1).addAllInfo(GData.sceneManager.getInfo()).build();
                    var smsg = new ProtoMessage(spb, Opcode.MoveSync);
                    for (var item : GData.userToChannel.entrySet()) {
                        item.getValue().writeAndFlush(smsg);
                    }
                }
            }
            default -> {
                logger.warn("Unexpected value: {}", msg.getOpcode());
            }
        }
    }
}
