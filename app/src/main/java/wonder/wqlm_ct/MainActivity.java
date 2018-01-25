package wonder.wqlm_ct;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private CheckBox cb_modeChoice, cb_notification, cb_accessibility;
    ServiceConnection serviceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (serviceConnection == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    WonderLog.i(TAG, "Notification服务绑定成功");
                    Toast.makeText(getApplication(), "Notification服务绑定成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    WonderLog.i(TAG, "Notification服务被断开");
                    Toast.makeText(getApplication(), "Notification服务被断开", Toast.LENGTH_SHORT).show();
                }
            };
        }

        startService(new Intent(this,FirstNotificationService.class));
        this.bindService(new Intent(this,FirstNotificationService.class), serviceConnection, Context.BIND_IMPORTANT);

        initObj();

    }

    private void initObj() {
        cb_modeChoice = findViewById(R.id.cb_modeChoice);
        cb_notification = findViewById(R.id.cb_notification);
        cb_accessibility = findViewById(R.id.cb_accessibility);

        initCheckBoxState();
        setClickListener();
    }

    private void initCheckBoxState() {
        if (Config.runningMode == Config.compatibleMode) {
            cb_modeChoice.setChecked(true);
        } else {
            cb_modeChoice.setChecked(false);
        }
        if (Tools.isServiceRunning(this, WQ.SELF_PACKAGE_NAME + "."
                + WQ.SELFCN_NOTIFICATION)) {
            cb_notification.setChecked(true);
        } else {
            cb_notification.setChecked(false);
        }
        if (Tools.isServiceRunning(this, WQ.SELF_PACKAGE_NAME + "."
                + WQ.SELFCN_ACCESSBILITY)) {
            cb_accessibility.setChecked(true);
        } else {
            cb_accessibility.setChecked(false);
        }
    }

    private void setClickListener() {
        cb_modeChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_modeChoice.isChecked()) {
                    Config.runningMode = Config.compatibleMode;
                } else {
                    Config.runningMode = Config.highSpeedMode;
                }
            }
        });
        cb_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getApplication().startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            }
        });
        cb_accessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getApplication().startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
    }

    @Override
    protected void onResume() {
        initCheckBoxState();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean isNotificationListenerServiceEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (packageNames.contains(context.getPackageName())) {
            WonderLog.i(TAG, "isNotificationListenerServiceEnabled = true");
            return true;
        }
        WonderLog.i(TAG, "isNotificationListenerServiceEnabled = false");
        return false;
    }

    /**
     * 判断当前服务是否正在运行
     * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isAccessibilityServiceRunning() {
        if(WQAccessibilityService.getService() == null) {
            return false;
        }
        AccessibilityManager accessibilityManager = (AccessibilityManager) WQAccessibilityService.getService().getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityServiceInfo info = WQAccessibilityService.getService().getServiceInfo();
        if(info == null) {
            return false;
        }
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        Iterator<AccessibilityServiceInfo> iterator = list.iterator();

        boolean isConnect = false;
        while (iterator.hasNext()) {
            AccessibilityServiceInfo i = iterator.next();
            if(i.getId().equals(info.getId())) {
                isConnect = true;
                break;
            }
        }
        if(!isConnect) {
            return false;
        }
        return true;
    }

}
