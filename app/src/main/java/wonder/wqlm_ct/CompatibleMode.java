package wonder.wqlm_ct;

import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by feeling on 2018/1/20.
 */

public class CompatibleMode {

    private final static String TAG = "wonder:CompatibleMode";

    public static void dealWindowStateChanged(String className, AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            return;
        }
        if (className.equals(WQ.WCN_LAUNCHER)) {
            // 聊天页面
            if (Config.isGotPacketSelf && WQ.currentSelfPacketStatus == WQ.W_openedPayStatus) {
                WQ.setCurrentSelfPacketStatus(WQ.W_intoChatDialogStatus);
                getSelfPacket(rootNode);
            } else {
                getPacket(rootNode);
            }
        } else if (className.equals(WQ.WCN_PACKET_RECEIVE)) {
            // 打开红包
            if (Config.isGotPacketSelf && WQ.currentSelfPacketStatus == WQ.W_intoChatDialogStatus) {
                openPacket(rootNode);
                WQ.setCurrentSelfPacketStatus(WQ.W_gotSelfPacketStatus);
            } else {
                openPacket(rootNode);
            }
        } else if (className.equals(WQ.WCN_PACKET_SEND)) {
            WQ.setCurrentSelfPacketStatus(WQ.W_openedPacketSendStatus);
        } else if (className.equals((WQ.WCN_PACKET_PAY))) {
            if (WQ.currentSelfPacketStatus == WQ.W_openedPacketSendStatus) {
                WQ.setCurrentSelfPacketStatus(WQ.W_openedPayStatus);
            }
        } else if (className.equals(WQ.WCN_PACKET_DETAIL)) {
            if (WQ.currentSelfPacketStatus == WQ.W_gotSelfPacketStatus) {
                AccessibilityHelper.performBack(WQAccessibilityService.getService());
                WQ.setCurrentSelfPacketStatus(WQ.W_otherStatus);
            }
        }
    }

    public static void dealWindowContentChanged(AccessibilityNodeInfo rooNode) {
        dealChatListAndWindow(rooNode);
    }

    private static boolean openPacket(AccessibilityNodeInfo rootNode) {
        Log.i(TAG, "openPacket!");
        clickAllView(rootNode);
        return true;
    }

    private static boolean getPacket(AccessibilityNodeInfo rootNode) {
        return inputClickByText(rootNode, WQ.WT_GET_PACKET);
    }

    private static void dealChatListAndWindow(AccessibilityNodeInfo rootNode) {
        Log.i(TAG, "dealChatListAndWindow");
        if (rootNode == null) {
            return;
        }
        if (getPacket(rootNode)) {
            return;
        }
        // inputClickByViewIDAndText(rootNode, WQ.WID_CHAT_LIST_MESSAGE_TEXT, WQ.WT_PACKET);
        // inputClickByViewIDAndText(rootNode, WQ.WID_CHAT_LIST_MESSAGE_NUM, WQ.WT_PACKET);
        // inputClickByViewIDAndText(rootNode, WQ.WID_CHAT_LIST_MESSAGE_POT, WQ.WT_PACKET);
        AccessibilityHelper.clickNewMessage(rootNode);
    }

    private static boolean inputClickByText(AccessibilityNodeInfo rootNode, String content) {
        Log.i(TAG, "inputClickByText! Text = " + content);
        boolean result = false;
        if (rootNode != null) {
            List<AccessibilityNodeInfo> nodeInfoList = rootNode.findAccessibilityNodeInfosByText(content);
            if (!nodeInfoList.isEmpty()) {
                AccessibilityNodeInfo item;
                for (int i = nodeInfoList.size() - 1; i >= 0; i--) {
                    item = nodeInfoList.get(i);
                    AccessibilityHelper.performClick(item);
                    result = true;
                }
            }
        }
        return result;
    }

    private static void clickAllView(AccessibilityNodeInfo nodeInfo) {
        Log.i(TAG, "clickAllView!");
        if (nodeInfo.getChildCount() == 0) {
            if (AccessibilityHelper.performClick(nodeInfo)) {
                return;
            }
        } else {
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                if (nodeInfo.getChild(i)!= null) {
                    clickAllView(nodeInfo.getChild(i));
                }
            }
        }
    }

    private static void inputClickByViewIDAndText(AccessibilityNodeInfo nodeInfo, String viewID, String text) {
        Log.i(TAG, "inputClickByViewIDAndText viewID = " + viewID + " text = " + text);
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId(viewID);
            if (!nodeInfoList.isEmpty()) {
                for (AccessibilityNodeInfo item : nodeInfoList) {
                    Log.i(TAG, "inputClickByViewIDAndText nodeInfoList = " + item.getText().toString());
                    if (item.getText().toString().contains(text)) {
                        AccessibilityHelper.performClick(item);
                    }
                }
            }
        }
    }

    private static void getSelfPacket(AccessibilityNodeInfo rootNode) {
        Log.i(TAG, "getSelfPacket");
        if (rootNode == null) {
            return;
        }
        List<AccessibilityNodeInfo> packetList = rootNode.findAccessibilityNodeInfosByText(WQ.WT_SEE_PACKET);
        if (!packetList.isEmpty()) {
            AccessibilityNodeInfo item;
            for (int i = packetList.size() - 1; i >= 0; i--) {
                item = packetList.get(i);
                AccessibilityHelper.performClick(item);
            }
        }
    }
}
