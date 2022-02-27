package wang.ioai.exgs.data;

import java.nio.ByteOrder;

public class GDefine {
    public static final int headerLength = 4;
    public static final ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    public static final int maxFrameLength = 4096;
    public static final int lengthFieldOffset = 0;
    public static final int lengthFieldLength = 2; // 2（报文长度）
    public static final int lengthAdjustment = 2; // 2（opcode)
    public static final int initialBytesToStrip = 2; // len | 2（报文长度） | 报文内容
    public static final boolean failFast = true;

    public static final int messageQueueSize = 1024;

    public static final int unauthConnectTimeOut = 5 * 1000; // 未验证连接超时（毫秒）
    public static final int unauthConnectIdleTimeOut = 10 * 1000; // 未验证连接闲置超时（毫秒）
}
