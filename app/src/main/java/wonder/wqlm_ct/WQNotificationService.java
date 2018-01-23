package wonder.wqlm_ct;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by feeling on 2018/1/20.
 */


public class WQNotificationService extends NotificationListenerService {
    private final static String TAG = "wonder:WQNotificationService";

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onListenerConnected() {
        Log.i(TAG, "onListenerConnected");
        Toast.makeText(this, "Hello World 2号 服务已连接", Toast.LENGTH_LONG).show();
        super.onListenerConnected();
    }

    @Override
    public void onListenerDisconnected() {
        Log.i(TAG, "onListenerDisconnected");
        Toast.makeText(this, "Hello World 2号 服务已断开", Toast.LENGTH_LONG).show();
        super.onListenerDisconnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (WQ.isGotNotification) {
            return;
        }
        WQ.isGotNotification = true;
        Log.i(TAG, "onNotificationPosted: " + sbn.getPackageName().toString()
                + " " + sbn.getNotification().extras.getString(Notification.EXTRA_TEXT));
        if (AccessibilityHelper.isLockScreen(this.getApplication())) {
            AccessibilityHelper.wakeAndUnlock(this.getApplication());
            WQ.isPreviouslyLockScreen = true;
        }
        AccessibilityHelper.openNotification(sbn, WQ.WECHAT_PACKAGE_NAME, WQ.WT_PACKET);
        WQ.isGotNotification = false;
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "onNotificationRemoved: " + sbn.toString());
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
