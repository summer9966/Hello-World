package wonder.wqlm_ct;

import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by feeling on 2018/1/27.
 */

public class EventScheduling {
    private final static String TAG = "EventScheduling";

    private Config config;

    private ArrayList<AccessibilityNodeInfo> getPacketList;
    private ArrayList<AccessibilityNodeInfo> openPacketList;

    private final int msgGetPacket = 0;
    private final int msgOpenPacket = 1;

    private HandlerThread checkMsgThread;
    private Handler checkMsgHandler;

    public  EventScheduling() {
        if (getPacketList == null) {
            getPacketList = new ArrayList<>();
        }
        if (openPacketList == null) {
            openPacketList = new ArrayList<>();
        }
        if (config == null) {
            config = Config.getConfig(WQAccessibilityService.getService());
        }
        initBackThread();
    }

    public void addGetPacketList(AccessibilityNodeInfo nodeInfo) {
        int delayedTime = config.getDelayedTime();
        WonderLog.i(TAG, "addGetPacketList delayedTime = " + delayedTime);
        if (getPacketList != null) {
            if (getPacketList.size() == 0) {
                WonderLog.i(TAG, "addGetPacketList " + nodeInfo.toString());
                getPacketList.add(nodeInfo);
                sendHandlerMessage(msgGetPacket, calculateDelayedTime(delayedTime));
            } else {
                if (!isHasSameNodeInfo(nodeInfo, getPacketList)) {
                    WonderLog.i(TAG, "addGetPacketList " + nodeInfo.toString());
                    getPacketList.add(nodeInfo);
                    sendHandlerMessage(msgGetPacket, calculateDelayedTime(delayedTime));
                }
            }
        }
    }

    public void addOpenPacketList(AccessibilityNodeInfo nodeInfo) {
        int delayedTime = config.getDelayedTime();
        WonderLog.i(TAG, "addOpenPacketList delayedTime = " + delayedTime);
        if (openPacketList != null) {
            if (openPacketList.size() == 0) {
                WonderLog.i(TAG, "addOpenPacketList " + nodeInfo.toString());
                openPacketList.add(nodeInfo);
                sendHandlerMessage(msgOpenPacket, calculateDelayedTime(delayedTime));
            } else {
                if (!isHasSameNodeInfo(nodeInfo, openPacketList)) {
                    WonderLog.i(TAG, "addOpenPacketList " + nodeInfo.toString());
                    openPacketList.add(nodeInfo);
                    sendHandlerMessage(msgOpenPacket, calculateDelayedTime(delayedTime));
                }
            }
        }
    }

    private void removeLastGetPacketList() {
        WonderLog.i(TAG, "removeLastGetPacketList");
        if (isSafeToArrayList(getPacketList)) {
            getPacketList.remove(getLastIndex(getPacketList));
        }
    }

    private void removeLastOpenPacketList() {
        WonderLog.i(TAG, "removeLastOpenPacketList");
        if (isSafeToArrayList(openPacketList)) {
            openPacketList.remove(getLastIndex(openPacketList));
        }
    }

    private boolean isSafeToArrayList(ArrayList<AccessibilityNodeInfo> nodeInfos) {
        if (nodeInfos != null) {
            if (nodeInfos.size() > 0) {
                return true;
            }
        }
        return false;
    }

    private int getLastIndex(ArrayList<AccessibilityNodeInfo> nodeInfos) {
        return (nodeInfos.size() - 1);
    }

    private Rect getRectFromNodeInfo(AccessibilityNodeInfo nodeInfo) {
        Rect rect= new Rect();
        nodeInfo.getBoundsInScreen(rect);
        return rect;
    }

    private boolean isHasSameNodeInfo(AccessibilityNodeInfo nodeInfo,
                                      ArrayList<AccessibilityNodeInfo> nodeInfos) {
        boolean result = false;
        // Rect rect = getRectFromNodeInfo(nodeInfo);
        /*for (int i = 0; i < nodeInfos.size(); i++) {
            if (rect.equals(getRectFromNodeInfo(nodeInfos.get(i)))) {
                result = true;
                break;
            }
        }*/
        for (int i = 0; i < nodeInfos.size(); i++) {
            if (nodeInfo.equals(nodeInfos.get(i))) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void sendHandlerMessage(int what, int delayedTime) {
        Message msg = checkMsgHandler.obtainMessage();
        msg.what = what;
        checkMsgHandler.sendMessageDelayed(msg, delayedTime);
    }

    private void initBackThread()
    {
        if (checkMsgHandler != null) {
            return;
        }
        checkMsgThread = new HandlerThread("check-message-coming");
        checkMsgThread.start();
        checkMsgHandler = new Handler(checkMsgThread.getLooper())
        {
            @Override
            public void handleMessage(Message msg) {
                WonderLog.i(TAG, "handleMessage msg.what = " + msg.what);
                switch (msg.what) {
                    case msgGetPacket:
                        if (isSafeToArrayList(getPacketList)) {
                            AccessibilityHelper.performClick(getPacketList
                                    .get(getLastIndex(getPacketList)));
                            removeLastGetPacketList();
                        }
                        break;
                    case msgOpenPacket:
                        if (isSafeToArrayList(openPacketList)) {
                            AccessibilityHelper.performClick(openPacketList
                                        .get(getLastIndex(openPacketList)));
                            removeLastOpenPacketList();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }


    private int calculateDelayedTime(int delayedTime) {
        int time;
        boolean isUsedDelayed = config.getIsUsedDelayed();
        boolean isUsedRandomDelayed = config.getIsUsedRandomDelayed();
        if (isUsedDelayed) {
            time = delayedTime / 2;         // 除以2，是因为分别在聊天界面和红包拆开界面分别延时
        } else if (isUsedRandomDelayed) {
            time = generateRandomNum(delayedTime) / 2;
        } else {
            // 不延时
            time = 0;
        }
        WonderLog.i(TAG, "calculateDelayedTime time = " + time);
        return time;
    }

    private int generateRandomNum(int n) {
        Random rand = new Random();
        return rand.nextInt(n + 1);
    }


}
