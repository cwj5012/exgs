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

    public String[] args;
    public EStatus status;
    private ChannelPipeline pipe;
    private Dispatch dispatch;
    private CliHandler cliHandler;
    private static int pingId;

    private String host = "localhost";
    private int port = 9001;

    EventLoopGroup group;
    Bootstrap bs;

    public CliBoot(String[] args) {
        this.args = args;
        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }
    }

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
        group = new NioEventLoopGroup(2);
        bs = new Bootstrap();

        runCli();
        runConnect();
    }

    private void runCli() {
        group.execute(() -> {
            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                try {
                    var s = br.readLine();
                    if (s.isEmpty()) {
                        continue;
                    }
                    if (s.indexOf("hello") == 0) {
                        var proto = ProtoDebug.Hello.newBuilder().setText(s.substring(6)).build();
                        var msg = new ProtoMessage(proto, Opcode.Hello);
                        pipe.channel().writeAndFlush(msg);
                        continue;
                    }
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
                    if (s.indexOf("/") == 0) {
                        var proto = ProtoDebug.Cmd.newBuilder().setText(s.substring(1)).build();
                        var msg = new ProtoMessage(proto, Opcode.CmdReq);
                        pipe.channel().writeAndFlush(msg);
                        continue;
                    }
                    if (s.indexOf("token ") == 0) {
                        var proto = ProtoDebug.GmLoginReq.newBuilder().setToken(s.substring(6)).build();
                        var msg = new ProtoMessage(proto, Opcode.GmLoginReq);
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
    }

    private void runConnect() {
        try {
            bs.group(group)
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
            logger.info("connect to {}:{}", host, port);
            ChannelFuture f = bs.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            group.shutdownGracefully();
        }
    }
}
