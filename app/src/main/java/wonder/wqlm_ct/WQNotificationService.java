package wonder.wqlm_ct;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

/**
 * Created by feeling on 2018/1/20.
 */


public class WQNotificationService extends NotificationListenerService {
    private final static String TAG = "WQNotificationService";

    private static WQNotificationService wqNotificationService;

    @Override
    public void onListenerConnected() {
        WonderLog.i(TAG, "onListenerConnected");
        Toast.makeText(this, "Hello World 通知监听 服务已连接", Toast.LENGTH_LONG).show();

        if (wqNotificationService == null) {
            wqNotificationService = this;
        }
        super.onListenerConnected();
    }

    @Override
    public void onListenerDisconnected() {
        WonderLog.i(TAG, "onListenerDisconnected");
        Toast.makeText(this, "Hello World 通知监听 服务已断开", Toast.LENGTH_LONG).show();
        wqNotificationService = null;
        super.onListenerDisconnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (WQBase.isGotNotification || WQBase.isClickedNewMessageList || WQBase.isGotPacket) {
            WonderLog.i(TAG, "onNotificationPosted: return \n"
                    + "WQ.isGotNotification = " + WQBase.isGotNotification
            + "WQ.isClickedNewMessageList = " + WQBase.isClickedNewMessageList
            + "WQ.isGotPacket = " + WQBase.isGotPacket);
            return;
        }
        WonderLog.i(TAG, "onNotificationPosted: " + sbn.getPackageName()
                + " " + sbn.getNotification().extras.getString(Notification.EXTRA_TEXT));
        if (Tools.isLockScreen(this.getApplication())) {
            Tools.wakeAndUnlock(this.getApplication());
            WQBase.isPreviouslyLockScreen = true;
        }
        if (AccessibilityHelper.openNotification(sbn, WQBase.WECHAT_PACKAGE_NAME, WQBase.WT_PACKET)) {
            // WQ.isGotNotification = true;
            /* handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WQ.isGotNotification = false;
                }
            }, 500); */
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        WonderLog.i(TAG, "onNotificationRemoved: " + sbn.toString());
        // WQ.isGotNotification = false;
        // super.onNotificationRemoved(sbn);
    }

    @Override
    public void onDestroy() {
        WonderLog.i(TAG, "onDestroy");
        wqNotificationService = null;
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        WonderLog.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        WonderLog.i(TAG, "onBind!");
        return super.onBind(intent);
    }

    public static void restarNotificationListenerService(Context context) {
        WonderLog.i(TAG,"restarNotificationListenerService");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestRebind(new ComponentName(context, WQNotificationService.class));
        } else {
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(
                    new ComponentName(context, wonder.wqlm_ct.WQNotificationService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            pm.setComponentEnabledSetting(
                    new ComponentName(context, wonder.wqlm_ct.WQNotificationService.class),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }

    public static WQNotificationService getWqNotificationService() {
        if (wqNotificationService != null) {
            return wqNotificationService;
        }
        return null;
    }

}
