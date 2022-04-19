package wang.ioai.exgs.exec.game;

import wang.ioai.exgs.core.net.msg.NetMessage;

public interface INetMessage {
    void onMessage(NetMessage msg) throws Exception;
}
