package wang.ioai.exgs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainCmd {
    private static final Logger logger = LoggerFactory.getLogger(MainClient.class);
    private static final ExecutorService threadExecutor = Executors.newFixedThreadPool(4);

    private static Jedis jedisSub;
    private static Jedis jedis;
    private static JedisPubSub jedisPubSub;
    private static final String clientChannel = "client";
    private static final String serverChannel = "server";

    public static void main(String[] args) {
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
        jedisSub = jedisPool.getResource();
        jedis = jedisPool.getResource();

        logger.info("current dir: {}", System.getProperty("user.dir"));
        logger.info("pid: {}", ProcessHandle.current().pid());
        logger.info("enter 'q' to exit the process.");
        threadExecutor.execute(() -> {
            try {
                while (true) {
                    Scanner sc = new Scanner(System.in);
                    var cmd = sc.nextLine();
                    if (cmd.equals("q")) {
                        jedisPubSub.unsubscribe();
                        break;
                    }
                    jedis.publish(clientChannel, cmd);
                }
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        });
        threadExecutor.execute(() -> {
            jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    System.out.println(message);
                }

                @Override
                public void onSubscribe(String channel, int subscribedChannels) {
                    logger.debug("jedis sub: {} {}", channel, subscribedChannels);
                }

                @Override
                public void onUnsubscribe(String channel, int subscribedChannels) {
                    logger.debug("jedis unsub: {} {}", channel, subscribedChannels);
                }
            };

            jedisSub.subscribe(jedisPubSub, serverChannel);
        });
        threadExecutor.shutdown();
    }
}
