package wonder.wqlm_ct;

/**
 * Created by feeling on 2018/1/20.
 */

public class Config {

    // 运行模式
    // 0 兼容模式：尽可能通过Text来查找Node
    public final static int compatibleMode = 0;
    // 1 高速模式：通过ViewID来查找Node
    public final static int highSpeedMode = 1;
    public final static int runningMode = highSpeedMode;

    public final static boolean isGotPacketSelf = true;
}
