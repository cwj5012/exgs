package wang.ioai.exgs;

import wang.ioai.exgs.boot.Boot;

public class GameServer {
    public static void main(String[] args) {
        var boot = new Boot();
        boot.init();
        boot.run();
    }
}
