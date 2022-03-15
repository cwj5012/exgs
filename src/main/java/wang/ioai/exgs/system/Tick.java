package wang.ioai.exgs.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;
import wang.ioai.exgs.data.GData;
import wang.ioai.exgs.data.GDefine;
import wang.ioai.exgs.game.PlayerContainer;
import wang.ioai.exgs.util.ThreadPoolErrors;

import java.util.HashMap;
import java.util.concurrent.*;

public class Tick {
    private static final Logger logger = LoggerFactory.getLogger(Tick.class);

    private final ScheduledExecutorService scheduleExecutor = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> future;
    private ScheduledFuture<?> netFuture;

    public void init() {

    }

    public void run() {
        future = scheduleExecutor.scheduleAtFixedRate(() -> {
            while (true) {
                // 循环获取队列中的网络消息
                var msg = GData.dispatch.queue.poll();
                if (msg != null) {
                    try {
                        GData.dispatch.onMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }, 0, 20, TimeUnit.MILLISECONDS);
        netFuture = scheduleExecutor.scheduleAtFixedRate(() -> {
            GData.command.connect();

            // 未验证连接超时
            var currentMs = System.currentTimeMillis();
            for (var item : GData.channels.entrySet()) {
                var timeout = currentMs - item.getValue().time;
                // if (timeout >= GDefine.unauthConnectTimeOut) {
                //     logger.debug("{} auth time out {} ms.", item.getValue().channel.id().asShortText(), timeout);
                // }
                if (timeout >= GDefine.unauthConnectIdleTimeOut) {
                    logger.warn("{} auth idle time out {} ms.", item.getValue().channel.id().asShortText(), timeout);
                    item.getValue().channel.close();
                    GData.channels.remove(item.getKey());
                }
            }
            // 已验证连接心跳
            for (var item : PlayerContainer.players.entrySet()) {
                item.getValue().heartBeat.check(currentMs);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void close() {
        future.cancel(false);
        netFuture.cancel(false);
        scheduleExecutor.shutdown();
    }
}
