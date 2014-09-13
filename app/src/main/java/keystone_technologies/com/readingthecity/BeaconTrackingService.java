package keystone_technologies.com.readingthecity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.List;

public class BeaconTrackingService extends Service {

    private static BeaconManager beaconManager;
    private BeaconDataSource dataSource;
    List<BeaconDevice> beaconList;
    private Notification notification;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        dataSource = new BeaconDataSource(this);
        beaconList = new ArrayList<BeaconDevice>();

        beaconManager = new BeaconManager(this);

        notification = new Notification.Builder(this)
        .setSmallIcon(R.drawable.beacon_gray)
        .setContentText("Searching For Beacons")
        .setContentTitle(getString(R.string.app_name))
        .build();

        startForeground(1, notification);
    }

    public void beaconNotify(Beacon b) {
        Log.d("Beacon found with UUID:", b.getProximityUUID());
        Toast.makeText(getApplicationContext(), "Beacon found with UUID: " +
                b.getProximityUUID(), Toast.LENGTH_SHORT).show();
        postNotification("Beacon Found", getApplicationContext());
    }

    public void postNotification(String msg, Context c) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //Yes intent
        Intent yesReceive = new Intent();
        yesReceive.setAction(Integer.toString(Constants.YES));
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);


        //No intent
        Intent noReceive = new Intent();
        noReceive.setAction(Integer.toString(Constants.NO));
        PendingIntent pendingIntentNo = PendingIntent.getBroadcast(this, 12345, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notificationBeacon = new Notification.Builder(c)
        .addAction(R.drawable.beacon_gray, "Yes", pendingIntentYes)
        .addAction(R.drawable.ic_launcher, "No", pendingIntentNo)
        .setSmallIcon(R.drawable.beacon_gray)
        .setContentTitle(c.getString(R.string.app_name))
        .setContentText(msg)
        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
        .build();

        /** set a custom layout to the notification in notification drawer  */
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.beacon_notification_layout);
        notification.contentView = notificationView;


        notificationManager.notify(0, notificationBeacon);







      //  if (//check to see if yes/no or should open webview activity) {
            //Intent intent = new Intent(this, BeaconInfoActivity.class);
            //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            //builder.setContentIntent(pendingIntent);
      //  } else {

     //   }




    }

    public static void stopTrackingListener() {
        try {
            beaconManager.disconnect();
            beaconManager.stopRanging(Constants.ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {

        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        try {
            beaconManager.disconnect();
            beaconManager.stopRanging(Constants.ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {

        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(Constants.ALL_ESTIMOTE_BEACONS_REGION);
                    Toast.makeText(getApplicationContext(), "try start ranging", Toast.LENGTH_LONG).show();
                } catch (RemoteException e) {
                    Toast.makeText(getApplicationContext(), "Cannot start ranging", Toast.LENGTH_LONG).show();
                }

                    beaconManager.setRangingListener(new BeaconManager.RangingListener() {

                        @Override
                        public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {

                            if (beacons.size() > 0) {
                                beaconList = dataSource.getAllBeacons();

                                if (beaconList.size() == 0) {
                                    dataSource.createBeacon(beacons.get(0).getProximityUUID(), 0);
                                    beaconNotify(beacons.get(0));
                                } else {
                                    for (BeaconDevice b : beaconList) {
                                        if (!b.getUUID().equals(beacons.get(0).getProximityUUID())) {
                                            dataSource.createBeacon(beacons.get(0).getProximityUUID(), 0);
                                            beaconNotify(beacons.get(0));
                                        }
                                    }
                                }
                            }
                        }
                    });
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }
}
