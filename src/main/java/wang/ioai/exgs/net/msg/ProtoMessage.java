package wang.ioai.exgs.net.msg;

import com.google.protobuf.Message;

public class ProtoMessage {
    public Message proto;
    public short opcode;

    public ProtoMessage(Message proto, short opcode) {
        this.proto = proto;
        this.opcode = opcode;
    }
}
