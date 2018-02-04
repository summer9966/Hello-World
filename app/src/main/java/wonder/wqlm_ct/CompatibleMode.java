package wonder.wqlm_ct;

import android.os.Handler;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by feeling on 2018/1/20.
 */

public class CompatibleMode {

    private final static String TAG = "CompatibleMode";

    private EventScheduling eventScheduling;
    private Config config;

    private static boolean isGotPacket = false;
    private static Handler handler = new Handler();

    public CompatibleMode() {
        if (eventScheduling == null) {
            eventScheduling = new EventScheduling();
        }
        if (config == null) {
            config = Config.getConfig(WQAccessibilityService.getService());
        }
    }

    public void dealWindowStateChanged(String className, AccessibilityNodeInfo rootNode) {
        if (className.equals(WQ.WCN_LAUNCHER)) {
            // 聊天页面
            if (config.getIsGotPacketSelf() && WQ.currentSelfPacketStatus == WQ.W_openedPayStatus) {
                WQ.setCurrentSelfPacketStatus(WQ.W_intoChatDialogStatus);
                getPacket(rootNode, true);
            } else {
                getPacket(rootNode, false);
            }
        } else if (className.equals(WQ.WCN_PACKET_RECEIVE)) {
            // 打开红包
            if (config.getIsGotPacketSelf() && WQ.currentSelfPacketStatus == WQ.W_intoChatDialogStatus) {
                openPacket(rootNode);
                WQ.setCurrentSelfPacketStatus(WQ.W_gotSelfPacketStatus);
            } else {
                if (openPacket(rootNode)) {
                    isGotPacket = true;
                }
            }
            WQ.isGotPacket = false;
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
            if (isGotPacket) {
                AccessibilityHelper.performBack(WQAccessibilityService.getService());
                isGotPacket = false;
            }
        }
    }

    public void dealWindowContentChanged(AccessibilityNodeInfo rootNode) {
        WonderLog.i(TAG, "dealWindowContentChanged");
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
        // AccessibilityHelper.clickNewMessage(rootNode);
    }

    private boolean openPacket(AccessibilityNodeInfo rootNode) {
        boolean result = false;
        if (rootNode != null) {
            WonderLog.i(TAG, "openPacket! " + rootNode.toString());
            List<AccessibilityNodeInfo> nodeInfos = rootNode
                    .findAccessibilityNodeInfosByText(WQ.WT_OPEN_SEND_A_PACKET);
            if (!nodeInfos.isEmpty()) {
                AccessibilityNodeInfo parent = nodeInfos.get(0).getParent();
                for (int i = 0; i < parent.getChildCount(); i++) {
                    AccessibilityNodeInfo nodeInfo = parent.getChild(i);
                    if (nodeInfo.isClickable()) {
                        eventScheduling.addOpenPacketList(nodeInfo);
                        result = true;
                    }
                }
            }
        }
        WonderLog.i(TAG, "openPacket result =  " + result);
        return result;
    }

    private boolean getPacket(AccessibilityNodeInfo rootNode, boolean isSelfPacket) {
        WonderLog.i(TAG, "getPacket");
        boolean result = false;
        if (rootNode != null) {
            List<AccessibilityNodeInfo> nodeInfoList;
            if (isSelfPacket) {
                nodeInfoList = rootNode.findAccessibilityNodeInfosByText(WQ.WT_SEE_PACKET);
            } else {
                nodeInfoList = rootNode.findAccessibilityNodeInfosByText(WQ.WT_GET_PACKET);
            }
            if (!nodeInfoList.isEmpty()) {
                for (int i = nodeInfoList.size() - 1; i >= 0; i--) {
                    // AccessibilityHelper.performClick(nodeInfoList.get(i));
                    eventScheduling.addGetPacketList(nodeInfoList.get(i));
                    result = true;
                }
            }
        }
        return result;
    }

    private boolean clickMessage(AccessibilityNodeInfo rootNode) {
        boolean result = false;
        if (rootNode == null) {
            WonderLog.i(TAG, "clickMessage rootNode == null");
            return false;
        }
        List<AccessibilityNodeInfo> nodeInfos = rootNode.findAccessibilityNodeInfosByText(WQ.WT_PACKET);
        if (!nodeInfos.isEmpty()) {
            for (int i = 0; i < nodeInfos.size(); i++) {
                AccessibilityNodeInfo nodeInfo = nodeInfos.get(i);
                WonderLog.i(TAG, "clickMessage nodeInfo = " + nodeInfo.toString());
                AccessibilityHelper.performClick(nodeInfo);
                result = true;
            }
        } else {
            WonderLog.i(TAG, "clickMessage isEmpty!");
        }
        WonderLog.i(TAG, "clickMessage result = " + result);
        return result;
    }

}
