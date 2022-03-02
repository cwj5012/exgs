package wang.ioai.exgs.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.data.GData;
import wang.ioai.exgs.data.GDefine;
import wang.ioai.exgs.net.codec.ProtoEncoder;
import wang.ioai.exgs.net.handler.ProtoHandler;

public final class NetServer {
    private static final Logger logger = LoggerFactory.getLogger(NetServer.class);

    private int port;
    private int backlog;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void init() {
        port = Integer.parseInt(GData.config.server.manager.port);
        backlog = GData.config.server.manager.backlog;
    }

    public void start() {
        // Configure the server.
        bossGroup = new NioEventLoopGroup(4);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, backlog)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            logger.debug("new {}", p.channel().id().asShortText());
                            GData.channels.put(p.channel().id(),
                                    new GData.ChannelInfo(p.channel(), System.currentTimeMillis()));
                            p.addLast(new LengthFieldBasedFrameDecoder(GDefine.byteOrder, GDefine.maxFrameLength,
                                    GDefine.lengthFieldOffset, GDefine.lengthFieldLength, GDefine.lengthAdjustment,
                                    GDefine.initialBytesToStrip, GDefine.failFast));
                            // p.addLast(new ProtoDecoder());
                            p.addLast(new ProtoEncoder());
                            p.addLast(new ProtoHandler());
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
