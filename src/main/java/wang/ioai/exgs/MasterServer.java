package wang.ioai.exgs;

import wang.ioai.exgs.exec.master.MasterBoot;

public class MasterServer {
    public static void main(String[] args) {
        var boot = new MasterBoot();
        boot.init();
        boot.run();
    }
}
