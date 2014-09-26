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

public class BeaconTrackingService extends Service {

    private static BeaconManager beaconManager;
    private static List<BeaconDevice> beaconList;
    private static Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        context = this;
        beaconList = new ArrayList<BeaconDevice>();

        beaconManager = new BeaconManager(this);

        Notification notification = new Notification.Builder(this)
        .setSmallIcon(R.drawable.beacon_gray)
        .setContentText("Searching For Beacons")
        .setContentTitle(getString(R.string.app_name))
        .build();

        startForeground(1, notification);
    }

    public static void addToBeaconList(BeaconDevice beacon) {
        beaconList.add(beacon);
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
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .build();

        notificationBeacon.contentView = notificationView;
        notificationManager.notify(Constants.NOTIFICATION_ID, notificationBeacon);

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
                                Date date = new Date();
                                for (Beacon b : beacons) {
                                    beaconList = getBeaconListDeserialized();
                                    BeaconDevice bd = new BeaconDevice(String.valueOf(b.getMajor()),
                                            String.valueOf(b.getMinor()), date);
                                    if (beaconList.isEmpty()) {
                                        beaconList.add(bd);
                                        serializeBeaconList();
                                    } else {
                                        if (compareBeaconToList(bd)) {
                                            beaconList.add(bd);
                                            serializeBeaconList();
                                        }
                                    }

                                       // new GetBeaconInfo(b, getApplicationContext()).execute();

                                }
                            }

                                //beaconNotify(beacons.get(0));

                        }
                    });
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    private ArrayList<BeaconDevice> getBeaconListDeserialized() {
            ArrayList<BeaconDevice> beaconListTemp = new ArrayList<BeaconDevice>();
            try {
               // File file = new File(context.getFilesDir().getAbsolutePath() + Constants.FILEPATH);
                //if (file.exists()) {
                    FileInputStream fileIn = context.openFileInput("beaconStorage");
                    BufferedInputStream buffer = new BufferedInputStream(fileIn);
                    ObjectInputStream in = new ObjectInputStream(buffer);

                    beaconListTemp = (ArrayList<BeaconDevice>)in.readObject();

                    for (BeaconDevice b : beaconListTemp) {
                        System.out.println("Deserialized data: \n" + b.getName());
                    }

                    in.close();
                    fileIn.close();
               // }

            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            return beaconListTemp;
    }

    public static void serializeBeaconList() {
        try {
            FileOutputStream fileOut = context.openFileOutput("beaconStorage", MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(beaconList);
            out.close();
            fileOut.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean compareBeaconToList(BeaconDevice beacon) {

        boolean retVal = false;
        for (BeaconDevice bd : beaconList) {
            if (bd.getMajor().equals(beacon.getMajor())) {
                if (bd.getMinor().equals(beacon.getMinor())) {
                    if (!isSameDay(bd.getDate(), beacon.getDate())) {
                        beaconList.remove(bd);
                        retVal = true;
                        break;
                    } else {
                        retVal = false;
                        break;
                    }
                } else {
                    retVal = true;
                }
            } else {
                retVal = true;
            }
        }
        return retVal;
    }

    private static boolean isSameDay(Date date1, Date date2) {
        long julianDayNumber1 = date1.getTime() / Constants.MILLIS_PER_DAY;
        long julianDayNumber2 = date2.getTime() / Constants.MILLIS_PER_DAY;

        // If they now are equal then it is the same day.
        return julianDayNumber1 == julianDayNumber2;
    }
}