package keystone_technologies.com.readingthecity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

public class Utilities {

    public static Boolean isBlueToothEnabled() {
        BluetoothAdapter blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueToothAdapter == null || !blueToothAdapter.isEnabled()) {
            // Device doesn't support bluetooth or not enabled
            return false;
        }
        return true;
    }
}
