package wang.ioai.exgs.core.data;

public class Instance {

    private Instance() {

    }

    private static class Holder {
        private static final Instance INSTANCE = new Instance();
    }

    public static Instance getInstance() {
        return Holder.INSTANCE;
    }

    // rest of class omitted
}