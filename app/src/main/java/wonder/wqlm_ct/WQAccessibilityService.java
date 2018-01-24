package wonder.wqlm_ct;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

/**
 * Created by feeling on 2018/1/13.
 */

public class WQAccessibilityService extends AccessibilityService {

    private final static String TAG = "wonder:WQAccessibilityService";
    private static WQAccessibilityService service;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        String className = accessibilityEvent.getClassName().toString();

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();

        Log.i(TAG, "onAccessibilityEvent eventType = " + eventType + "className = " + className);

        if (rootNode == null) {
            Log.w(TAG, "onAccessibilityEvent rootNode == null");
            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                    && className.equals(WQ.WCN_PACKET_RECEIVE)) {
                WQ.isStuckCauseNull = true;
            }
            return;
        }

        switch (eventType) {
            // 第一步：监听通知栏消息
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED: {
                if (WQ.isGotNotification) {
                    return;
                }
                WQ.isGotNotification = true;
                Log.i(TAG, "通知栏消息改变");
                if (AccessibilityHelper.isLockScreen(this.getApplication())) {
                    AccessibilityHelper.wakeAndUnlock(this.getApplication());
                    WQ.isPreviouslyLockScreen = true;
                }
                AccessibilityHelper.openNotification(accessibilityEvent, WQ.WT_PACKET);
                WQ.isGotNotification = false;
                break;
            }
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: {
                Log.i(TAG, "窗口状态改变");
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
                // Log.i(TAG, "窗口内容变化");
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
        Log.i(TAG, "onInterrupt");
        Toast.makeText(this, "Hello World 1号 服务被中断", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onServiceConnected() {
        Log.i(TAG, "onServiceConnected");
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
}
