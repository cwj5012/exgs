package wang.ioai.exgs.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.net.msg.ProtoMessage;

public class ProtoEncoder extends MessageToByteEncoder<ProtoMessage> {
    private static final Logger logger = LoggerFactory.getLogger(ProtoEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtoMessage msg, ByteBuf out) {
        try {
            if (msg == null) {
                logger.warn("encode message is null.");
                return;
            }
            // logger.debug("send: {}", msg.proto.toByteArray().length + 4);
            var buf = msg.proto.toByteArray();
            out.writeShort(buf.length);
            out.writeShort(msg.opcode);
            out.writeBytes(buf);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
