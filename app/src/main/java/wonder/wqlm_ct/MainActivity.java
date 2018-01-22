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
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "wonder:MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isNotificationListenerServiceEnabled(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAccessibilityServiceRunning()) {
                    Toast.makeText(getApplication(), "Hello World 1号 服务已启用", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplication(), "Hello World 1号 服务已停止", Toast.LENGTH_LONG).show();
                }
            }
        }, 3000);
    }

    private boolean isNotificationListenerServiceEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (packageNames.contains(context.getPackageName())) {
            Log.i(TAG, "isNotificationListenerServiceEnabled = true");
            Toast.makeText(this, "Hello World 2号 服务已启用", Toast.LENGTH_LONG).show();
            return true;
        }
        Log.i(TAG, "isNotificationListenerServiceEnabled = false");
        Toast.makeText(this, "Hello World 2号 服务已停止", Toast.LENGTH_LONG).show();
        context.startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
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
