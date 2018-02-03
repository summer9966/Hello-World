package wonder.wqlm_ct;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private CheckBox cb_modeChoice, cb_notification, cb_accessibility;
    private CheckBox cb_getPacketSelf, cb_usedDelayed, cb_usedRandomDelayed, cb_useKeyWords;
    private EditText et_delayedTime;
    private Button bt_test;

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
        cb_getPacketSelf = findViewById(R.id.cb_getPacketSelf);
        cb_usedDelayed = findViewById(R.id.cb_usedDelayed);
        cb_usedRandomDelayed = findViewById(R.id.cb_usedRandomDelayed);
        cb_useKeyWords = findViewById(R.id.cb_usedKeyWords);
        et_delayedTime = findViewById(R.id.et_delayedTime);
        bt_test = findViewById(R.id.bt_test);

        initViewState();
        setClickListener();
        setCheckedListener();
    }

    private void initViewState() {
        Config.getAllConfig(MainActivity.this);
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
        if (Config.isGotPacketSelf) {
            cb_getPacketSelf.setChecked(true);
        } else {
            cb_getPacketSelf.setChecked(false);
        }
        if (Config.isUsedDelayed) {
            cb_usedDelayed.setChecked(true);
        } else {
            cb_usedDelayed.setChecked(false);
        }
        if (Config.isUsedRandomDelayed) {
            cb_usedRandomDelayed.setChecked(true);
        } else {
            cb_usedRandomDelayed.setChecked(false);
        }
        et_delayedTime.setText(String.valueOf(Config.delayedTime));
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplication(), "当前安卓系统版本过低，请手动设置本应用的通知读取权限",
                            Toast.LENGTH_LONG).show();
                    boolean isNotificationServiceRunning = Tools.isServiceRunning(getApplication(),
                            WQ.SELF_PACKAGE_NAME + "." + WQ.SELFCN_NOTIFICATION);
                    cb_notification.setChecked(isNotificationServiceRunning);
                }
            }
        });
        cb_accessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        bt_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WQNotificationService.restarNotificationListenerService(getApplication());
            }
        });
    }

    private void setCheckedListener() {
        cb_getPacketSelf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Config.isGotPacketSelf = true;
                } else {
                    Config.isGotPacketSelf = false;
                }
                Config.saveAllConfig(MainActivity.this);
            }
        });
        cb_usedDelayed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Config.isUsedDelayed = true;
                } else {
                    Config.isUsedDelayed = false;
                }
                Config.saveAllConfig(MainActivity.this);
            }
        });
        cb_usedRandomDelayed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Config.isUsedRandomDelayed = true;
                } else {
                    Config.isUsedRandomDelayed = false;
                }
                Config.saveAllConfig(MainActivity.this);
            }
        });
    }

    private void getEditTextContent() {
        Config.delayedTime = Integer.parseInt(et_delayedTime.getText().toString());
        WonderLog.i(TAG, "getEditTextContent delayTime = " + Config.delayedTime);
        Config.saveAllConfig(MainActivity.this);
    }

    @Override
    protected void onResume() {
        initViewState();
        super.onResume();
    }

    @Override
    protected void onPause() {
        getEditTextContent();
        super.onPause();
    }

    @Override
    protected void onStop() {
        getEditTextContent();
        super.onStop();
    }

}
