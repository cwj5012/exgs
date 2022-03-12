package wang.ioai.exgs.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;
import wang.ioai.exgs.data.GData;
import wang.ioai.exgs.util.ThreadPoolErrors;

import java.util.HashMap;

public class Command {
    private static final Logger logger = LoggerFactory.getLogger(Tick.class);

    private final int threadNum = 4;
    private final ThreadPoolErrors threadExecutor = new ThreadPoolErrors(threadNum);
    private final HashMap<String, Runnable> commands = new HashMap<>();
    private String command;
    private Runnable sbuRunnable;
    private final String channelName = "server";

    private int subCount = 0;
    private int pubCount = 0;

    public void init() {
        commands.put("help", () -> {
            GData.jedis.publish(channelName, """
                    ######### cmd list #########
                    help            帮助
                    echo <arg>      回显
                    pid             进程 ID
                    show-scene
                    show-channel
                    exit            退出
                    ######### cmd end #########""");
        });
        commands.put("exit", () -> {
            GData.jedis.publish(channelName, "server will exit now.");
            System.exit(0);
        });
        commands.put("echo", () -> GData.jedis.publish(channelName, command));
        commands.put("pid", () -> GData.jedis.publish(channelName, Long.toString(ProcessHandle.current().pid())));
        commands.put("show-channel", () -> {
            var msg = String.format("not auth channel %d\nuser to channel  %d\nchannel to user  %d",
                    GData.channels.size(), GData.userToChannel.size(), GData.channelToUser.size());
            GData.jedis.publish(channelName, msg);
        });
        commands.put("show-scene", () -> {
            GData.jedis.publish(channelName, GData.sceneManager.DebugStatus());
        });

        sbuRunnable = new Runnable() {
            @Override
            public void run() {
                JedisPubSub jedisPubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        var msg = message.strip();
                        var spaceIndex = msg.indexOf(" ");
                        if (spaceIndex > 0) {
                            // 格式：cmd <arg>
                            var cmd = msg.substring(0, spaceIndex);
                            var arg = msg.substring(spaceIndex + 1);
                            if (commands.containsKey(cmd)) {
                                command = arg;
                                commands.get(cmd).run();
                            }
                        } else {
                            if (commands.containsKey(msg)) {
                                command = "";
                                commands.get(msg).run();
                            }
                        }
                    }

                    @Override
                    public void onSubscribe(String channel, int subscribedChannels) {
                        logger.debug("redis sub: {} {}", channel, subscribedChannels);
                    }

                    @Override
                    public void onUnsubscribe(String channel, int subscribedChannels) {
                        logger.debug("redis unsub: {} {}", channel, subscribedChannels);
                    }
                };
                try {
                    if (subCount == 0) {
                        ++subCount;
                        GData.jedisSub = new Jedis("localhost", 6379);
                        GData.jedisSub.connect();
                        logger.info("connect redis ok.");
                        GData.jedisSub.subscribe(jedisPubSub, "client");
                    }
                } catch (JedisConnectionException e) {
                    --subCount;
                    GData.jedisSub.close();
                    logger.error("{}", e.getMessage());
                }
                // logger.debug("exit thread");
            }
        };
    }

    public void connect() {
        try {
            if (pubCount == 0) {
                ++pubCount;
                GData.jedis = new Jedis("localhost", 6379);
                GData.jedis.connect();
                logger.info("connect redis ok.");
            } else {
                GData.jedis.ping();
            }
        } catch (JedisConnectionException e) {
            logger.error("{}", e.getMessage());
            --pubCount;
            GData.jedis = new Jedis("localhost", 6379);
        }
        if (subCount == 0) {
            threadExecutor.execute(sbuRunnable);
        }
    }
}
