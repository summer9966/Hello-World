package wonder.wqlm_ct;

import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Array;
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

    private final int timeResetSelfPacketStatus = 1000;
    private final int timeResetIsClickedNewMessageList = 500;
    private final int timeResetIsGotPacket = 500;
    private final int timeResetBackToMessageListStatus = 2000;

    private final int msgGetPacket = 0;
    private final int msgOpenPacket = 1;
    private final int msgResetSelfPacketStatus = 2;
    private final int msgResetIsClickedNewMessageList = 3;
    private final int msgResetIsGotPacket = 4;
    private final int msgResetBackToMessageListStatus = 5;

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
                    sortGetPacketList();
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
        for (int i = 0; i < nodeInfos.size(); i++) {
            if (nodeInfo.equals(nodeInfos.get(i))) {
                result = true;
                break;
            }
        }
        WonderLog.i(TAG, "isHasSameNodeInfo result = " + result);
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
                    case msgResetSelfPacketStatus:
                        WQ.setCurrentSelfPacketStatus(WQ.W_otherStatus);
                        break;
                    case msgResetIsClickedNewMessageList:
                        WQ.isClickedNewMessageList = false;
                        break;
                    case msgResetIsGotPacket:
                        WQ.isGotPacket = false;
                        break;
                    case msgResetBackToMessageListStatus:
                        WQ.backtoMessageListStatus = WQ.backtoMessageListOther;
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

    public void resetSelfPacketStatus() {
        sendHandlerMessage(msgResetSelfPacketStatus, timeResetSelfPacketStatus);
    }

    public void resetIsClickedNewMessageList() {
        sendHandlerMessage(msgResetIsClickedNewMessageList, timeResetIsClickedNewMessageList);
    }

    public void resetIsGotPacket() {
        sendHandlerMessage(msgResetIsGotPacket, timeResetIsGotPacket);
    }

    public void resetBacktoMessageListStatus() {
        sendHandlerMessage(msgResetBackToMessageListStatus, timeResetBackToMessageListStatus);
    }

    private int getBottomFromNodeInfo(AccessibilityNodeInfo nodeInfo) {
        Rect rect = getRectFromNodeInfo(nodeInfo);
        WonderLog.i(TAG, "getBottomFromNodeInfo bottom = " + rect.bottom);
        return rect.bottom;
    }

    private void sortGetPacketList() {
        if (getPacketList.size() == 1) {
            return;
        }
        ArrayList<AccessibilityNodeInfo> tempGetPacketList = new ArrayList<>();
        int[] nodeInfosBottom = new int[getPacketList.size()];
        int[] nodeInfosIndex = new int[getPacketList.size()];
        for (int i = 0; i < getPacketList.size(); i ++) {
            nodeInfosBottom[i] = getBottomFromNodeInfo(getPacketList.get(i));
            nodeInfosIndex[i] = i;
            tempGetPacketList.add(getPacketList.get(i));
        }
        getPacketList.clear();
        insertSort(nodeInfosBottom, nodeInfosIndex);
        for (int i = 0; i < tempGetPacketList.size(); i++) {
            getPacketList.add(tempGetPacketList.get(nodeInfosIndex[i]));
            WonderLog.i(TAG, "sortGetPacketList nodeInfoBottom[" + i +"] = "
                    + getBottomFromNodeInfo(getPacketList.get(i)));
        }
    }

    private void insertSort(int[] a, int[] b) {
        // nodeInofs的数量一般小于10，插入排序效率较高
        int i, j, insertNoteA, insertNoteB;             // 要插入的数据
        for (i = 1; i < a.length; i++) {                // 从数组的第二个元素开始循环将数组中的元素插入
            insertNoteA = a[i];                         // 设置数组中的第2个元素为第一次循环要插入的数据
            insertNoteB = b[i];
            j = i - 1;
            while (j >= 0 && insertNoteA < a[j]) {
                a[j + 1] = a[j];             // 如果要插入的元素小于第j个元素,就将第j个元素向后移动
                b[j + 1] = b[j];
                j--;
            }
            a[j + 1] = insertNoteA;          // 直到要插入的元素不小于第j个元素,将insertNote插入到数组中
            b[j + 1] = insertNoteB;
        }
    }

}
