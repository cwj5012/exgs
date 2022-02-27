package wang.ioai.exgs.net.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(GameServerHandler.class);

    private int recvByte;
    private int sendByte;
    private int recvCount;
    private int sendCount;

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
        // logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName());
        // logger.debug("\n" + ByteBufUtil.prettyHexDump(msg));
        ctx.write(msg.retain());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName());
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        logger.error(cause.getMessage());
        ctx.close();
    }
}
