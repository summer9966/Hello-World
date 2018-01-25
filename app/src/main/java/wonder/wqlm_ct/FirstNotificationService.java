package wonder.wqlm_ct;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

/**
 * Created by feeling on 2018/1/20.
 */


public class FirstNotificationService extends NotificationListenerService {
    private final static String TAG = "FirstNotificationService";

    MyBinder myBinder;
    private MyServiceConnection myServiceConnection;
    private PendingIntent pendingIntent;
    private static FirstNotificationService service;

    @Override
    public void onCreate() {
        WonderLog.i(TAG, "onCreate");
        if (Config.isUsedGuardService) {
            myServiceConnection = new MyServiceConnection();
            if (myBinder == null) {
                myBinder = new MyBinder();
            }
        }
        if (service == null) {
            service = new FirstNotificationService();
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WonderLog.i(TAG, "onStartCommand");

        if (Config.isUsedGuardService) {
            this.bindService(new Intent(this, GuardService.class), myServiceConnection, Context.BIND_IMPORTANT);
        }
        pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        Notification notification = new Notification.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setWhen(System.currentTimeMillis())
                .setTicker("HelloWorld!")
                .setContentTitle("通知监听")
                .setContentText("服务正在运行")
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build();

        //设置service为前台进程，避免手机休眠时系统自动杀掉该服务
        startForeground(startId, notification);

        // flags = START_FLAG_RETRY;
        return START_STICKY;
        // return super.onStartCommand(intent, flags, startId);
    }

    /*@Override
    public IBinder onBind(Intent intent) {
        WonderLog.i(TAG, "onBind");
        Toast.makeText(getApplication(), "监听服务已绑定", Toast.LENGTH_SHORT).show();
        return super.onBind(intent);
    }*/

    @Override
    public void onListenerConnected() {
        WonderLog.i(TAG, "onListenerConnected");
        Toast.makeText(this, "Hello World 2号 服务已连接", Toast.LENGTH_LONG).show();
        super.onListenerConnected();
    }

    @Override
    public void onListenerDisconnected() {
        WonderLog.i(TAG, "onListenerDisconnected");
        Toast.makeText(this, "Hello World 2号 服务已断开", Toast.LENGTH_LONG).show();
        super.onListenerDisconnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (WQ.isGotNotification || WQ.isClickedNewMessageList
                || WQ.isGotPacket) {
            super.onNotificationPosted(sbn);
            WonderLog.i(TAG, "onNotificationPosted: return ");
            return;
        }
        WonderLog.i(TAG, "onNotificationPosted: " + sbn.getPackageName().toString()
                + " " + sbn.getNotification().extras.getString(Notification.EXTRA_TEXT));
        WQ.isGotNotification = true;
        if (Tools.isLockScreen(this.getApplication())) {
            Tools.wakeAndUnlock(this.getApplication());
            WQ.isPreviouslyLockScreen = true;
        }
        if (AccessibilityHelper.openNotification(sbn, WQ.WECHAT_PACKAGE_NAME, WQ.WT_PACKET)) {
            cancelNotification(sbn.getKey());
        }
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        WonderLog.i(TAG, "onNotificationRemoved: " + sbn.toString());
        WQ.isGotNotification = false;
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onDestroy() {
        WonderLog.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        WonderLog.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            WonderLog.i("castiel", "守护服务连接成功");
            Toast.makeText(FirstNotificationService.this, "守护服务连接成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // 连接出现了异常断开了，守护被杀掉了
            Toast.makeText(FirstNotificationService.this, "守护服务被干掉", Toast.LENGTH_LONG).show();
            // 启动守护服务
            FirstNotificationService.this.startService(new Intent(FirstNotificationService.this, GuardService.class));
            FirstNotificationService.this.bindService(new Intent(FirstNotificationService.this, GuardService.class),
                    myServiceConnection, Context.BIND_IMPORTANT);
        }

    }


    class MyBinder extends GuardServiceInterface.Stub {

        @Override
        public String getProName() throws RemoteException {
            WonderLog.i(TAG, "getProName!");
            return "监听服务搬来的救兵";
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            return;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        WonderLog.i(TAG, "onBind!");
        return myBinder;
    }

    public static void toggleNotificationListenerService(Context context) {
        WonderLog.i(TAG,"toggleNotificationListenerService");
        /*PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(context, wonder.wqlm_ct.FirstNotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(
                new ComponentName(context, wonder.wqlm_ct.FirstNotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);*/
        requestRebind(new ComponentName(context, FirstNotificationService.class));
    }

    public static FirstNotificationService getService() {
        if (service != null) {
            return service;
        }
        return null;
    }

}
