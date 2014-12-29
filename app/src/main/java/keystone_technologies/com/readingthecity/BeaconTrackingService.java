package keystone_technologies.com.readingthecity;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import java.util.Date;
import java.util.List;

public class BeaconTrackingService extends Service {

    private static BeaconManager beaconManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        beaconManager = new BeaconManager(this);

        startForeground(Constants.SERVICE_NOTIFICATION_ID, new Notification.Builder(this)
                .setSmallIcon(R.drawable.beacon_gray)
                .setContentText("Searching For Beacons")
                .setContentTitle(getString(R.string.app_name))
                .build());
    }

    public static void stopTrackingListener() {
        try {
            beaconManager.stopRanging(Constants.ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {

        }
    }

    public static void startTrackingListener() {
        try {
            beaconManager.startRanging(Constants.ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {

        }
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
                        BeaconsDataSource beaconsDataSource = new BeaconsDataSource(getApplicationContext());
                        if (!beacons.isEmpty()) {
                            int notificationId = NotificationOutput.getNotificationId();
                            if (notificationId != 99) {
                                BeaconDevice beacon = beaconsDataSource.getBeacon(notificationId);
                                if (beacons.size() > 1) {
                                    if (!match(beacons.get(0), beacon)) {
                                        if (beacons.get(0).getRssi() - beacons.get(1).getRssi() >= 5) {
                                            if (beaconsDataSource.isBeaconInDB(notificationId)) {
                                                if (beaconsDataSource.isBeaconAgeExpired(notificationId)) {
                                                    if (beaconsDataSource.notCurrentlyFetchingBeacon(notificationId)) {
                                                        beaconsDataSource.deleteBeacon(notificationId);
                                                        beaconsDataSource.createBeacon(beacons.get(0).getMajor(),
                                                                beacons.get(0).getMinor(), notificationId, new Date());
                                                        new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                                    }
                                                } else {
                                                    // get beacon from DB and pass on
                                                    new GetBeaconDetails(beaconsDataSource.getBeaconFromDB(notificationId).getParent(),
                                                            getApplicationContext()).execute();
                                                }
                                            } else {
                                                beaconsDataSource.createBeacon(beacons.get(0).getMajor(),
                                                        beacons.get(0).getMinor(), notificationId, new Date());
                                                new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                            }
                                        }
                                    }
                                } else {
                                    if (!match(beacons.get(0), beacon)) {
                                        if (beaconsDataSource.isBeaconInDB(notificationId)) {
                                            if (beaconsDataSource.isBeaconAgeExpired(notificationId)) {
                                                if (beaconsDataSource.notCurrentlyFetchingBeacon(notificationId)) {
                                                    beaconsDataSource.deleteBeacon(notificationId);
                                                    beaconsDataSource.createBeacon(beacons.get(0).getMajor(),
                                                            beacons.get(0).getMinor(), notificationId, new Date());
                                                    new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                                }
                                            } else {
                                                new GetBeaconDetails(beaconsDataSource.getBeaconFromDB(notificationId).getParent(),
                                                        getApplicationContext()).execute();
                                            }
                                        } else {
                                            beaconsDataSource.createBeacon(beacons.get(0).getMajor(),
                                                    beacons.get(0).getMinor(), notificationId, new Date());
                                            new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                        }
                                    }
                                }
                            } else {
                                notificationId = Integer
                                        .parseInt(String.valueOf(beacons.get(0).getMinor()));
                                if (beacons.size() > 1) {
                                    if (beacons.get(0).getRssi() - beacons.get(1).getRssi() >= 5) {
                                        if (beaconsDataSource.isBeaconInDB(notificationId)) {
                                            if (beaconsDataSource.isBeaconAgeExpired(notificationId)) {
                                                if (beaconsDataSource.notCurrentlyFetchingBeacon(notificationId)) {
                                                    beaconsDataSource.deleteBeacon(notificationId);
                                                    beaconsDataSource.createBeacon(beacons.get(0).getMajor(),
                                                            beacons.get(0).getMinor(), notificationId, new Date());
                                                    new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                                }
                                            } else {
                                                new GetBeaconDetails(beaconsDataSource.getBeaconFromDB(notificationId).getParent(),
                                                        getApplicationContext()).execute();
                                            }
                                        } else {
                                            beaconsDataSource.createBeacon(beacons.get(0).getMajor(),
                                                    beacons.get(0).getMinor(), notificationId, new Date());
                                            new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                        }
                                    }
                                } else {
                                    if (beaconsDataSource.isBeaconInDB(notificationId)) {
                                        if (beaconsDataSource.isBeaconAgeExpired(notificationId)) {
                                            if (beaconsDataSource.notCurrentlyFetchingBeacon(notificationId)) {
                                                beaconsDataSource.deleteBeacon(notificationId);
                                                beaconsDataSource.createBeacon(beacons.get(0).getMajor(),
                                                        beacons.get(0).getMinor(), notificationId, new Date());
                                                new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                            }
                                        } else {
                                            new GetBeaconDetails(beaconsDataSource.getBeaconFromDB(notificationId).getParent(),
                                                    getApplicationContext()).execute();
                                        }
                                    } else {
                                        beaconsDataSource.createBeacon(beacons.get(0).getMajor(),
                                                beacons.get(0).getMinor(), notificationId, new Date());
                                        new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
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

    private boolean match(Beacon b, BeaconDevice bd) {
        if (b.getMajor() == bd.getMajor()) {
            if (b.getMinor() == bd.getMinor()) {
                return true;
            }
        }
        return false;
    }
}