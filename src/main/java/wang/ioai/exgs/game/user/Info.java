package wang.ioai.exgs.game.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.game.INetMessage;
import wang.ioai.exgs.game.Player;
import wang.ioai.exgs.net.msg.NetMessage;

public class Info implements INetMessage {
    private static final Logger logger = LoggerFactory.getLogger(Info.class);
    private final Player player;

    public long id;
    public String name;

    public Info(Player player) {
        this.player = player;
    }

    @Override
    public void onMessage(NetMessage msg) throws Exception {

    }
}
