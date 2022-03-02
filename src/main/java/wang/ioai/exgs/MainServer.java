package wang.ioai.exgs;

import wang.ioai.exgs.boot.Boot;

public class MainServer {
    public static void main(String[] args) throws Exception {
        var boot = new Boot();
        boot.init();
        boot.run();
    }
}
