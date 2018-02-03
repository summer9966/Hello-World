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
        initBackThread();
    }

    public void addGetPacketList(AccessibilityNodeInfo nodeInfo) {
        WonderLog.i(TAG, "addGetPacketList delayedTime = " + Config.delayedTime);
        if (getPacketList != null) {
            if (getPacketList.size() == 0) {
                WonderLog.i(TAG, nodeInfo.toString());
                getPacketList.add(nodeInfo);
                sendHandlerMessage(msgGetPacket, calculateDelayedTime(Config.delayedTime));
            } else {
                if (!isHasSameNodeInfo(nodeInfo, getPacketList)) {
                    WonderLog.i(TAG, nodeInfo.toString());
                    getPacketList.add(nodeInfo);
                    sendHandlerMessage(msgGetPacket, calculateDelayedTime(Config.delayedTime));
                }
            }
        }
    }

    public void addOpenPacketList(AccessibilityNodeInfo nodeInfo) {
        WonderLog.i(TAG, "addOpenPacketList delayedTime = " + Config.delayedTime);
        if (openPacketList != null) {
            if (openPacketList.size() == 0) {
                openPacketList.add(nodeInfo);
                sendHandlerMessage(msgOpenPacket, calculateDelayedTime(Config.delayedTime));
            } else {
                if (nodeInfo != openPacketList.get(getLastIndex(openPacketList))) {
                    openPacketList.add(nodeInfo);
                    sendHandlerMessage(msgOpenPacket, calculateDelayedTime(Config.delayedTime));
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
        Rect rect = getRectFromNodeInfo(nodeInfo);
        boolean result = false;
        for (int i = 0; i < nodeInfos.size(); i++) {
            if (rect.equals(getRectFromNodeInfo(nodeInfos.get(i)))) {
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
                            getPacketList.get(getLastIndex(getPacketList))
                                    .performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            removeLastGetPacketList();
                        }
                        break;
                    case msgOpenPacket:
                        if (isSafeToArrayList(openPacketList)) {
                            openPacketList.get(getLastIndex(openPacketList))
                                    .performAction(AccessibilityNodeInfo.ACTION_CLICK);
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
        if (Config.isUsedDelayed) {
            if (Config.isUsedRandomDelayed) {
                // 除以2，是因为分别在聊天界面和红包拆开界面分别延时
                time = generateRandomNum(delayedTime) / 2;
            } else {
                time = delayedTime / 2;
            }
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
