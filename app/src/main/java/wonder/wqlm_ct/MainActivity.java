package wonder.wqlm_ct;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;


public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private CheckBox cb_modeChoice, cb_notification, cb_accessibility;
    private Button bt_serviceRestart;

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
        bt_serviceRestart = findViewById(R.id.bt_serviceRestart);

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
        bt_serviceRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WQNotificationService.getWqNotificationService() != null) {
                    WQNotificationService.restarNotificationListenerService(getApplication());
                }
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

}
