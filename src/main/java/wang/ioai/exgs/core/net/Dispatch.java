package wang.ioai.exgs.core.net;

import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue;
import wang.ioai.exgs.core.data.GDefine;
import wang.ioai.exgs.exec.game.INetMessage;
import wang.ioai.exgs.core.net.msg.NetMessage;

import java.util.HashMap;

public class Dispatch {
    private final static HashMap<Short, INetMessage> messageMap = new HashMap<>();
    public MpscArrayQueue<NetMessage> queue;

    public Dispatch() {
        queue = new MpscArrayQueue<>(GDefine.messageQueueSize);
    }

    public static void registMessage(short opcode, INetMessage module) {
        messageMap.put(opcode, module);
    }

    public static void removeMessage(short opcode, INetMessage module) {
        messageMap.remove(opcode);
    }

    public void registMessage1(short opcode, INetMessage module) {
        messageMap.put(opcode, module);
    }

    public void removeMessage1(short opcode, INetMessage module) {
        messageMap.remove(opcode);
    }

    public void onMessage(NetMessage msg) throws Exception {
        if (messageMap.containsKey(msg.getOpcode())) {
            messageMap.get(msg.getOpcode()).onMessage(msg);
        }
    }
}
