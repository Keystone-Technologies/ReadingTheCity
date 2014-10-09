package keystone_technologies.com.readingthecity;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothStateReceiver extends BroadcastReceiver {

    private static Intent pushIntent;
    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction())){
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    stopEstimoteService(context);
                    BeaconTrackingService.stopTrackingListener();

                    mNotificationManager = (NotificationManager)
                            context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(Constants.SERVICE_NOTIFICATION_ID, new Notification.Builder(context)
                            .setSmallIcon(R.drawable.bg_distance)
                            .setContentText("Bluetooth Off")
                            .setContentTitle(context.getString(R.string.app_name))
                            .build());


                   // BeaconTrackingService service = new BeaconTrackingService();
                    //service.getService().stopSelf();
                   // stopTrackingService(context);
                    break;
                case BluetoothAdapter.STATE_ON:
                    startEstimoteService(context);
                    BeaconTrackingService.startTrackingListener();

                    mNotificationManager = (NotificationManager)
                            context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(Constants.SERVICE_NOTIFICATION_ID, new Notification.Builder(context)
                            .setSmallIcon(R.drawable.beacon_gray)
                            .setContentText("Searching for Beacons")
                            .setContentTitle(context.getString(R.string.app_name))
                            .build());

                    //startTrackingService(context);
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