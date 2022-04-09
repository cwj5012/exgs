package wang.ioai.exgs.core.data;

public class Opcode {
    // begin
    public static final short Hello = 1;
    public static final short Ping = 2;
    public static final short Pong = 3;
    public static final short CmdReq = 41;
    public static final short CmdRet = 42;
    public static final short EchoReq = 51;
    public static final short EchoRet = 52;

    public static final short AuthUserReq = 6;
    public static final short AuthUserRet = 7;

    public static final short HeartBeat = 8;

    public static final short EnterSceneReq = 1001;
    public static final short EnterSceneRet = 1002;
    public static final short ExitSceneReq = 1003;
    public static final short ExitSceneRet = 1004;
    public static final short SceneInfoReq = 1005;
    public static final short SceneInfoRet = 1006;
    public static final short MoveReq = 1007;
    public static final short MoveRet = 1008;
    public static final short MoveSync = 1009;

    public static final short GmLoginReq = 2001;
    public static final short GmLoginRet = 2002;
    // end
}
