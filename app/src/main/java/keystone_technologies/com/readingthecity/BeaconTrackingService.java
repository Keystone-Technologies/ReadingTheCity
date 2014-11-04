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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BeaconTrackingService extends Service {

    private static BeaconManager beaconManager;
    private List<Details> beaconList;
    private BeaconDataSource beaconDataSource;
    private DetailsDataSource detailsDataSource;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        beaconList = new ArrayList<Details>();
        beaconDataSource = new BeaconDataSource(this);
        detailsDataSource = new DetailsDataSource(this);

        beaconManager = new BeaconManager(this);

        startForeground(Constants.SERVICE_NOTIFICATION_ID, new Notification.Builder(this)
                .setSmallIcon(R.drawable.beacon_gray)
                .setContentText("Searching For Beacons")
                .setContentTitle(getString(R.string.app_name))
                .build());
    }

    public static void postNotification(Details detail, Context c) {

        NotificationManager notificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);

        /** set a custom layout to the notification in notification drawer  */
        RemoteViews notificationView = new RemoteViews(c.getPackageName(), R.layout.beacon_notification_layout);

        try {
            JSONObject jsonObject = new JSONObject(detail.getDetail());
            JSONObject value = jsonObject.getJSONObject("value");
            notificationView.setTextViewText(R.id.detailName, value.getString("name"));
            notificationView.setTextViewText(R.id.detailDescription, value.getString("description"));
            Intent infoIntent = new Intent(c, BeaconInfoActivity.class);
            infoIntent.putExtra("url", value.getString("url"));
            PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, infoIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            Intent yesIntent = new Intent(c, NotificationButtonListener.class);
            yesIntent.setAction("Yes");
            yesIntent.putExtra("id", value.getString("_id"));

            PendingIntent pendingYesIntent = PendingIntent.getBroadcast(c, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationView.setOnClickPendingIntent(R.id.btnYes, pendingYesIntent);

            Intent noIntent = new Intent(c, NotificationButtonListener.class);
            noIntent.setAction("No");
            noIntent.putExtra("id", value.getString("_id"));

            PendingIntent pendingNoIntent = PendingIntent.getBroadcast(c, 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationView.setOnClickPendingIntent(R.id.btnNo, pendingNoIntent);

            Notification notificationBeacon = new Notification.Builder(c)
                    .setSmallIcon(R.drawable.beacon_gray)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                    .setContentIntent(pendingIntent)
                    .build();

            notificationBeacon.contentView = notificationView;
            notificationManager.notify(Constants.BEACON_NOTIFICATION_ID, notificationBeacon);
           // }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

                            if (!beacons.isEmpty()) {
                                if (beacons.size() > 1) {
                                    if (beacons.get(0).getRssi() - beacons.get(1).getRssi() <= -5) {
                                        if (!beaconDataSource.isBeaconInDB(beacons.get(0))) {
                                            new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                        } else {
                                            if (isBeaconOld(beacons.get(0))) {
                                                beaconDataSource.deleteBeacon(beacons.get(0));
                                                new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                            } else {
                                                // get beacon from DB
                                                String details = detailsDataSource.getDetailsFromDevice(
                                                        beaconDataSource.getBeaconFromDB(beacons.get(0)));
                                           //     Log.d("details are:", details);
                                            }
                                        }
                                    }
                                } else {
                                    if (!beaconDataSource.isBeaconInDB(beacons.get(0))) {
                                        new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                    } else {
                                        if (isBeaconOld(beacons.get(0))) {
                                            beaconDataSource.deleteBeacon(beacons.get(0));
                                            new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                        } else {
                                            // get beacon from DB
                                            String details = detailsDataSource.getDetailsFromDevice(
                                                    beaconDataSource.getBeaconFromDB(beacons.get(0)));
                                          //  Log.d("details are:", details);
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

    private boolean isBeaconOld(Beacon beacon) {

        List<Device> beaconList = beaconDataSource.getAllBeacons();
        boolean flag = false;

        for (Device b : beaconList) {
            if (b.getMajor() == beacon.getMajor()) {
                if (b.getMinor() == beacon.getMinor()) {
                    if (isSameDay(b.getDate(), new Date())) {
                        flag = false;
                        break;
                    } else {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        long julianDayNumber1 = date1.getTime() / Constants.MILLIS_PER_DAY;
        long julianDayNumber2 = date2.getTime() / Constants.MILLIS_PER_DAY;

        // If they now are equal then it is the same day.
        return julianDayNumber1 == julianDayNumber2;
    }
}