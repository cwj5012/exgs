package wang.ioai.exgs;

import wang.ioai.exgs.exec.login.LoginBoot;

public class LoginServer {
    public static void main(String[] args) {
        var boot = new LoginBoot();
        boot.init();
        boot.run();
    }
}
