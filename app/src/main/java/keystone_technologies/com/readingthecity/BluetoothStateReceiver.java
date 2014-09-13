package keystone_technologies.com.readingthecity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothStateReceiver extends BroadcastReceiver {

    private static Intent pushIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction())){
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    stopEstimoteService(context);
                    BeaconTrackingService.stopTrackingListener();
                    stopTrackingService(context);
                 //   Utilities.postNotification("bluetooth", context);
                    break;
                case BluetoothAdapter.STATE_ON:
                    startEstimoteService(context);
                    startTrackingService(context);
                   // Utilities.postNotification("bluetooth", context);
                    break;
            }
        }
    }

    public static void startEstimoteService(Context c) {
        pushIntent = new Intent(c, com.estimote.sdk.service.BeaconService.class);
        c.startService(pushIntent);
    }

    public static void stopEstimoteService(Context c) {
        pushIntent = new Intent(c, com.estimote.sdk.service.BeaconService.class);
        c.stopService(pushIntent);
    }

    public static void startTrackingService(Context c) {
        pushIntent = new Intent(c, BeaconTrackingService.class);
        c.startService(pushIntent);
    }

    public static void stopTrackingService(Context c) {
        pushIntent = new Intent(c, BeaconTrackingService.class);
        c.stopService(pushIntent);
    }
}