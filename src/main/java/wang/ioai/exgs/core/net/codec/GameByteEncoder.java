package wang.ioai.exgs.core.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameByteEncoder extends MessageToByteEncoder<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(GameStrEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        if (msg == null) {
            throw new Exception("msg is null");
        }
        // logger.debug(ByteBufUtil.prettyHexDump(msg));

        byte[] bodyBytes =  new byte[msg.readableBytes()];
        msg.readBytes(bodyBytes);
        // int readerIndex = msg.readerIndex();
        // msg.getBytes(readerIndex, bodyBytes);

        // 注意，协议消息长度为 2 字节，这里要用 short 类型，大端序
        out.writeShort(bodyBytes.length);
        out.writeBytes(bodyBytes);
    }
}

