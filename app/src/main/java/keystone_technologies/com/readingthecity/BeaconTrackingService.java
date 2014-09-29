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

public class BeaconTrackingService extends Service {

    private static BeaconManager beaconManager;
    private List<BeaconDevice> beaconList;
    private BeaconDataSource dataSource;
    //private static Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        //context = this;
        beaconList = new ArrayList<BeaconDevice>();
        dataSource = new BeaconDataSource(this);

        beaconManager = new BeaconManager(this);

        Notification notification = new Notification.Builder(this)
        .setSmallIcon(R.drawable.beacon_gray)
        .setContentText("Searching For Beacons")
        .setContentTitle(getString(R.string.app_name))
        .build();

        startForeground(1, notification);
    }

//    public void beaconNotify(BeaconDevice b) {
//        Log.d("Beacon found. Name:", b.getName());
//
//
//
//        Toast.makeText(getApplicationContext(), "Beacon found with Name: " +
//                b.getName(), Toast.LENGTH_SHORT).show();
//        postNotification(b, this.getApplicationContext());
//    }

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

                            if (!beacons.isEmpty()) {
                                for (Beacon b : beacons) {
                                    beaconList = dataSource.getAllBeacons();

                                    if (beaconList.isEmpty()) {
                                        dataSource.createBeacon(b.getMajor(), b.getMinor(), new Date().toString(),
                                                Constants.NO);
                                        new GetBeaconInfo(b, getApplicationContext()).execute();
                                    } else {
                                        if (compareBeaconToList(b)) {
                                            dataSource.createBeacon(b.getMajor(), b.getMinor(), new Date().toString(),
                                                    Constants.NO);
                                            new GetBeaconInfo(b, getApplicationContext()).execute();
                                        } else {
                                          //  BeaconDevice bd = getStoredBeacon(b);
//                                            ArrayList<BeaconDevice> deviceList = new ArrayList<BeaconDevice>();
//                                            deviceList.add(bd);
//                                            while (getStoredBeacon(getBeaconFromList(b).getId()).hasParent()) {
//
//                                            }



                                           // ArrayList<File> fileList = getBeaconDetsilsList(beaconDevice.getId());

                                            // use current data by getting serialized file stored.
                                        }
                                    }


                                   // beaconList = getBeaconListDeserialized();
//                                    BeaconDevice bd = new BeaconDevice(String.valueOf(b.getMajor()),
//                                            String.valueOf(b.getMinor()), date);
//                                    if (beaconList.isEmpty()) {
//                                        beaconList.add(bd);
//                                        serializeBeaconList();
//                                        new GetBeaconInfo(b, getApplicationContext()).execute();
//                                    } else {
//                                        if (compareBeaconToList(bd)) {
//                                            beaconList.add(bd);
//                                            serializeBeaconList();
//                                            new GetBeaconInfo(b, getApplicationContext()).execute();
//                                        }
//                                    }
                                }
                            }

                                //beaconNotify(beacons.get(0));

                        }
                    });
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    private BeaconDevice getStoredBeacon(Beacon beacon) {
        BeaconDevice beaconStored = new BeaconDevice();
        try {
            FileInputStream fileIn = openFileInput(String.valueOf(beacon.getMajor() + beacon.getMinor()));
            BufferedInputStream buffer = new BufferedInputStream(fileIn);
            ObjectInputStream in = new ObjectInputStream(buffer);

            String jsonContents = in.readObject().toString();
            beaconStored = parseJSON(jsonContents);

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return beaconStored;
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

//    private ArrayList<BeaconDevice> getBeaconListDeserialized() {
//            ArrayList<BeaconDevice> beaconListTemp = new ArrayList<BeaconDevice>();
//            try {
//               // File file = new File(context.getFilesDir().getAbsolutePath() + Constants.FILEPATH);
//                //if (file.exists()) {
//                    FileInputStream fileIn = context.openFileInput("beaconStorage");
//                    BufferedInputStream buffer = new BufferedInputStream(fileIn);
//                    ObjectInputStream in = new ObjectInputStream(buffer);
//
//                    beaconListTemp = (ArrayList<BeaconDevice>)in.readObject();
//
//                    for (BeaconDevice b : beaconListTemp) {
//                        System.out.println("Deserialized data: \n" + b.getName());
//                    }
//
//                    in.close();
//                    fileIn.close();
//               // }
//
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            } catch (ClassNotFoundException ex) {
//                ex.printStackTrace();
//            }
//            return beaconListTemp;
//    }

//    public static void serializeBeaconList() {
//        try {
//            FileOutputStream fileOut = context.openFileOutput("beaconStorage", MODE_PRIVATE);
//            ObjectOutputStream out = new ObjectOutputStream(fileOut);
//            out.writeObject(beaconList);
//            out.close();
//            fileOut.close();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }

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