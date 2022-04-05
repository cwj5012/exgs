package wang.ioai.exgs.core.net.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.core.data.GDefine;
import wang.ioai.exgs.core.data.GData;
import wang.ioai.exgs.core.net.msg.NetMessage;

public class ProtoHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(ProtoHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName());
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (msg.readableBytes() < GDefine.initialBytesToStrip) {
            logger.warn("byte num {} is not small.", msg.readableBytes());
        }

        NetMessage netMsg = new NetMessage();
        netMsg.setOpcode(msg.readShort());
        byte[] buf = new byte[msg.readableBytes()];
        msg.readBytes(buf);
        netMsg.setBuf(buf);
        netMsg.setChannel(ctx.channel());
        // 导致异常
        // msg.release();

        // 收包个数统计
        ++GData.recvCount;
        if (GData.recvCount % 10000 == 0) {
            logger.debug("{} {}/s", GData.recvCount,
                    (GData.recvCount - GData.recvCountLast) * 1000L / (System.currentTimeMillis() - GData.lastTime));
            GData.lastTime = System.currentTimeMillis();
            GData.recvCountLast = GData.recvCount;
        }
        // 直接响应消息
        GData.dispatch.onMessage(netMsg);

        // 时间片响应消息
        // if (!GData.dispatch.queue.offer(msg)) {
        //     logger.error("queue offer failed.");
        // }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("{}", cause.getMessage());
        ctx.close();
    }
}
