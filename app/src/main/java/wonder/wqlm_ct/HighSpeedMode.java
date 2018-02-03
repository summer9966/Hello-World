package wonder.wqlm_ct;

import android.os.Handler;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import static java.lang.Thread.sleep;


/**
 * Created by feeling on 2018/1/20.
 */

public class HighSpeedMode {
    private final static String TAG = "HighSpeedMode";

    private static boolean isGotPacket = false;
    private static Handler handler = new Handler();
    private EventScheduling eventScheduling;

    public HighSpeedMode() {
        if (eventScheduling == null) {
            eventScheduling = new EventScheduling();
        }
    }

    public void dealWindowStateChanged(String className, final AccessibilityNodeInfo rootNode) {
        WonderLog.i(TAG, "dealWindowStateChanged");
        if (className.equals(WQ.WCN_LAUNCHER)) {
            // 聊天页面
            if (WQ.backtoMessageListStatus == WQ.backtoMessageListReceiveUI) {
                AccessibilityHelper.performBack(WQAccessibilityService.getService());
                WQ.backtoMessageListStatus = WQ.backtoMessageListChatDialog;
                return;
            } else if (WQ.backtoMessageListStatus == WQ.backtoMessageListChatDialog) {
                return;
            }
            if (Config.isGotPacketSelf && WQ.currentSelfPacketStatus == WQ.W_openedPayStatus) {
                WQ.setCurrentSelfPacketStatus(WQ.W_intoChatDialogStatus);
                getPacket(rootNode, true);
            } else {
                getPacket(rootNode, false);
            }
        } else if (className.equals(WQ.WCN_PACKET_RECEIVE)) {
            // 打开红包
            WonderLog.i(TAG, "dealWindowStateChanged 打开红包页面");
            if (Config.isGotPacketSelf && WQ.currentSelfPacketStatus == WQ.W_intoChatDialogStatus) {
                if (openPacket(rootNode)) {
                    WQ.setCurrentSelfPacketStatus(WQ.W_gotSelfPacketStatus);
                }
            } else {
                if (openPacket(rootNode)) {
                    isGotPacket = true;
                }
            }
            WQ.isClickedNewMessageList = false;
            WQ.isGotPacket = false;
        } else if (className.equals(WQ.WCN_PACKET_SEND)) {
            WQ.setCurrentSelfPacketStatus(WQ.W_openedPacketSendStatus);
        } else if (className.equals((WQ.WCN_PACKET_PAY))) {
            if (WQ.currentSelfPacketStatus == WQ.W_openedPacketSendStatus) {
                WQ.setCurrentSelfPacketStatus(WQ.W_openedPayStatus);
            }
        } else if (className.equals(WQ.WCN_PACKET_DETAIL)) {
            WonderLog.i(TAG, "dealWindowStateChanged 红包详情页面");
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

    public void dealWindowContentChanged(String className, AccessibilityNodeInfo rootNode) {
        WonderLog.i(TAG, "dealWindowContentChanged");
        if (WQ.backtoMessageListStatus == WQ.backtoMessageListChatDialog) {
            if (AccessibilityHelper.clickMessage(rootNode)) {
                WQ.backtoMessageListStatus = WQ.backtoMessageListOther;
            }
            return;
        } else if (WQ.backtoMessageListStatus >= WQ.backtoMessageListReceiveUI) {
            return;
        }

        if (AccessibilityHelper.clickNewMessage(rootNode)) {
            WQ.isClickedNewMessageList = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WQ.isClickedNewMessageList = false;
                }
            }, 500);
            return;
        }
        if (getPacket(rootNode, false)) {
            WQ.isGotPacket = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WQ.isGotPacket = false;
                }
            }, 500);
            return;
        }

    }

    private boolean getPacket(AccessibilityNodeInfo rootNode, boolean isSelfPacket) {
        if (rootNode == null) {
            WonderLog.i(TAG, "getPacket rootNode == null");
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
                            // packetList.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            eventScheduling.addGetPacketList(packetList.get(i));
                            result = true;
                        }
                    } else {
                        if (packetTextList.get(0).getText().toString().contains(WQ.WT_GET_PACKET)) {
                            // packetList.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            eventScheduling.addGetPacketList(packetList.get(i));
                            result = true;
                        }
                    }
                }
            }
        }
        WonderLog.i(TAG, "getPacket result = " + result);
        return result;
    }

    private boolean openPacket(AccessibilityNodeInfo rootNode) {
        if (rootNode == null && WQ.backtoMessageListStatus == WQ.backtoMessageListOther) {
            AccessibilityHelper.performBack(WQAccessibilityService.getService());
            WQ.backtoMessageListStatus = WQ.backtoMessageListReceiveUI;
            WonderLog.w(TAG, "openPacket == null");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WQ.backtoMessageListStatus = WQ.backtoMessageListOther;
                }
            }, 2000);
            return false;
        } else if (WQ.backtoMessageListStatus >= WQ.backtoMessageListReceiveUI) {
            return false;
        }
        boolean result = false;
        List<AccessibilityNodeInfo> packetList = rootNode.findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_PACKET_DIALOG_BUTTON);
        if (!packetList.isEmpty()) {
            AccessibilityNodeInfo item = packetList.get(0);
            if (item.isClickable()) {
                // item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                eventScheduling.addOpenPacketList(item);
                result = true;
            }
        }
        WonderLog.i(TAG, "openPacket result = " + result);
        return result;
    }

}
