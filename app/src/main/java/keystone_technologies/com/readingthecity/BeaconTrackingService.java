package keystone_technologies.com.readingthecity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
        postNotification(b, getApplicationContext());
    }

    public void postNotification(Beacon beacon, Context c) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        /** set a custom layout to the notification in notification drawer  */
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.beacon_notification_layout);

        Intent yesIntent = new Intent(c, NotificationButtonListener.class);
        yesIntent.setAction("Yes");
        yesIntent.putExtra("uuid", beacon.getProximityUUID());
        PendingIntent pendingYesIntent = PendingIntent.getBroadcast(c, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.btnYes, pendingYesIntent);

        Intent noIntent = new Intent(c, NotificationButtonListener.class);
        noIntent.setAction("No");
        noIntent.putExtra("uuid", beacon.getProximityUUID());
        PendingIntent pendingNoIntent = PendingIntent.getBroadcast(c, 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.btnNo, pendingNoIntent);

        Notification notificationBeacon = new Notification.Builder(c)
                .setSmallIcon(R.drawable.beacon_gray)
                .setContentTitle(c.getString(R.string.app_name))
                .setContentText("Are you interested in?")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .build();

        notificationBeacon.contentView = notificationView;
        notificationManager.notify(0, notificationBeacon);

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
                                    dataSource.createBeacon(beacons.get(0).getProximityUUID(), Constants.NO);
                                    beaconNotify(beacons.get(0));
                                } else {
                                    for (BeaconDevice b : beaconList) {
                                        if (!b.getUUID().equals(beacons.get(0).getProximityUUID())) {
                                            dataSource.createBeacon(beacons.get(0).getProximityUUID(), Constants.NO);
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
