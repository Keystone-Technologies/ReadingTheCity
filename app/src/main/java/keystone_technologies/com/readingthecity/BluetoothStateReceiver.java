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
                    Utilities.stopService(context);
                    stopTrackingService(context);
                    Utilities.postNotification("bluetooth", context);
                    break;
                case BluetoothAdapter.STATE_ON:
                    Utilities.startService(context);
                    startTrackingService(context);
                    Utilities.postNotification("bluetooth", context);
                    break;
            }
        }
    }

    public void startTrackingService(Context c) {
        pushIntent = new Intent(c, BeaconTrackingService.class);
        c.startService(pushIntent);
    }

    public void stopTrackingService(Context c) {
        pushIntent = new Intent(c, BeaconTrackingService.class);
        c.stopService(pushIntent);
    }
}