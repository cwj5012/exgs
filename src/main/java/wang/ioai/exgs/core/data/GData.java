package wang.ioai.exgs.core.data;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import wang.ioai.exgs.core.boot.Boot;
import wang.ioai.exgs.core.config.Config;
import wang.ioai.exgs.core.net.Dispatch;
import wang.ioai.exgs.core.net.NetClient;
import wang.ioai.exgs.core.net.NetServer;
import wang.ioai.exgs.core.system.Command;
import wang.ioai.exgs.core.system.Tick;
import wang.ioai.exgs.exec.game.Player;
import wang.ioai.exgs.exec.game.PlayerManager;
import wang.ioai.exgs.exec.game.debug.HeartBeatHandle;
import wang.ioai.exgs.exec.game.debug.SystemHandle;
import wang.ioai.exgs.exec.game.scene.SceneManager;
import wang.ioai.exgs.exec.game.user.AuthManager;
import wang.ioai.exgs.exec.game.user.InfoHandle;

import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

public final class GData {
    private static Boot boot;
    public static Config config;
    public static NetServer netServer;
    public static NetClient netClient;
    public static Tick tick;
    public static Command command;
    public static Dispatch dispatch;
    public static PlayerManager playerManager;
    public static AuthManager authManager;
    public static HeartBeatHandle heartBeatHandle;
    public static SystemHandle systemHandle;
    public static InfoHandle infoHandle;

    public static int recvByte;
    public static int sendByte;
    public static int recvCount;
    public static int recvCountLast;
    public static int sendCount;
    public static long lastTime;

    public static MongoClient mongoClient;
    public static MongoDatabase db;
    public static Jedis jedisSub;
    public static Jedis jedis;
    public static JedisPool jedisPool;

    public static SceneManager sceneManager;

    public static Boot getBoot() {
        return boot;
    }

    public static void setBoot(Boot boot) {
        GData.boot = boot;
    }

    public static class ChannelInfo {
        public Channel channel;
        public long time;

        public ChannelInfo(Channel channel, long time) {
            this.channel = channel;
            this.time = time;
        }
    }

    // ??????????????????netty channel id -> netty channel
    public static final ConcurrentHashMap<ChannelId, ChannelInfo> channels = new ConcurrentHashMap<>();
    // ??????????????????user id -> netty channel
    public static final ConcurrentHashMap<Long, Channel> userToChannel = new ConcurrentHashMap<>();
    // ??????????????????user id -> netty channel
    public static final ConcurrentHashMap<ChannelId, Player> channelToUser = new ConcurrentHashMap<>();

    public static void init() {
        config = new Config();
        netServer = new NetServer();
        netClient = new NetClient();
        tick = new Tick();
        command = new Command();
        dispatch = new Dispatch();
        playerManager = new PlayerManager();
        authManager = new AuthManager();
        heartBeatHandle = new HeartBeatHandle();
        heartBeatHandle.init();
        systemHandle = new SystemHandle();
        systemHandle.init();
        infoHandle = new InfoHandle();
        infoHandle.init();
        lastTime = System.currentTimeMillis();

        sceneManager = new SceneManager();

        // ???????????????
        GData.config.load(Paths.get(System.getProperty("user.dir"), "config/server.json").toString());

        // mongoClient = new MongoClient("localhost", 27017);
        // db = mongoClient.getDatabase("test");

        jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);

        GData.command.init();
        GData.tick.init();
        // GData.netServer.init();
    }
}
