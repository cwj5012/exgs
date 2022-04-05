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

    private String host;                    // 服务器监听地址
    private int port;                       // 服务器监听端口
    private int backlog;                    // 完成队列长度
    private int bossThreadNum;              // netty accept 线程数
    private int workerThreadNum;              // netty worker 线程数

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void init() {
        host = GData.config.server.manager.ip;
        port = Integer.parseInt(GData.config.server.manager.port);
        backlog = GData.config.server.manager.backlog;
        bossThreadNum = GData.config.server.manager.bossThreadNum;
        workerThreadNum = GData.config.server.manager.workerThreadNum;
    }

    public void start() {
        bossGroup = new NioEventLoopGroup(bossThreadNum);
        workerGroup = new NioEventLoopGroup(workerThreadNum);
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
                            p.addLast(new ProtoEncoder());
                            p.addLast(new ProtoHandler());
                        }
                    });
            ChannelFuture f = b.bind(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            close();
        }
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
