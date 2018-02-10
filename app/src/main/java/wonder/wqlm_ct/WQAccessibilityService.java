package wonder.wqlm_ct;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feeling on 2018/1/13.
 */

public class WQAccessibilityService extends AccessibilityService {

    private final static String TAG = "WQAccessibilityService";
    private static WQAccessibilityService service;
    private static Handler handler = new Handler();

    private HighSpeedMode highSpeedMode;
    private CompatibleMode compatibleMode;
    private Config config;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // flags = START_FLAG_RETRY;
        return START_STICKY;
        // return super.onStartCommand(intent, flags, startId);
    }

    private void initObj() {
        if (highSpeedMode == null) {
            highSpeedMode = new HighSpeedMode();
        }
        if (compatibleMode == null){
            compatibleMode = new CompatibleMode();
        }
        if (config == null) {
            config = Config.getConfig(getService());
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        String className = accessibilityEvent.getClassName().toString();
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        // AccessibilityNodeInfo rootNode = findCurrentWindows(accessibilityEvent, WQ.WECHAT);

        WonderLog.i(TAG, "onAccessibilityEvent eventType = " + eventType + "className = " + className);
        switch (eventType) {
            // 第一步：监听通知栏消息
            /*case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED: {
                if (WQ.isGotNotification) {
                    return;
                }
                WQ.isGotNotification = true;
                WonderLog.i(TAG, "通知栏消息改变");
                if (Tools.isLockScreen(this.getApplication())) {
                    Tools.wakeAndUnlock(this.getApplication());
                    WQ.isPreviouslyLockScreen = true;
                }
                AccessibilityHelper.openNotification(accessibilityEvent, WQ.WT_PACKET);
                WQ.isGotNotification = false;
                break;
            }*/
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: {
                WonderLog.i(TAG, "窗口状态改变 className = " + className);
                if (config.getRunningMode() == Config.compatibleMode) {
                    compatibleMode.dealWindowStateChanged(className, rootNode);
                } else {
                    highSpeedMode.dealWindowStateChanged(className, rootNode);
                }
                /*if (WQ.isPreviouslyLockScreen && WQ.currentAutoPacketStatus == WQ.W_rebackUIStatus) {
                    WQ.isPreviouslyLockScreen = false;
                    WQ.setCurrentAutoPacketStatus(WQ.W_waitStatus);
                    AccessibilityHelper.sleepAndLock(this.getApplication());
                }*/
                break;
            }
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: {
                // WonderLog.i(TAG, "窗口内容变化");
                if (config.getRunningMode() == Config.compatibleMode) {
                    // 联系人列表
                    compatibleMode.dealWindowContentChanged(rootNode);
                } else {
                    highSpeedMode.dealWindowContentChanged(className, rootNode);
                }
                break;
            }
            default:
                break;
        }
        if (rootNode != null) {
            rootNode.recycle();
        }
    }

    private AccessibilityNodeInfo findCurrentWindows(AccessibilityEvent accessibilityEvent, String title) {
        ArrayList<AccessibilityNodeInfo> windowListRoots = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<AccessibilityWindowInfo> windowList = getWindows();
            if (windowList.size() > 0) {
                for (AccessibilityWindowInfo window : windowList) {
                    WonderLog.i(TAG, "findCurrentWindows " + window.toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (window.getTitle() != null) {
                            if (window.getTitle().toString().equals(title)) {
                                return window.getRoot();
                            }
                        }
                    } else {
                        windowListRoots.add(window.getRoot());
                    }
                }
            }
        } else {
            AccessibilityNodeInfo windowSource = accessibilityEvent.getSource();
            AccessibilityNodeInfo windowChild;
            if (windowSource != null) {
                for (int i = 0; i < windowSource.getChildCount(); i++) {
                    windowChild = windowSource.getChild(i);
                    windowListRoots.add(windowChild);
                }
            } else {
                windowListRoots = null;
            }
        }
        if (windowListRoots != null) {
            WonderLog.i(TAG, "findCurrentWindows size = " + windowListRoots.size());
        }
        if (windowListRoots.size() > 0) {
            return windowListRoots.get(windowListRoots.size() - 1);
        } else {
            return null;
        }
    }

    private AccessibilityNodeInfo findRootInWindows(ArrayList<AccessibilityNodeInfo> windows, String ViewID) {
        for (int i = 0; i < windows.size(); i++) {
            if (windows.get(i) != null) {
                List<AccessibilityNodeInfo> packetList = windows.get(i).findAccessibilityNodeInfosByViewId(ViewID);
                if (!packetList.isEmpty()) {
                    if (packetList.get(0).isClickable()) {
                        return packetList.get(0);
                    }
                }
            }
        }
        WonderLog.i(TAG, "findRootInWindows == null");
        return null;
    }

    @Override
    public void onInterrupt() {
        WonderLog.i(TAG, "onInterrupt");
        Toast.makeText(this, "Hello World 模拟操作 服务被中断", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onServiceConnected() {
        WonderLog.i(TAG, "onServiceConnected");
        Toast.makeText(this, "Hello World 模拟操作 服务已连接", Toast.LENGTH_LONG).show();
        service = this;
        initObj();
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
