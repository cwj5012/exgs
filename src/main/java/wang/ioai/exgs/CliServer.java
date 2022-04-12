package wang.ioai.exgs;

import wang.ioai.exgs.exec.cli.CliBoot;

public class CliServer {
    public static void main(String[] args) {
        var boot = new CliBoot(args);
        boot.init();
        boot.run();
    }
}
