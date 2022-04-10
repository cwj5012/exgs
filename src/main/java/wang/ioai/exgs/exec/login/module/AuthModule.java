package wang.ioai.exgs.exec.login.module;

import wang.ioai.exgs.core.db.MongoX;

public class AuthModule {
    public MongoX mongoX;

    public AuthModule() {
        mongoX = new MongoX();
    }

    public void init() {
        mongoX.init("localhost", 27017, "test");
    }
}
