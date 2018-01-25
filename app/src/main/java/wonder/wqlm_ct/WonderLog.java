package wonder.wqlm_ct;

import android.util.Log;

/**
 * Created by feeling on 2018/1/24.
 */

public class WonderLog {

    public static int LOG_LEVEL = 6;
    public static int ERROR = 1;
    public static int WARN = 2;
    public static int INFO = 3;
    public static int DEBUG = 4;
    public static int VERBOS = 5;

    private static String SELF_TAG = "wonder:";

    public static void e(String tag,String msg) {
        if (LOG_LEVEL > ERROR) {
            Log.e(SELF_TAG + tag, msg);
        }
    }

    public static void w(String tag,String msg) {
        if (LOG_LEVEL > WARN) {
            Log.w(SELF_TAG + tag, msg);
        }
    }
    public static void i(String tag,String msg) {
        if (LOG_LEVEL > INFO) {
            Log.i(SELF_TAG + tag, msg);
        }
    }
    public static void d(String tag,String msg) {
        if(LOG_LEVEL > DEBUG) {
            Log.d(SELF_TAG + tag, msg);
        }
    }
    public static void v(String tag,String msg) {
        if(LOG_LEVEL > VERBOS) {
            Log.v(SELF_TAG + tag, msg);
        }
    }
}

