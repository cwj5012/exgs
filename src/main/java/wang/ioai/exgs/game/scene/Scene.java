package wang.ioai.exgs.game.scene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.game.INetMessage;
import wang.ioai.exgs.game.Player;
import wang.ioai.exgs.net.msg.NetMessage;

public class Scene implements INetMessage {
    private static final Logger logger = LoggerFactory.getLogger(Scene.class);
    private final Player player;

    public int currentId;

    public Scene(Player player) {
        this.player = player;
    }

    @Override
    public void onMessage(NetMessage msg) throws Exception {

    }
}
