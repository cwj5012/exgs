package wang.ioai.exgs.data;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import wang.ioai.exgs.config.Config;
import wang.ioai.exgs.game.Player;
import wang.ioai.exgs.game.PlayerManager;
import wang.ioai.exgs.game.debug.HeartBeatHandle;
import wang.ioai.exgs.game.debug.SystemHandle;
import wang.ioai.exgs.game.scene.SceneManager;
import wang.ioai.exgs.game.user.AuthManager;
import wang.ioai.exgs.game.user.InfoHandle;
import wang.ioai.exgs.net.Dispatch;
import wang.ioai.exgs.net.NetClient;
import wang.ioai.exgs.net.NetServer;
import wang.ioai.exgs.system.Tick;

import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

public final class GData {
    public static Config config;
    public static NetServer netServer;
    public static NetClient netClient;
    public static Tick tick;
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

    public static class ChannelInfo {
        public Channel channel;
        public long time;

        public ChannelInfo(Channel channel, long time) {
            this.channel = channel;
            this.time = time;
        }
    }

    // 未认证连接，netty channel id -> netty channel
    public static final ConcurrentHashMap<ChannelId, ChannelInfo> channels = new ConcurrentHashMap<>();
    // 已认证连接，user id -> netty channel
    public static final ConcurrentHashMap<Long, Channel> userToChannel = new ConcurrentHashMap<>();
    // 已认证连接，user id -> netty channel
    public static final ConcurrentHashMap<ChannelId, Player> channelToUser = new ConcurrentHashMap<>();

    public static void init() {
        config = new Config();
        netServer = new NetServer();
        netClient = new NetClient();
        tick = new Tick();
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

        // 先加载配置
        GData.config.load(Paths.get(System.getProperty("user.dir"), "config/server.json").toString());

        mongoClient = new MongoClient("localhost", 27017);
        db = mongoClient.getDatabase("test");

        jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
    }
}
