package keystone_technologies.com.readingthecity;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
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



        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.beacon_gray)
                .setContentText("Looking For Beacons")
                .setContentTitle("Tracking Service Started")
                .getNotification();


        startForeground(1, notification);
    }

    public void beaconNotify(Beacon b) {
        Log.d("Beacon found with UUID:", b.getProximityUUID());
        Toast.makeText(getApplicationContext(), "Beacon found with UUID: " +
                b.getProximityUUID(), Toast.LENGTH_SHORT).show();
        Utilities.postNotification("Beacon Found", getApplicationContext());
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
