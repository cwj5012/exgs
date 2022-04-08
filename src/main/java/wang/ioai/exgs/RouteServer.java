package wang.ioai.exgs;

import wang.ioai.exgs.exec.route.RouteBoot;

public class RouteServer {
    public static void main(String[] args) {
        var boot = new RouteBoot();
        boot.init();
        boot.run();
    }
}
