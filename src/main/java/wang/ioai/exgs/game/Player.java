package wang.ioai.exgs.game;

import wang.ioai.exgs.game.debug.HeartBeat;
import wang.ioai.exgs.game.debug.System;
import wang.ioai.exgs.game.scene.Scene;
import wang.ioai.exgs.game.user.Info;
import wang.ioai.exgs.net.msg.NetMessage;

public class Player {
    public System system;
    public Info info;
    public HeartBeat heartBeat;
    public Scene scene;

    public int auth; // 0 未验证，1 已验证

    public Player()
    {
        system = new System(this);
        info = new Info(this);
        heartBeat = new HeartBeat(this);
        scene = new Scene(this);
    }

    public void onMessage(NetMessage msg) throws Exception {

    }
}
