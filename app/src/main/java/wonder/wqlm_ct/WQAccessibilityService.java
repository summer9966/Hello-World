package wonder.wqlm_ct;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

/**
 * Created by feeling on 2018/1/13.
 */

public class WQAccessibilityService extends AccessibilityService {

    private final static String TAG = "WQAccessibilityService";
    private static WQAccessibilityService service;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // flags = START_FLAG_RETRY;
        return START_STICKY;
        // return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        String className = accessibilityEvent.getClassName().toString();

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();

        WonderLog.i(TAG, "onAccessibilityEvent eventType = " + eventType + "className = " + className);

        switch (eventType) {
            // 第一步：监听通知栏消息
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED: {
                if (WQ.isGotNotification) {
                    return;
                }
                WQ.isGotNotification = true;
                WonderLog.i(TAG, "通知栏消息改变");
                if (Tools.isLockScreen(this.getApplication())) {
                    Tools.wakeAndUnlock(this.getApplication());
                    WQ.isPreviouslyLockScreen = true;
                }
                AccessibilityHelper.openNotification(accessibilityEvent, WQ.WT_PACKET);
                WQ.isGotNotification = false;
                FirstNotificationService.toggleNotificationListenerService(FirstNotificationService.getService());
                break;
            }
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: {
                WonderLog.i(TAG, "窗口状态改变");
                if (Config.runningMode == Config.compatibleMode) {
                    CompatibleMode.dealWindowStateChanged(className, rootNode);
                } else {
                    HighSpeedMode.dealWindowStateChanged(className, rootNode);
                }
                /*if (WQ.isPreviouslyLockScreen && WQ.currentAutoPacketStatus == WQ.W_rebackUIStatus) {
                    WQ.isPreviouslyLockScreen = false;
                    WQ.setCurrentAutoPacketStatus(WQ.W_waitStatus);
                    AccessibilityHelper.sleepAndLock(this.getApplication());
                }*/
                break;
            }
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: {
                // WonderLog.i(TAG, "窗口内容变化");
                if (Config.runningMode == Config.compatibleMode) {
                    // 联系人列表
                    CompatibleMode.dealWindowContentChanged(rootNode);
                } else {
                    HighSpeedMode.dealWindowContentChanged(className, rootNode);
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onInterrupt() {
        WonderLog.i(TAG, "onInterrupt");
        Toast.makeText(this, "Hello World 1号 服务被中断", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onServiceConnected() {
        WonderLog.i(TAG, "onServiceConnected");
        Toast.makeText(this, "Hello World 1号 服务已连接", Toast.LENGTH_LONG).show();
        service = this;
        super.onServiceConnected();
    }

    @Override
    public void onDestroy() {
        service = null;
        super.onDestroy();
    }

    public static AccessibilityService getService() {
        return service;
    }

    public static void toggleNotificationListenerService(Context context) {
        WonderLog.e(TAG,"toggleNLS");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(context, FirstNotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(
                new ComponentName(context, FirstNotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
