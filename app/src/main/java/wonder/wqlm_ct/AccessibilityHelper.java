package wonder.wqlm_ct;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

/**
 * Created by feeling on 2018/1/17.
 */

public final class AccessibilityHelper {

    private final static String TAG = "wonder:AccessibilityHelper";

    private static KeyguardManager sKeyguardManager;
    private static PowerManager sPowerManager;

    private AccessibilityHelper() {}

    public static boolean performClick(AccessibilityNodeInfo nodeInfo) {
        Log.i(TAG, "performClick");
        if(nodeInfo == null) {
            Log.i(TAG, "performClick noInfo == null.");
            return false;
        }
        if(nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.i(TAG, "performClick return true.");
            return true;
        } else {
            performClick(nodeInfo.getParent());
        }
        Log.i(TAG, "performClick return false.");
        return false;
    }

    public static boolean clickNewMessage(AccessibilityNodeInfo nodeInfo) {
        boolean result = false;
        List<AccessibilityNodeInfo> dialogList = nodeInfo.findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_LIST_DIALOG);
        if (!dialogList.isEmpty()) {
            for (AccessibilityNodeInfo item : dialogList) {
                List<AccessibilityNodeInfo> newMessageList = item.findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_LIST_MESSAGE_NUM);
                newMessageList.addAll(item.findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_LIST_MESSAGE_POT));
                if (!newMessageList.isEmpty()) {
                    List<AccessibilityNodeInfo> newMessageTextList = item.findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_LIST_MESSAGE_TEXT);
                    if (!newMessageTextList.isEmpty()) {
                        if (newMessageTextList.get(0).getText().toString().contains(WQ.WT_PACKET)) {
                            item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            result = true;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static boolean openNotification(final AccessibilityEvent accessibilityEvent, final String textFound) {
        Log.i(TAG, "openNotification AccessibilityEvent");
        List<CharSequence> texts = accessibilityEvent.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                Log.i(TAG, "onAccessibilityEvent text = " + content);
                if (content.contains(textFound)) {
                    // 模拟打开通知栏消息
                    if (accessibilityEvent.getParcelableData() != null
                            && accessibilityEvent.getParcelableData() instanceof Notification) {
                        Notification notification = (Notification) accessibilityEvent.getParcelableData();
                        PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            pendingIntent.send();
                            return true;
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
        return false;
    }

    public static boolean openNotification(final StatusBarNotification sbn, final String packageFound, final String textFound) {
        Log.i(TAG, "openNotification  StatusBarNotification");
        String packageName = sbn.getPackageName().toString();
        if (packageName.equals(packageFound)) {
            String content = sbn.getNotification().extras.getString(Notification.EXTRA_TEXT);
            if (content.contains(textFound)) {
                PendingIntent pendingIntent = sbn.getNotification().contentIntent;
                try {
                    pendingIntent.send();
                    return true;
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }

        }
        return false;
    }

    public static void clickView(AccessibilityNodeInfo nodeInfo, String viewID) {
        Log.i(TAG, "clickView ");
        List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId(viewID);
        if (!nodeInfoList.isEmpty()) {
            if (nodeInfoList.get(0).isClickable()) {
                nodeInfoList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.i(TAG, "clickView success!");
                return;
            }
        }
        Log.i(TAG, "clickView failed!");
    }

    /** 返回主界面事件*/
    public static void performHome(AccessibilityService service) {
        if(service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    /** 返回事件*/
    public static void performBack(AccessibilityService service) {
        if(service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    public static void wakeAndUnlock(Context context)
    {
        Log.i(TAG, "wakeAndUnlock");
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
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
        boolean isScreenOn = false;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            isScreenOn = pm.isInteractive();
        } else {
            isScreenOn = pm.isScreenOn();
        }
        result = km.inKeyguardRestrictedInputMode() || !isScreenOn;
        Log.i(TAG, "isLockScreen = " + result);
        return result;
    }

    public static void sleepAndLock(Context context) {
        Log.i(TAG, "sleepAndLock");
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //得到键盘锁管理器对象
        KeyguardManager km= (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //锁屏
        kl.reenableKeyguard();
        //释放wakeLock，关灯
        wl.release();
    }
}
