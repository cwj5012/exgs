package wang.ioai.exgs.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Server {
    @JsonProperty("ip")
    public String ip;
    @JsonProperty("manager")
    public Listen manager;
    @JsonProperty("game")
    public Listen game;
}
