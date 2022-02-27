package wang.ioai.exgs.game.scene;

import wang.ioai.exgs.pb.ProtoDebug;

import java.util.ArrayList;
import java.util.HashMap;

public class SceneManager {
    public static class Info {
        public int x;
        public int y;

        public Info(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private final HashMap<Long, Info> playerInfo = new HashMap<>();

    public SceneManager() {

    }

    public boolean add(Long uid) {
        if (playerInfo.get(uid) == null) {
            playerInfo.put(uid, new Info(0, 0));
        } else {
            return false;
        }
        return true;
    }

    public Info update(Long uid, int x, int y) {
        var info = playerInfo.get(uid);
        if (info != null) {
            info.x += x;
            info.y += y;
        } else {
            return null;
        }
        return info;
    }

    public ArrayList<ProtoDebug.PlayerInfo> getInfo() {
        ArrayList<ProtoDebug.PlayerInfo> result = new ArrayList<>();
        for (var item : playerInfo.entrySet()) {
            result.add(ProtoDebug.PlayerInfo.newBuilder()
                    .setUid(item.getKey()).setX(item.getValue().x).setY(item.getValue().y).build());
        }
        return result;
    }

    public String DebugStatus() {
        StringBuilder result = new StringBuilder();
        for (var item : playerInfo.entrySet()) {
            result.append(String.format("player %d x %d y %d\n",
                    item.getKey(), item.getValue().x, item.getValue().y));
        }
        return result.toString();
    }
}
