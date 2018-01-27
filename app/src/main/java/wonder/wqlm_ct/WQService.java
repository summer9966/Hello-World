package wonder.wqlm_ct;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * Created by feeling on 2018/1/24.
 */

public class WQService extends Service{
    private final static String TAG = "WQService";

    @Override
    public void onCreate() {
        WonderLog.i(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WonderLog.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        WonderLog.i(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        WonderLog.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
