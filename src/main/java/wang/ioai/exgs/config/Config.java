package wang.ioai.exgs.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public final class Config {
    @JsonIgnore
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public Server server;

    public Config() {
        server = new Server();
    }

    public void load(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        var file = new File(path);
        try {
            server = objectMapper.readValue(file, Server.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
