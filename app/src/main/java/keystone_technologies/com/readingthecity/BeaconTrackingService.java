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
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class BeaconTrackingService extends Service {

    private static BeaconManager beaconManager;
    private List<BeaconDevice> beaconList;
    private BeaconDataSource dataSource;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        beaconList = new ArrayList<BeaconDevice>();
        dataSource = new BeaconDataSource(this);

        beaconManager = new BeaconManager(this);

        startForeground(Constants.SERVICE_NOTIFICATION_ID, new Notification.Builder(this)
                .setSmallIcon(R.drawable.beacon_gray)
                .setContentText("Searching For Beacons")
                .setContentTitle(getString(R.string.app_name))
                .build());
    }

    public static void postNotification(BeaconDevice beacon, Context c) {

        NotificationManager notificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);

        /** set a custom layout to the notification in notification drawer  */
        RemoteViews notificationView = new RemoteViews(c.getPackageName(), R.layout.beacon_notification_layout);

        notificationView.setTextViewText(R.id.location, beacon.getName());

        Intent yesIntent = new Intent(c, NotificationButtonListener.class);
        yesIntent.setAction("Yes");
        yesIntent.putExtra("id", beacon.getId());

        PendingIntent pendingYesIntent = PendingIntent.getBroadcast(c, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.btnYes, pendingYesIntent);

        Intent noIntent = new Intent(c, NotificationButtonListener.class);
        noIntent.setAction("No");
        noIntent.putExtra("id", beacon.getId());

        PendingIntent pendingNoIntent = PendingIntent.getBroadcast(c, 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.btnNo, pendingNoIntent);

        Notification notificationBeacon = new Notification.Builder(c)
                .setSmallIcon(R.drawable.beacon_gray)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .build();

        notificationBeacon.contentView = notificationView;
        notificationManager.notify(Constants.BEACON_NOTIFICATION_ID, notificationBeacon);

    }

    public static void stopTrackingListener() {
        try {
           // beaconManager.disconnect();
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

//    @Override
//    public void onDestroy() {
//        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
//        try {
//            beaconManager.disconnect();
//            beaconManager.stopRanging(Constants.ALL_ESTIMOTE_BEACONS_REGION);
//        } catch (RemoteException e) {
//
//        }
//        super.onDestroy();
//    }

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
                                beaconList = dataSource.getAllBeacons();
                                if (beaconList.isEmpty()) {
                                    dataSource.createBeacon(beacons.get(0).getMajor(),
                                            beacons.get(0).getMinor(), new Date().toString());
                                        new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                } else {
                                    if (compareBeaconToList(beacons.get(0))) {
                                        dataSource.createBeacon(beacons.get(0).getMajor(), beacons.get(0).getMinor(),
                                                new Date().toString());
                                            new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                    } else {
                                        // beacon less than 24 hours old so use it from db
                                    }
                                }
                            }
                        }
                    });
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    private BeaconDevice parseJSON(String result) {

        BeaconDevice beaconDevice = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("rows");
            JSONObject row = jsonArray.getJSONObject(0);

            beaconDevice = new BeaconDevice(row.getInt("major"), row.getInt("minor"),
                    row.getString("name"), row.getString("parent"),
                    row.getString("id"), row.getString("url"),
                    row.getString("description"));

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return beaconDevice;
    }


    private BeaconDevice getBeaconFromList(Beacon beacon) {
        for (BeaconDevice bd : beaconList) {
            if (beacon.getMajor() == bd.getMajor()) {
                if (beacon.getMinor() == bd.getMinor()) {
                    return bd;
                }
            }
        }
        return null;
    }

    private boolean compareBeaconToList(Beacon beacon) {

        boolean retVal = true;
        for (BeaconDevice bd : beaconList) {
            if (bd.getMajor() == beacon.getMajor()) {
                if (bd.getMinor() == beacon.getMinor()) {
                    if (isSameDay(bd.getDate(), new Date())) {
                        retVal = false;
                        break;
                    } else {
                        beaconList.remove(bd);
                        dataSource.deleteBeacon(bd);
                        break;
                    }
                }
            }
        }
        return retVal;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        long julianDayNumber1 = date1.getTime() / Constants.MILLIS_PER_DAY;
        long julianDayNumber2 = date2.getTime() / Constants.MILLIS_PER_DAY;

        // If they now are equal then it is the same day.
        return julianDayNumber1 == julianDayNumber2;
    }
}