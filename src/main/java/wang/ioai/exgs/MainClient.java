package wang.ioai.exgs;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.core.data.Opcode;
import wang.ioai.exgs.core.net.codec.ProtoDecoder;
import wang.ioai.exgs.core.net.codec.ProtoEncoder;
import wang.ioai.exgs.core.net.handler.EchoClientHandler;
import wang.ioai.exgs.core.net.msg.ProtoMessage;
import wang.ioai.exgs.common.pb.ProtoDebug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainClient {
    private static final Logger logger = LoggerFactory.getLogger(MainClient.class);

    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", "9001"));

    private static int pingId;

    private static ChannelPipeline pipe;

    public static void main(String[] args) throws Exception {
        logger.info("current dir: {}", System.getProperty("user.dir"));

        EventLoopGroup group = new NioEventLoopGroup();
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
                        if (s.indexOf("/") == 0) {
                            var proto = ProtoDebug.Cmd.newBuilder().setText(s.substring(4)).build();
                            var msg = new ProtoMessage(proto, Opcode.CmdReq);
                            pipe.channel().writeAndFlush(msg);
                            continue;
                        }
                        if (s.indexOf("hello ") == 0) {
                            var proto = ProtoDebug.Hello.newBuilder().setText(s.substring(6)).build();
                            var msg = new ProtoMessage(proto, Opcode.Hello);
                            pipe.channel().writeAndFlush(msg);
                        }
                        if (s.indexOf("echo ") == 0) {
                            var proto = ProtoDebug.Echo.newBuilder().setText(s.substring(5)).build();
                            var msg = new ProtoMessage(proto, Opcode.EchoReq);
                            pipe.channel().writeAndFlush(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
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
                            pipe.addLast(new ProtoDecoder());
                            pipe.addLast(new ProtoEncoder());
                            pipe.addLast(new EchoClientHandler());
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(HOST, PORT).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
