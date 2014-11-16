package keystone_technologies.com.readingthecity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BeaconTrackingService extends Service {

    private static BeaconManager beaconManager;
    private BeaconDataSource beaconDataSource;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        beaconDataSource = new BeaconDataSource(this);
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
                        if (!beacons.isEmpty()) {
                            if (beacons.size() > 1) {
                                if (beacons.get(0).getRssi() - beacons.get(1).getRssi() >= 5) {
                                    if (beaconDataSource.isBeaconNotInDB(beacons.get(0))) {
                                         beaconDataSource.createBeacon(beacons.get(0).getMajor(), beacons.get(0).getMinor(),
                                              new Date().toString());
                                        new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                    } else {
                                        if (isBeaconOld(beacons.get(0))) {
                                            beaconDataSource.deleteBeacon(beacons.get(0));
                                            beaconDataSource.createBeacon(beacons.get(0).getMajor(), beacons.get(0).getMinor(),
                                                    new Date().toString());
                                            new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                        } else {
                                            // get beacon from DB
                                            try {
                                              //  String id = beaconDataSource.getIdFromDB(beacons.get(0));
                                              //  if (id != null) {
                                               //     JSONObject jsonObject = new JSONObject
                                               //             (detailsDataSource.getDetailsFromId(id));
                                               //     new GetBeaconDetails(jsonObject.get("parent").toString(),
                                                //            context).execute();
                                             //   } else {
                                              //      beaconDataSource.deleteBeacon(beacons.get(0));
                                              //  }
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (beaconDataSource.isBeaconNotInDB(beacons.get(0))) {
                                    beaconDataSource.createBeacon(beacons.get(0).getMajor(), beacons.get(0).getMinor(),
                                            new Date().toString());
                                    new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                } else {
                                    if (isBeaconOld(beacons.get(0))) {
                                        beaconDataSource.deleteBeacon(beacons.get(0));
                                        beaconDataSource.createBeacon(beacons.get(0).getMajor(), beacons.get(0).getMinor(),
                                                new Date().toString());
                                        new GetBeaconInfo(beacons.get(0), getApplicationContext()).execute();
                                    } else {
                                        // get beacon from DB
                                        try {
//                                            String id = beaconDataSource.getIdFromDB(beacons.get(0));
//                                            if (id != null) {
//                                                JSONObject jsonObject = new JSONObject
//                                                        (detailsDataSource.getDetailsFromId(id));
//                                                new GetBeaconDetails(jsonObject.get("parent").toString(),
//                                                        context).execute();
//                                            } else {
//                                                beaconDataSource.deleteBeacon(beacons.get(0));
//                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
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