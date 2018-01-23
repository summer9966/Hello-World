package wonder.wqlm_ct;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "wonder:MainActivity";

    private CheckBox cb_modeChoice, cb_notification, cb_accessibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        if (isNotificationListenerServiceEnabled(this)) {
            cb_notification.setChecked(true);
        } else {
            cb_notification.setChecked(false);
        }
        if (isAccessibilityServiceRunning()) {
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

    private boolean isNotificationListenerServiceEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (packageNames.contains(context.getPackageName())) {
            Log.i(TAG, "isNotificationListenerServiceEnabled = true");
            return true;
        }
        Log.i(TAG, "isNotificationListenerServiceEnabled = false");
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
