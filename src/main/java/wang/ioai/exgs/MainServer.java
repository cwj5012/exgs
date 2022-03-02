package wang.ioai.exgs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.ioai.exgs.boot.Boot;
import wang.ioai.exgs.data.GData;

import java.nio.file.Paths;

public class MainServer {


    public static void main(String[] args) throws Exception {
        var boot = new Boot();
        boot.init();

    }
}
