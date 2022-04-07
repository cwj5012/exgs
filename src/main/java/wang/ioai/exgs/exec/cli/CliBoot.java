package wang.ioai.exgs.exec.cli;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wang.ioai.exgs.exec.cli.handler.CliHandler;
import wang.ioai.exgs.core.data.GDefine;
import wang.ioai.exgs.core.data.Opcode;
import wang.ioai.exgs.core.net.Dispatch;
import wang.ioai.exgs.core.net.codec.ProtoEncoder;
import wang.ioai.exgs.core.net.handler.ProtoHandler;
import wang.ioai.exgs.core.net.msg.ProtoMessage;
import wang.ioai.exgs.core.define.EStatus;
import wang.ioai.exgs.common.pb.ProtoDebug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CliBoot {
    private static final Logger logger = LoggerFactory.getLogger(CliBoot.class);

    public EStatus status;
    private ChannelPipeline pipe;
    private Dispatch dispatch;
    private CliHandler cliHandler;
    private static int pingId;

    public void init() {
        status = EStatus.init;
        dispatch = new Dispatch();
        cliHandler = new CliHandler();

        cliHandler.init(dispatch);
    }

    public void run() {
        status = EStatus.running;

        // 线程 1 阻塞监听命令行输入
        // 线程 2 连接到服务器
        EventLoopGroup group = new NioEventLoopGroup(2);

        try {
            Bootstrap b = new Bootstrap();
            group.execute(() -> {
                while (true) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    try {
                        String s = br.readLine();
                        if (s.indexOf("ping") == 0) {
                            var proto = ProtoDebug.Ping.newBuilder().setId(++pingId).build();
                            var msg = new ProtoMessage(proto, Opcode.Ping);
                            pipe.channel().writeAndFlush(msg);
                            continue;
                        }
                        if (s.indexOf("echo ") == 0) {
                            var proto = ProtoDebug.Echo.newBuilder().setText(s.substring(5)).build();
                            var msg = new ProtoMessage(proto, Opcode.EchoReq);
                            pipe.channel().writeAndFlush(msg);
                            continue;
                        }
                        if (s.indexOf("cmd ") == 0) {
                            var proto = ProtoDebug.Cmd.newBuilder().setText(s.substring(4)).build();
                            var msg = new ProtoMessage(proto, Opcode.CmdReq);
                            pipe.channel().writeAndFlush(msg);
                            continue;
                        }
                        logger.warn("unknow command.");
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        return;
                    }
                }
            });
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            pipe = ch.pipeline();
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
            logger.info("connect to {}:{}", "localhost", 9001);
            ChannelFuture f = b.connect("127.0.0.1", 9001).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            group.shutdownGracefully();
        }
    }
}
