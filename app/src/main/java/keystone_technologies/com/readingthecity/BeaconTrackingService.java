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
    private BeaconDataSource beaconDataSource;
    private static DetailsDataSource detailsDataSource;
    private static NotificationsDataSource notificationsDataSource;
    private Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        context = this;
        beaconDataSource = new BeaconDataSource(this);
        detailsDataSource = new DetailsDataSource(this);
        notificationsDataSource = new NotificationsDataSource(this);

        beaconManager = new BeaconManager(this);

        startForeground(Constants.SERVICE_NOTIFICATION_ID, new Notification.Builder(this)
                .setSmallIcon(R.drawable.beacon_gray)
                .setContentText("Searching For Beacons")
                .setContentTitle(getString(R.string.app_name))
                .build());
    }

    public static void postNotification(Details detail, Context c) {
        Intent infoIntent = new Intent(c, BeaconInfoActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, infoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            JSONObject jsonObject = new JSONObject(detail.getDetail());
            if (jsonObject.isNull("value")) {
                if (!notificationsDataSource.isPostInDB(jsonObject.getString("_id"))) {
                    NotificationManager notificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
                    /** set a custom layout to the notification in notification drawer  */
                    RemoteViews notificationView = new RemoteViews(c.getPackageName(), R.layout.beacon_notification_layout);
                    notificationView.setTextViewText(R.id.detailName, jsonObject.getString("name"));
                    notificationView.setTextViewText(R.id.detailDescription, jsonObject.getString("description"));

                    infoIntent.putExtra("url", jsonObject.getString("url"));
                    infoIntent.putExtra("name", jsonObject.getString("name"));

                    Intent yesIntent = new Intent(c, NotificationButtonListener.class);
                    yesIntent.setAction("Yes");
                    yesIntent.putExtra("id", jsonObject.getString("_id"));
                    yesIntent.putExtra("name", jsonObject.getString("name"));

                    PendingIntent pendingYesIntent = PendingIntent.getBroadcast(c, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationView.setOnClickPendingIntent(R.id.btnYes, pendingYesIntent);

                    Intent noIntent = new Intent(c, NotificationButtonListener.class);
                    noIntent.setAction("No");
                    noIntent.putExtra("id", jsonObject.getString("_id"));
                    noIntent.putExtra("name", jsonObject.getString("name"));

                    PendingIntent pendingNoIntent = PendingIntent.getBroadcast(c, 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationView.setOnClickPendingIntent(R.id.btnNo, pendingNoIntent);

                    Notification notificationBeacon = new Notification.Builder(c)
                            .setSmallIcon(R.drawable.beacon_gray)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                            .setContentIntent(pendingIntent)
                            .build();

                    notificationBeacon.contentView = notificationView;
                    notificationManager.notify(Constants.BEACON_NOTIFICATION_ID, notificationBeacon);
                } else {
                    try {
                        if (notificationsDataSource.getNotificationResponse(jsonObject.getString("_id")) != 0) {
                            Details childDetail = detailsDataSource.getChildDetailFromId(jsonObject.getString("_id"));
                            if (childDetail != null) {
                                BeaconTrackingService.postNotification(childDetail, c);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                JSONObject value = jsonObject.getJSONObject("value");
                if (!notificationsDataSource.isPostInDB(value.getString("_id"))) {
                    NotificationManager notificationManager = (NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
                    /** set a custom layout to the notification in notification drawer  */
                    RemoteViews notificationView = new RemoteViews(c.getPackageName(), R.layout.beacon_notification_layout);

                    notificationView.setTextViewText(R.id.detailName, value.getString("name"));
                    notificationView.setTextViewText(R.id.detailDescription, value.getString("description"));
                    // Intent infoIntent = new Intent(c, BeaconInfoActivity.class);
                    infoIntent.putExtra("url", value.getString("url"));
                    infoIntent.putExtra("name", value.getString("name"));
                    //  PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, infoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Intent yesIntent = new Intent(c, NotificationButtonListener.class);
                    yesIntent.setAction("Yes");
                    yesIntent.putExtra("id", value.getString("_id"));
                    yesIntent.putExtra("name", value.getString("name"));

                    PendingIntent pendingYesIntent = PendingIntent.getBroadcast(c, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationView.setOnClickPendingIntent(R.id.btnYes, pendingYesIntent);

                    Intent noIntent = new Intent(c, NotificationButtonListener.class);
                    noIntent.setAction("No");
                    noIntent.putExtra("id", value.getString("_id"));
                    noIntent.putExtra("name", value.getString("name"));

                    Notification notificationBeacon = new Notification.Builder(c)
                            .setSmallIcon(R.drawable.beacon_gray)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                            .setContentIntent(pendingIntent)
                            .build();

                    notificationBeacon.contentView = notificationView;
                    notificationManager.notify(Constants.BEACON_NOTIFICATION_ID, notificationBeacon);
                } else {
                    try {
                        if (notificationsDataSource.getNotificationResponse(value.getString("_id")) != 0) {
                            Details childDetail = detailsDataSource.getChildDetailFromId(value.getString("_id"));
                            if (childDetail != null) {
                                BeaconTrackingService.postNotification(childDetail, c);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
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