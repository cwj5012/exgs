package wang.ioai.exgs.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.data.GDefine;
import wang.ioai.exgs.net.msg.NetMessage;

public class ProtoDecoder extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(ProtoDecoder.class);

    public ProtoDecoder() {
        // super(GDefine.byteOrder, GDefine.maxFrameLength, GDefine.lengthFieldOffset, GDefine.lengthFieldLength,
        //         GDefine.lengthAdjustment, GDefine.initialBytesToStrip, GDefine.failFast);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        // logger.debug("\n" + ByteBufUtil.prettyHexDump(msg));

        // 截断后数据
        // logger.debug("\n" + ByteBufUtil.prettyHexDump(in));

        if (msg.readableBytes() < GDefine.initialBytesToStrip) {
            logger.warn("byte num {} is not small.", msg.readableBytes());
        }

        NetMessage netMsg = new NetMessage();
        netMsg.setOpcode(msg.readShort());
        byte[] buf = new byte[msg.readableBytes()];
        msg.readBytes(buf);
        netMsg.setBuf(buf);
        netMsg.setChannel(ctx.channel());
        msg.release();
        logger.debug("{}", netMsg);
        //将消息传递下去，或者在这里将消息发布出去
        ctx.fireChannelRead(netMsg);

        // 业务数据
        // logger.debug(msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("{}", cause.getMessage());
    }

    // @Override
    // protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
    //     // 原始数据
    //     // logger.debug("\n" + ByteBufUtil.prettyHexDump(in));
    //     var din = (ByteBuf) super.decode(ctx, in);
    //     if (din == null) {
    //         logger.warn("decode in is null");
    //         logger.debug("\n" + ByteBufUtil.prettyHexDump(din));
    //         return null;
    //     }
    //
    //     // 截断后数据
    //     // logger.debug("\n" + ByteBufUtil.prettyHexDump(in));
    //
    //     if (din.readableBytes() < GDefine.initialBytesToStrip) {
    //         logger.warn("byte num {} is not small.",din.readableBytes());
    //     }
    //
    //     NetMessage msg = new NetMessage();
    //     msg.setOpcode(din.readShort());
    //     byte[] buf = new byte[din.readableBytes()];
    //     din.readBytes(buf);
    //     msg.setBuf(buf);
    //     msg.setChannel(ctx.channel());
    //     din.release();
    //     // 业务数据
    //     // logger.debug(msg.toString());
    //
    //     return msg;
    // }
}
