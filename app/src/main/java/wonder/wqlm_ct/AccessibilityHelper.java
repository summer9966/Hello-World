package wonder.wqlm_ct;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.service.notification.StatusBarNotification;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

/**
 * Created by feeling on 2018/1/17.
 */

public final class AccessibilityHelper {

    private final static String TAG = "AccessibilityHelper";

    private AccessibilityHelper() {}

    public static boolean performClick(AccessibilityNodeInfo nodeInfo) {
        WonderLog.i(TAG, "performClick");
        if(nodeInfo == null) {
            WonderLog.i(TAG, "performClick noInfo == null.");
            return false;
        }
        if(nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            WonderLog.i(TAG, "performClick return true.");
            return true;
        } else {
            performClick(nodeInfo.getParent());
        }
        WonderLog.i(TAG, "performClick return false.");
        return false;
    }

    public static boolean clickNewMessage(AccessibilityNodeInfo nodeInfo) {
        WonderLog.i(TAG, "clickNewMessage");
        if (nodeInfo == null) {
            WonderLog.i(TAG, "clickNewMessage nodeInfo == null");
            return false;
        }
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
        WonderLog.i(TAG, "clickNewMessage result = " + result);
        return result;
    }

    public static boolean clickMessage(AccessibilityNodeInfo nodeInfo) {
        WonderLog.i(TAG, "clickMessage");
        if (nodeInfo == null) {
            WonderLog.i(TAG, "clickMessage nodeInfo == null");
            return false;
        }
        boolean result = false;
        List<AccessibilityNodeInfo> dialogList = nodeInfo.findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_LIST_DIALOG);
        if (!dialogList.isEmpty()) {
            for (AccessibilityNodeInfo item : dialogList) {
                List<AccessibilityNodeInfo> messageTextList = item.findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_LIST_MESSAGE_TEXT);
                if (!messageTextList.isEmpty()) {
                    if (messageTextList.get(0).getText().toString().contains(WQ.WT_PACKET)) {
                        item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        result = true;
                    }
                }
            }
        }
        WonderLog.i(TAG, "clickMessage result = " + result);
        return result;
    }

    public static boolean openNotification(final AccessibilityEvent accessibilityEvent, final String textFound) {
        WonderLog.i(TAG, "openNotification AccessibilityEvent");
        List<CharSequence> texts = accessibilityEvent.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                WonderLog.i(TAG, "onAccessibilityEvent text = " + content);
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
        WonderLog.i(TAG, "openNotification  StatusBarNotification");
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
        WonderLog.i(TAG, "clickView ");
        List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId(viewID);
        if (!nodeInfoList.isEmpty()) {
            if (nodeInfoList.get(0).isClickable()) {
                nodeInfoList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                WonderLog.i(TAG, "clickView success!");
                return;
            }
        }
        WonderLog.i(TAG, "clickView failed!");
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

}
