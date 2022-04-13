package wang.ioai.exgs.exec.game.scene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.exec.game.INetMessage;
import wang.ioai.exgs.exec.game.Player;
import wang.ioai.exgs.core.net.msg.NetMessage;

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
