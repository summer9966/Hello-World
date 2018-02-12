package wonder.wqlm_ct;

import android.os.Handler;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;


/**
 * Created by feeling on 2018/1/20.
 */

public class HighSpeedMode {
    private final static String TAG = "HighSpeedMode";

    private EventScheduling eventScheduling;
    private Config config;

    private static boolean isGotPacket = false;


    public HighSpeedMode() {
        if (eventScheduling == null) {
            eventScheduling = new EventScheduling();
        }
        if (config == null) {
            config = Config.getConfig(WQAccessibilityService.getService());
        }
        WQ.initWQ(WQAccessibilityService.getService());
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
            if (config.getIsGotPacketSelf() && WQ.currentSelfPacketStatus == WQ.W_openedPayStatus) {
                WQ.setCurrentSelfPacketStatus(WQ.W_intoChatDialogStatus);
                getPacket(rootNode, true);
            } else {
                getPacket(rootNode, false);
            }
        } else if (className.equals(WQ.WCN_PACKET_RECEIVE)) {
            // 打开红包
            WonderLog.i(TAG, "dealWindowStateChanged 打开红包页面");
            if (config.getIsGotPacketSelf() && WQ.currentSelfPacketStatus == WQ.W_intoChatDialogStatus) {
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
            if (WQ.currentSelfPacketStatus <= WQ.W_otherStatus) {
                WQ.setCurrentSelfPacketStatus(WQ.W_openedPacketSendStatus);
            }
        } else if (className.equals((WQ.WCN_PACKET_PAY))) {
            if (WQ.currentSelfPacketStatus == WQ.W_openedPacketSendStatus) {
                WQ.setCurrentSelfPacketStatus(WQ.W_openedPayStatus);
            }
        } else if (className.equals(WQ.WCN_PACKET_DETAIL)) {
            WonderLog.i(TAG, "dealWindowStateChanged 红包详情页面");
            if (WQ.currentSelfPacketStatus != WQ.W_otherStatus) {
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
            if (clickMessage(rootNode)) {
                WQ.backtoMessageListStatus = WQ.backtoMessageListOther;
            }
            return;
        } else if (WQ.backtoMessageListStatus >= WQ.backtoMessageListReceiveUI) {
            return;
        }

        if (clickNewMessage(rootNode)) {
            WQ.isClickedNewMessageList = true;
            eventScheduling.resetIsClickedNewMessageList();
            return;
        }
        if (getPacket(rootNode, false)) {
            WQ.isGotPacket = true;
            eventScheduling.resetIsGotPacket();
            return;
        }

    }

    private boolean getPacket(AccessibilityNodeInfo rootNode, boolean isSelfPacket) {
        if (rootNode == null) {
            WonderLog.i(TAG, "getPacket rootNode == null");
            return false;
        }
        boolean result = false;
        List<AccessibilityNodeInfo> packetList = rootNode
                .findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_DIALOG_PACKET);
        if (!packetList.isEmpty()) {
            for (int i = packetList.size() - 1; i >= 0; i--) {
                List<AccessibilityNodeInfo> packetTextList = packetList
                        .get(i).findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_DIALOG_PACKET_TEXT);
                if (!packetTextList.isEmpty()) {
                    if (isSelfPacket) {
                        if (packetTextList.get(0).getText().toString().contains(WQ.WT_SEE_PACKET)) {
                            if (config.getIsUsedKeyWords()) {
                                result = isInKeyWordsMode(packetList, i);
                            } else {
                                // packetList.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                eventScheduling.addGetPacketList(packetList.get(i));
                                result = true;
                            }
                        }
                    } else {
                        if (packetTextList.get(0).getText().toString().contains(WQ.WT_GET_PACKET)) {
                            if (config.getIsUsedKeyWords()) {
                                result = isInKeyWordsMode(packetList, i);
                            } else {
                                // packetList.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                eventScheduling.addGetPacketList(packetList.get(i));
                                result = true;
                            }
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
            eventScheduling.resetBacktoMessageListStatus();
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

    private boolean isInKeyWordsMode(List<AccessibilityNodeInfo> nodeInfos, int index) {
        List<AccessibilityNodeInfo> packetContentList = nodeInfos.get(index)
                .findAccessibilityNodeInfosByViewId(WQ.WID_CHAT_DIALOG_PACKET_CONTENT);
        if (!packetContentList.isEmpty()) {
            if (!isContainKeyWords(config.getPacketKeyWords(),
                    packetContentList.get(0).getText().toString())) {
                // packetList.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                eventScheduling.addGetPacketList(nodeInfos.get(index));
                return true;
            }
        }
        return false;
    }

    private boolean isContainKeyWords(String keyWords, String content) {
        boolean result = false;
        String[] arrayListKeyWords = keyWords.split("、");
        for (String keyWord : arrayListKeyWords) {
            WonderLog.i(TAG, "isContainKeyWords keyWord = " + keyWord);
            if (content.contains(keyWord)) {
                result = true;
                break;
            }
        }
        WonderLog.i(TAG, "isContainKeyWords result = " + result);
        return result;
    }

    private boolean clickNewMessage(AccessibilityNodeInfo nodeInfo) {
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

    private boolean clickMessage(AccessibilityNodeInfo nodeInfo) {
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

}
