package wang.ioai.exgs.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Server {
    @JsonProperty("ip")
    public String ip;
    @JsonProperty("manager")
    public Listen manager;
    @JsonProperty("game")
    public Listen game;
    @JsonProperty("route")
    public Listen route;

    public String mongo_host;
    public int mongo_port;

    public String mysql_host;
    public int mysql_port;

    public String redis_host;
    public int redis_port;
}