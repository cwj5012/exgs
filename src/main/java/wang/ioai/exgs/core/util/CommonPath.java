package wang.ioai.exgs.core.util;

import java.nio.file.Paths;

public class CommonPath {
    public static String userPath() {
        return System.getProperty("user.dir");
    }

    public static String configPath() {
        return Paths.get(System.getProperty("user.dir"), "config/server.json").toString();
    }
}
