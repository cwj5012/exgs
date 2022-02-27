package wang.ioai.exgs.game;

import wang.ioai.exgs.net.msg.NetMessage;

public interface INetMessage {
    void onMessage(NetMessage msg) throws Exception;
}
