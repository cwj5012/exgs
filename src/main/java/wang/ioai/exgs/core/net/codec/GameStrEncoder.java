package wang.ioai.exgs.core.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class GameStrEncoder extends MessageToByteEncoder<String> {
    private static final Logger logger = LoggerFactory.getLogger(GameStrEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        if (msg == null) {
            throw new Exception("msg is null");
        }

        byte[] bodyBytes = msg.getBytes(StandardCharsets.UTF_8);
        // 注意，协议消息长度为 2 字节，这里要用 short 类型，大端序
        out.writeShort(bodyBytes.length);
        out.writeBytes(bodyBytes);
        // logger.debug("{}", bodyBytes.length);
    }
}
