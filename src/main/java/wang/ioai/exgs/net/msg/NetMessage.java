package wang.ioai.exgs.net.msg;

import io.netty.channel.Channel;

import java.util.Arrays;

/**
 * 接收到的网络消息
 */
public class NetMessage {
    private short opcode;
    private byte[] buf;
    private Channel channel;

    public short getOpcode() {
        return opcode;
    }

    public void setOpcode(short opcode) {
        this.opcode = opcode;
    }

    public byte[] getBuf() {
        return buf;
    }

    public void setBuf(byte[] buf) {
        this.buf = buf;
    }

    @Override
    public String toString() {
        return "NetMessage{" +
                "opcode=" + opcode +
                ", buf=" + Arrays.toString(buf) +
                '}';
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
