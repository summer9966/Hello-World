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
    private int runningMode = highSpeedMode;

    private boolean isGotPacketSelf = true;

    private boolean isUsedDelayed = true;
    private boolean isUsedRandomDelayed = false;
    private int delayedTime = 100;

    private boolean isUsedKeyWords = true;
    private String packetKeyWords = "测试、挂、专属、生日、踢";

    private static Config config;

    public static synchronized Config getConfig(Context context) {
        WonderLog.i(TAG, "getConfig");
        if(config == null) {
            config = new Config(context.getApplicationContext());
        }
        return config;
    }

    private SharedPreferences sharedPreferences;
    private Context context;

    private Config(Context context) {
        WonderLog.i(TAG, "Config");
        this.context = context;
        sharedPreferences = context.getSharedPreferences(WonderConfig, Context.MODE_PRIVATE);
    }

    public void saveAllConfig() {
        sharedPreferences.edit().putInt("runningMode", runningMode)
                .putBoolean("isGotPacketSelf", isGotPacketSelf)
                .putBoolean("isUsedDelayed", isUsedDelayed)
                .putBoolean("isUsedRandomDelayed", isUsedRandomDelayed)
                .putInt("delayedTime", delayedTime)
                .putBoolean("isUsedKeyWords", isUsedKeyWords)
                .putString("packetKeyWords", packetKeyWords)
                .apply();
    }

    public void getAllConfig() {
        runningMode = sharedPreferences.getInt("runningMode", runningMode);
        isGotPacketSelf = sharedPreferences.getBoolean("isGotPacketSelf", isGotPacketSelf);
        isUsedDelayed = sharedPreferences.getBoolean("isUsedDelayed", isUsedDelayed);
        isUsedRandomDelayed = sharedPreferences.getBoolean("isUsedRandomDelayed", isUsedRandomDelayed);
        delayedTime = sharedPreferences.getInt("delayedTime", delayedTime);
        isUsedKeyWords = sharedPreferences.getBoolean("isUsedKeyWords", isUsedKeyWords);
        packetKeyWords = sharedPreferences.getString("packetKeyWords", packetKeyWords);
    }

    public int getRunningMode () {
        return sharedPreferences.getInt("runningMode", runningMode);
    }

    public boolean getIsGotPacketSelf() {
        return sharedPreferences.getBoolean("isGotPacketSelf", isGotPacketSelf);
    }

    public boolean getIsUsedDelayed() {
        return sharedPreferences.getBoolean("isUsedDelayed", isUsedDelayed);
    }

    public boolean getIsUsedRandomDelayed() {
        return sharedPreferences.getBoolean("isUsedRandomDelayed", isUsedRandomDelayed);
    }

    public int getDelayedTime() {
        return sharedPreferences.getInt("delayedTime", delayedTime);
    }

    public boolean getIsUsedKeyWords() {
        return sharedPreferences.getBoolean("isUsedKeyWords", isUsedKeyWords);
    }

    public String getPacketKeyWords() {
        return sharedPreferences.getString("packetKeyWords", packetKeyWords);
    }

    public void saveRunningMode(int mode) {
        sharedPreferences.edit().putInt("runningMode", mode).apply();
    }

    public void saveIsGotPacketSelf(boolean b) {
        sharedPreferences.edit().putBoolean("isGotPacketSelf", b).apply();
    }

    public void saveIsUsedDelayed(boolean b) {
        sharedPreferences.edit().putBoolean("isUsedDelayed", b).apply();
    }

    public void saveIsUsedRandomDelayed(boolean b) {
        sharedPreferences.edit().putBoolean("isUsedRandomDelayed", b).apply();
    }

    public void saveDelayedTime(int time) {
        sharedPreferences.edit().putInt("delayedTime", time).apply();
    }

    public void saveIsUsedKeyWords(boolean b) {
        sharedPreferences.edit().putBoolean("isUsedKeyWords", b).apply();
    }

    public void savePacketKeyWords(String keyWords) {
        sharedPreferences.edit().putString("packetKeyWords", keyWords).apply();
    }
}
