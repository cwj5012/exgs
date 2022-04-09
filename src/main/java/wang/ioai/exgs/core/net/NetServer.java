package wang.ioai.exgs.core.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wang.ioai.exgs.core.config.ServerBaseConfig;
import wang.ioai.exgs.core.data.GDefine;
import wang.ioai.exgs.core.net.codec.ProtoEncoder;
import wang.ioai.exgs.core.net.handler.ProtoHandler;
import wang.ioai.exgs.core.data.GData;

public final class NetServer {
    private static final Logger logger = LoggerFactory.getLogger(NetServer.class);

    private String host;                    // 服务器监听地址
    private int port;                       // 服务器监听端口
    private int backlog;                    // 完成队列长度
    private int bossThreadNum;              // netty accept 线程数
    private int workerThreadNum;            // netty worker 线程数

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture f;

    public Dispatch dispatch;

    public void init(ServerBaseConfig config) {
        host = config.ip;
        port = config.port;
        backlog = config.backlog;
        bossThreadNum = config.bossThreadNum;
        workerThreadNum = config.workerThreadNum;
        dispatch = new Dispatch();
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
                            ChannelPipeline pipe = ch.pipeline();
                            logger.debug("accept: {}", pipe.channel().id().asShortText());
                            GData.channels.put(pipe.channel().id(), new GData.ChannelInfo(pipe.channel(), System.currentTimeMillis()));
                            // 解码
                            pipe.addLast(new LengthFieldBasedFrameDecoder(GDefine.byteOrder, GDefine.maxFrameLength,
                                    GDefine.lengthFieldOffset, GDefine.lengthFieldLength, GDefine.lengthAdjustment,
                                    GDefine.initialBytesToStrip, GDefine.failFast));
                            // 编码
                            pipe.addLast(new ProtoEncoder());
                            // 消息处理
                            var protoHandler = new ProtoHandler();
                            protoHandler.setDispatch(dispatch);
                            pipe.addLast(protoHandler);
                        }
                    });
            f = b.bind(host, port).sync();
            logger.info("listen {}:{}", host, port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            shutdown();
        }
    }

    public void close(){
        f.channel().close();
    }

    private void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info("netty group shutdown.");
    }
}
