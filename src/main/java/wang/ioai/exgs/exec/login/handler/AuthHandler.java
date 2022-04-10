package wang.ioai.exgs.exec.login.handler;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.common.pb.ProtoDebug;
import wang.ioai.exgs.core.data.Opcode;
import wang.ioai.exgs.core.net.Dispatch;
import wang.ioai.exgs.core.net.msg.NetMessage;
import wang.ioai.exgs.core.net.msg.ProtoMessage;
import wang.ioai.exgs.exec.game.INetMessage;
import wang.ioai.exgs.exec.login.LoginBoot;

public class AuthHandler implements INetMessage {
    private static final Logger logger = LoggerFactory.getLogger(AuthHandler.class);

    private LoginBoot loginBoot;

    public AuthHandler(LoginBoot loginBoot) {
        this.loginBoot = loginBoot;
    }

    public void init(Dispatch dispatch) {
        dispatch.registMessage1(Opcode.GmLoginReq, this);
    }

    @Override
    public void onMessage(NetMessage msg) throws Exception {
        switch (msg.getOpcode()) {
            case Opcode.GmLoginReq -> {
                var pb = ProtoDebug.GmLoginReq.parseFrom(msg.getBuf());
                var col = loginBoot.authModule.mongoX.db.getCollection("gm").find(new Document("token", pb.getToken())).first();
                logger.warn("{}", col);
                if (col != null) {
                    var spb = ProtoDebug.GmLoginRet.newBuilder().setId(col.getInteger("id")).build();
                    var smsg = new ProtoMessage(spb, Opcode.GmLoginRet);
                    msg.getChannel().writeAndFlush(smsg);
                }
            }
            default -> logger.warn("Unexpected value: {}", msg.getOpcode());
        }
    }
}
