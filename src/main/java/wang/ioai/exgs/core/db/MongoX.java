package wang.ioai.exgs.core.db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoX {
    public MongoClient mongoClient;
    public MongoDatabase db;

    public MongoX() {

    }

    public void init(String host, int port, String dbName) {
        mongoClient = new MongoClient(host, port);
        db = mongoClient.getDatabase(dbName);
    }
}
