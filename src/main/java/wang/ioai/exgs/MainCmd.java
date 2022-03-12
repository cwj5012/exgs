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
    private static final ExecutorService threadExecutor = Executors.newFixedThreadPool(2);

    private static Jedis jedisSub;
    private static Jedis jedis;
    private static JedisPubSub jedisPubSub;
    private static final String clientChannel = "client";
    private static final String serverChannel = "server";

    private static final String redisHost = "localhost";
    private static final int redisPort = 6379;

    private static final String cmdQuit = "q";

    public static void main(String[] args) {
        logger.info("current dir: {}", System.getProperty("user.dir"));
        logger.info("pid: {}", ProcessHandle.current().pid());
        logger.info("enter 'q' to exit the process.");

        // 初始化 redis 连接池
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort);
        jedisSub = jedisPool.getResource();
        jedis = jedisPool.getResource();

        // 接收 console 命令并发布通告
        threadExecutor.execute(() -> {
            try {
                while (true) {
                    Scanner sc = new Scanner(System.in);
                    var cmd = sc.nextLine();
                    if (cmd.equals(cmdQuit)) {
                        jedisPubSub.unsubscribe();
                        break;
                    }
                    jedis.publish(clientChannel, cmd);
                }
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        });

        // 订阅结果
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

        System.out.println("exit ok.");
    }
}
