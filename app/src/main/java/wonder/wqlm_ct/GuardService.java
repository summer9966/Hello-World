package wonder.wqlm_ct;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;


/**
 * Created by feeling on 2018/1/24.
 */

public class GuardService extends Service{
    private final static String TAG = "GuardService";

    MyBinder myBinder;
    private PendingIntent pendingIntent;
    MyServiceConnection guardServiceConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        if (myBinder == null) {
            myBinder = new MyBinder();
        }
        guardServiceConnection = new MyServiceConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.bindService(new Intent(this,FirstNotificationService.class), guardServiceConnection, Context.BIND_IMPORTANT);
        pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setWhen(System.currentTimeMillis())
                .setTicker("HelloWorld!")
                .setContentTitle("守护")
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

    class MyServiceConnection implements ServiceConnection {



        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            WonderLog.i(TAG, "通知监听服务连接成功");
            Toast.makeText(GuardService.this, "通知监听服务连接成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // 连接出现了异常断开了，监听通知服务被杀死了
            Toast.makeText(GuardService.this, "监听通知服务被杀死了", Toast.LENGTH_LONG).show();
            // 启动监听通知服务
            GuardService.this.startService(new Intent(GuardService.this,FirstNotificationService.class));
            GuardService.this.bindService(new Intent(GuardService.this,FirstNotificationService.class), guardServiceConnection, Context.BIND_IMPORTANT);
        }

    }

    class MyBinder extends GuardServiceInterface.Stub {

        @Override
        public String getProName() throws RemoteException {
            WonderLog.i(TAG, "getProName!");
            return "守护服务搬来的救兵！";
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
}
