package wonder.wqlm_ct;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationManagerCompat;

import java.util.List;
import java.util.Set;

/**
 * Created by feeling on 2018/1/25.
 */

public final class Tools {

    private final static String TAG = "Tools";

    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(1000);
        if (serviceList.size() < 0) {
            return false;
        }
        for (int i=0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static boolean isNotificationListenerServiceEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (packageNames.contains(context.getPackageName())) {
            WonderLog.i(TAG, "isNotificationListenerServiceEnabled = true");
            return true;
        }
        WonderLog.i(TAG, "isNotificationListenerServiceEnabled = false");
        return false;
    }

    public static void wakeAndUnlock(Context context)
    {
        WonderLog.i(TAG, "wakeAndUnlock");
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire(1000);
        //得到键盘锁管理器对象
        KeyguardManager km= (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁,加上会自动解锁，比较危险
        kl.disableKeyguard();
    }

    /** 是否为锁屏或黑屏状态*/
    public static boolean isLockScreen(Context context) {
        boolean result;
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            isScreenOn = pm.isInteractive();
        } else {
            isScreenOn = pm.isScreenOn();
        }
        result = km.inKeyguardRestrictedInputMode() || !isScreenOn;
        WonderLog.i(TAG, "isLockScreen = " + result);
        return result;
    }

    public static void sleepAndLock(Context context) {
        WonderLog.i(TAG, "sleepAndLock");
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire(1000);
        //得到键盘锁管理器对象
        KeyguardManager km= (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //锁屏
        kl.reenableKeyguard();
        //释放wakeLock，关灯
        wl.release();
    }

    public static String getWeChatVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfos) {
            if (packageManager.getApplicationLabel(packageInfo.applicationInfo).toString()
                    .equals(WQBase.WECHAT)) {
                return packageInfo.versionName;
            }
        }
        return null;
    }

    public static boolean isSupportHighSpeedMode(Context context) {
        if (Tools.getWeChatVersion(context).equals(WQ.WeChatVersion_6_6_1)) {
            return true;
        } else if (Tools.getWeChatVersion(context).equals(WQ.WeChatVersion_6_6_2)) {
            return true;
        } else {
            return false;
        }
    }
}
