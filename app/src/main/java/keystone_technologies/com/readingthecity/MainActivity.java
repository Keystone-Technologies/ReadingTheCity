package keystone_technologies.com.readingthecity;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isBlueToothEnabled()) {
            if (!isMyServiceRunning(com.estimote.sdk.service.BeaconService.class)) {
                BluetoothStateReceiver.startEstimoteService(this);
            }
            if (!isMyServiceRunning(BeaconTrackingService.class)) {
                BluetoothStateReceiver.startTrackingService(this);
            }
        }
    }

    public static Boolean isBlueToothEnabled() {
        BluetoothAdapter blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueToothAdapter == null || !blueToothAdapter.isEnabled()) {
            // Device doesn't support bluetooth or not enabled
            return false;
        }
        return true;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void settings(View v) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class );
        startActivity(settingsIntent);
    }
}
