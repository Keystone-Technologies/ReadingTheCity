package keystone_technologies.com.readingthecity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

public class BootCompletedIntentReceiver extends BroadcastReceiver {

   // private BeaconManager beaconManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if (Utilities.isBlueToothEnabled()) {
                Utilities.startService(context);
                Utilities.startTrackingService(context);
               // startRangingListener(context);
                //Intent listenerIntent = new Intent(context, InvisibleActivity.class);
                //listenerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //context.startActivity(listenerIntent);
            }
        }
    }

//    private void startRangingListener(final Context context) {
//        // Configure BeaconManager.
//        beaconManager = new BeaconManager(context);
//        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
//            @Override
//            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
//                // Note that results are not delivered on UI thread.
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Note that beacons reported here are already sorted by estimated
//                        // distance between device and beacon.
//                        //getActionBar().setSubtitle("Found beacons: " + beacons.size());
//                        Utilities.postNotification("Found beacon!", context);
//                    }
//                });
//            }
//        });
//    }
}
