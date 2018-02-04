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

    private Config config;

    private CheckBox cb_modeChoice, cb_notification, cb_accessibility;
    private CheckBox cb_getPacketSelf, cb_usedDelayed, cb_usedRandomDelayed, cb_useKeyWords;
    private EditText et_delayedTime, et_keyWords;
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
        et_keyWords = findViewById(R.id.et_keyWords);
        bt_test = findViewById(R.id.bt_test);

        if (config == null) {
            config = Config.getConfig(MainActivity.this);
        }
        initViewState();
        setClickListener();
    }

    private void initViewState() {
        if (Tools.getWeChatVersion(MainActivity.this).equals(Config.currentSupportWVersion)) {
            if (config.getRunningMode() == Config.compatibleMode) {
                cb_modeChoice.setChecked(true);
                cb_useKeyWords.setChecked(false);
                config.saveIsUsedKeyWords(false);
            } else {
                cb_modeChoice.setChecked(false);
            }
        } else {
            cb_modeChoice.setChecked(true);
            config.saveRunningMode(Config.compatibleMode);
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
        if (config.getIsGotPacketSelf()) {
            cb_getPacketSelf.setChecked(true);
        } else {
            cb_getPacketSelf.setChecked(false);
        }
        if (config.getIsUsedDelayed()) {
            cb_usedDelayed.setChecked(true);
        } else {
            cb_usedDelayed.setChecked(false);
        }
        if (config.getIsUsedRandomDelayed()) {
            cb_usedRandomDelayed.setChecked(true);
        } else {
            cb_usedRandomDelayed.setChecked(false);
        }
        if (config.getIsUsedKeyWords()) {
            cb_useKeyWords.setChecked(true);
        } else {
            cb_useKeyWords.setChecked(false);
        }
        et_delayedTime.setText(String.valueOf(config.getDelayedTime()));
        et_keyWords.setText(config.getPacketKeyWords());

        bt_test.setVisibility(View.INVISIBLE);
    }

    private void setClickListener() {
        cb_modeChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_modeChoice.isChecked()) {
                    config.saveRunningMode(Config.compatibleMode);
                    config.saveIsUsedKeyWords(false);
                    cb_useKeyWords.setChecked(false);
                } else {
                    if (Tools.getWeChatVersion(MainActivity.this)
                            .equals(Config.currentSupportWVersion)) {
                        config.saveRunningMode(Config.highSpeedMode);
                    } else {
                        cb_modeChoice.setChecked(true);
                        config.saveRunningMode(Config.compatibleMode);
                        Toast.makeText(MainActivity.this, "当前非兼容模式仅支持微信6.6.1版本",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        cb_getPacketSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_getPacketSelf.isChecked()) {
                    config.saveIsGotPacketSelf(true);
                } else {
                    config.saveIsGotPacketSelf(false);
                }
            }
        });
        cb_usedDelayed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_usedDelayed.isChecked()) {
                    config.saveIsUsedDelayed(true);
                    if (cb_usedRandomDelayed.isChecked()) {
                        cb_usedRandomDelayed.setChecked(false);
                        config.saveIsUsedRandomDelayed(false);
                    }
                } else {
                    config.saveIsUsedDelayed(false);
                }
            }
        });
        cb_usedRandomDelayed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_usedRandomDelayed.isChecked()) {
                    config.saveIsUsedRandomDelayed(true);
                    if (cb_usedDelayed.isChecked()) {
                        cb_usedDelayed.setChecked(false);
                        config.saveIsUsedDelayed(false);
                    }
                } else {
                    config.saveIsUsedRandomDelayed(false);
                }
            }
        });
        cb_useKeyWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_useKeyWords.isChecked()) {
                    if (config.getRunningMode() != Config.compatibleMode) {
                        config.saveIsUsedKeyWords(true);
                    } else {
                        cb_useKeyWords.setChecked(false);
                        config.saveIsUsedKeyWords(false);
                        Toast.makeText(MainActivity.this, "目前兼容模式下不支持关键字过滤",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    config.saveIsUsedKeyWords(false);
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
                // WQNotificationService.restarNotificationListenerService(getApplication());
            }
        });
    }

    private void getEditTextContent() {
        int delayedTime = Integer.parseInt(et_delayedTime.getText().toString());
        String packetKeyWords = et_keyWords.getText().toString();
        WonderLog.i(TAG, "getEditTextContent delayTime = " + delayedTime
                + "keyWords = " + packetKeyWords);
        config.savePacketKeyWords(packetKeyWords);
        config.saveDelayedTime(delayedTime);
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
