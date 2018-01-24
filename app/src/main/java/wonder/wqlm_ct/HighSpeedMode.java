package wonder.wqlm_ct;

import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


/**
 * Created by feeling on 2018/1/20.
 */

public class HighSpeedMode {
    private final static String TAG = "wonder:HighSpeedMode";

    private static boolean isGotPacket = false;

    public static void dealWindowStateChanged(String className, final AccessibilityNodeInfo rootNode) {
        Log.i(TAG, "dealWindowStateChanged");
        if (rootNode == null) {
            Log.i(TAG, "dealWindowStateChanged rootNode == null");
            return;
        }
        if (className.equals(WQ.WCN_LAUNCHER)) {
            // 聊天页面
            if (Config.isGotPacketSelf && WQ.currentSelfPacketStatus == WQ.W_openedPayStatus) {
                WQ.setCurrentSelfPacketStatus(WQ.W_intoChatDialogStatus);
                getPacket(rootNode, true);
            } else {
                getPacket(rootNode, false);
            }
        } else if (className.equals(WQ.WCN_PACKET_RECEIVE)) {
            // 打开红包
            Log.i(TAG, "dealWindowStateChanged 打开红包页面");
            if (Config.isGotPacketSelf && WQ.currentSelfPacketStatus == WQ.W_intoChatDialogStatus) {
                openPacket(rootNode);
                WQ.setCurrentSelfPacketStatus(WQ.W_gotSelfPacketStatus);
            } else {
                if (openPacket(rootNode)) {
                    isGotPacket = true;
                    WQ.isClickedNewMessageList = false;
                    WQ.isGotPacket = false;
                }
            }
        } else if (className.equals(WQ.WCN_PACKET_SEND)) {
            WQ.setCurrentSelfPacketStatus(WQ.W_openedPacketSendStatus);
        } else if (className.equals((WQ.WCN_PACKET_PAY))) {
            if (WQ.currentSelfPacketStatus == WQ.W_openedPacketSendStatus) {
                WQ.setCurrentSelfPacketStatus(WQ.W_openedPayStatus);
            }
        } else if (className.equals(WQ.WCN_PACKET_DETAIL)) {
            Log.i(TAG, "dealWindowStateChanged 红包详情页面");
            if (WQ.currentSelfPacketStatus == WQ.W_gotSelfPacketStatus) {
                AccessibilityHelper.performBack(WQAccessibilityService.getService());
                WQ.setCurrentSelfPacketStatus(WQ.W_otherStatus);
            }
            if (isGotPacket) {
                AccessibilityHelper.performBack(WQAccessibilityService.getService());
                isGotPacket = false;
            }
        }
    }

    public static void dealWindowContentChanged(String className, AccessibilityNodeInfo rootNode) {
        Log.i(TAG, "dealWindowContentChanged");
        if (rootNode == null) {
            Log.i(TAG, "dealWindowContentChanged rootNode == null");
            return;
        }

        if (AccessibilityHelper.clickNewMessage(rootNode)) {
            WQ.isClickedNewMessageList = true;
            return;
        }
        if (getPacket(rootNode, false)) {
            WQ.isGotPacket = true;
            return;
        }

        if (WQ.isStuckCauseNull) {
            if (openPacket(rootNode)) {
                WQ.isStuckCauseNull = false;
                isGotPacket = true;
            }
        }
    }

    private static boolean getPacket(AccessibilityNodeInfo rootNode, boolean isSelfPacket) {
        Log.i(TAG, "getPacket");
        if (rootNode == null) {
            Log.i(TAG, "getPacket rootNode == null");
            return false;
        }
        boolean result = false;
        List<AccessibilityNodeInfo> packetList = rootNode.findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_DIALOG_PACKET);
        if (!packetList.isEmpty()) {
            for (int i = packetList.size() - 1; i >= 0; i--) {
                List<AccessibilityNodeInfo> packetTextList = packetList.get(i).findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_DIALOG_PACKET_TEXT);
                if (!packetTextList.isEmpty()) {
                    if (isSelfPacket) {
                        if (packetTextList.get(0).getText().toString().contains(WQ.WT_SEE_PACKET)) {
                            packetList.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            result = true;
                        }
                    } else {
                        if (packetTextList.get(0).getText().toString().contains(WQ.WT_GET_PACKET)) {
                            packetList.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            result = true;
                        }
                    }
                }
            }
        }
        return result;
    }

    private static boolean openPacket(AccessibilityNodeInfo rootNode) {
        Log.i(TAG, "openPacket");
        if (rootNode == null) {
            Log.i(TAG, "openPacket rootNode == null");
            return false;
        }
        boolean result = false;
        List<AccessibilityNodeInfo> packetList = rootNode.findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_PACKET_DIALOG_BUTTON);
        if (!packetList.isEmpty()) {
            AccessibilityNodeInfo item = packetList.get(0);
            if (item.isClickable()) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                result = true;
            }
        }
        return result;
    }

    private static void judgeCurrentWindow(AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            return;
        }
        List<AccessibilityNodeInfo> dialogList = rootNode.findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_LIST_DIALOG);
        if (!dialogList.isEmpty()) {
            WQ.currentWindow = WQ.W_chatListWindow;
        } else {
            if (foundViewID(rootNode, WQ.WID_CHAT_DIALOG_PACKET_TEXT)) {
                WQ.currentWindow = WQ.W_chatWindow;
            } else if (foundViewID(rootNode, WQ.WID_CHAT_PACKET_DIALOG_BUTTON)) {
                WQ.currentWindow = WQ.W_packetWindow;
            } else {
                WQ.currentWindow = WQ.W_otherWindow;
            }
        }
        Log.i(TAG, "judgeCurrentWindow currentWindow = " + WQ.currentWindow);
    }

    private static boolean foundViewID(AccessibilityNodeInfo rootNode, String viewID) {
        boolean result = rootNode.findAccessibilityNodeInfosByViewId(viewID).isEmpty();
        Log.i(TAG, "foundViewID result = " + result);
        if (result) {
            return false;
        } else {
            return true;
        }
    }

}
