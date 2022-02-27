package wang.ioai.exgs.net.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(EchoClientHandler.class);

    private int recvByte;
    private int sendByte;
    private int recvCount;
    private int sendCount;

    private final ByteBuf firstMessage;
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    /**
     * Creates a client-side handler.
     */
    public EchoClientHandler() {
        firstMessage = Unpooled.buffer(SIZE);
        for (int i = 0; i < firstMessage.capacity(); i++) {
            firstMessage.writeByte((byte) i);
        }
    }

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
        recvByte += msg.readableBytes();
        ctx.write(msg.retain());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // logger.debug(Thread.currentThread().getStackTrace()[1].getMethodName());
        recvCount++;
        if (recvCount % 10000 == 0) {
            logger.debug("{}", recvCount);
        }
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage());
        ctx.close();
    }
}
