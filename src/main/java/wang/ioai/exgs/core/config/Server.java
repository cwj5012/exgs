package wang.ioai.exgs.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Server {
    @JsonProperty("ip")
    public String ip;
    @JsonProperty("manager")
    public ServerBaseConfig manager;
    @JsonProperty("game")
    public ServerBaseConfig game;
    @JsonProperty("route")
    public ServerBaseConfig route;
    @JsonProperty("login")
    public ServerBaseConfig login;

    public String mongo_host;
    public int mongo_port;

    public String mysql_host;
    public int mysql_port;

    public String redis_host;
    public int redis_port;
}
