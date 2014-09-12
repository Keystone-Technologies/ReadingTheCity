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

    public static void startService(Context c) {
        Intent pushIntent = new Intent(c, com.estimote.sdk.service.BeaconService.class);
        c.startService(pushIntent);
    }

    public static void postNotification(String msg, Context c) {
        Notification.Builder builder = new Notification.Builder(c);
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (msg.equals("bluetooth")) {
            if (isBlueToothEnabled()) {
                //startService(c);
                builder.setSmallIcon(R.drawable.beacon_gray);
                builder.setContentText("Searching For Beacons");
            } else {
                //stopService(c);
               // BeaconTrackingService.stopTrackingListener();
                builder.setSmallIcon(R.drawable.bg_distance);
                builder.setContentText("Bluetooth not enabled!");
            }
            builder.setContentTitle(c.getString(R.string.app_name));
        } else {
            builder.setSmallIcon(R.drawable.beacon_gray);
            builder.setContentTitle(c.getString(R.string.app_name));
            builder.setContentText(msg);
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
            //builder.setDefaults(Notification.DEFAULT_LIGHTS);
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
        //notification.defaults |= Notification.DEFAULT_SOUND;
       // notification.defaults |= Notification.DEFAULT_LIGHTS;
       // notificationManager.notify(Constants.NOTIFICATION_ID, builder);
        }

        //NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());




//        Intent notifyIntent = new Intent(c, MainActivity.class);
//        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivities(
//                c,
//                0,
//                new Intent[]{notifyIntent},
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        Notification notification = new Notification.Builder(MainActivity.this)
//                .setSmallIcon(R.drawable.beacon_gray)
//                .setContentTitle(msg)
//                .setContentText(msg)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//                .build();
//        notification.defaults |= Notification.DEFAULT_SOUND;
//        notification.defaults |= Notification.DEFAULT_LIGHTS;
//        notificationManager.notify(Constants.NOTIFICATION_ID, notification);

        //TextView statusTextView = (TextView) findViewById(R.id.status);
        //statusTextView.setText(msg);
    }
}
