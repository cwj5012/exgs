package wang.ioai.exgs.core.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.core.data.GDefine;

public class GameDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger logger = LoggerFactory.getLogger(GameDecoder.class);

    public GameDecoder() {
        // 大端序
        super(GDefine.byteOrder, GDefine.maxFrameLength, GDefine.lengthFieldOffset, GDefine.lengthFieldLength,
                GDefine.lengthAdjustment, GDefine.initialBytesToStrip, GDefine.failFast);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // logger.debug(ByteBufUtil.prettyHexDump(in));
        return super.decode(ctx, in);
    }
}
