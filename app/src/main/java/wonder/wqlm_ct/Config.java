package wonder.wqlm_ct;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by feeling on 2018/1/20.
 */

public class Config {
    private final static String TAG = "Config";
    private final static String WonderConfig = "wonderConfig";

    // 运行模式
    // 0 兼容模式：尽可能通过Text来查找Node
    public final static int compatibleMode = 0;
    // 1 高速模式：通过ViewID来查找Node
    public final static int highSpeedMode = 1;
    public static int runningMode = highSpeedMode;

    public static boolean isGotPacketSelf = true;

    public static boolean isUsedDelayed = true;
    public static boolean isUsedRandomDelayed = true;
    public static int delayedTime = 100;

    public Config() {

    }

    public static void saveAllConfig(Context context) {
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(WonderConfig, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("runningMode", runningMode)
                .putBoolean("isGotPacketSelf", isGotPacketSelf)
                .putBoolean("isUsedDelayed", isUsedDelayed)
                .putBoolean("isUsedRandomDelayed", isUsedRandomDelayed)
                .putInt("delayedTime", delayedTime)
                .commit();
    }

    public static void getAllConfig(Context context) {
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(WonderConfig, Context.MODE_PRIVATE);
        runningMode = sharedPreferences.getInt("runningMode", runningMode);
        isGotPacketSelf = sharedPreferences.getBoolean("isGotPacketSelf", isGotPacketSelf);
        isUsedDelayed = sharedPreferences.getBoolean("isUsedDelayed", isUsedDelayed);
        isUsedRandomDelayed = sharedPreferences.getBoolean("isUsedRandomDelayed", isUsedRandomDelayed);
        delayedTime = sharedPreferences.getInt("delayedTime", delayedTime);
    }
}
