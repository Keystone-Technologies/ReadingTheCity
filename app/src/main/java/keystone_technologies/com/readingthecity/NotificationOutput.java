package keystone_technologies.com.readingthecity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.RemoteViews;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.List;

public class NotificationOutput {

    private static int notificationId = 99;
    private BeaconsDataSource beaconsDataSource;

    public NotificationOutput() {
        super();
    }

    public static int getNotificationId() {
        return notificationId;
    }

    public void postNotification(Details detail, final Context context) {
        final DetailsDataSource detailsDataSource = new DetailsDataSource(context);
        final Intent infoIntent = new Intent(context, BeaconInfoActivity.class);
        beaconsDataSource = new BeaconsDataSource(context);

        try {
            final JSONObject jsonObject = detail.getDetail();
            final NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (!jsonObject.isNull("thumbnail")) {
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(final Void... params) {
                        Bitmap icon = null;
                        try {
                            String url = jsonObject.getString("thumbnail");
                            InputStream in = new java.net.URL(url).openStream();
                            icon = BitmapFactory.decodeStream(in);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return icon;
                    }

                    @Override
                    protected void onPostExecute(Bitmap result) {
                        RemoteViews notificationView = null;
                        try {
                            JSONArray urlArray = jsonObject.getJSONArray("url");
                            infoIntent.putExtra("url", urlArray.get(0).toString());

                            infoIntent.putExtra("name", jsonObject.getString("name"));
                            PendingIntent pendingIntent = PendingIntent.getActivity
                                    (context, 0, infoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            if (detailsDataSource.getChildDetailFromId(jsonObject.getString("_id")) == null) {
                                notificationView = new RemoteViews
                                        (context.getPackageName(), R.layout.nochild_notification_layout);
                                notificationView.setTextViewText(R.id.detailName, jsonObject.getString("name"));
                                notificationView.setTextViewText(R.id.detailDescription, jsonObject.getString("description"));

                                if (result != null) {
                                    notificationView.setImageViewBitmap(R.id.leftImage, result);
                                }

                                Notification notificationDetail = new Notification.Builder(context)

                                        .setSmallIcon(R.drawable.beacon_gray)
                                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                                        .setContentIntent(pendingIntent)
                                        .setPriority(Notification.PRIORITY_MAX)
                                        .build();

                                notificationDetail.bigContentView = notificationView;
                                notificationId = beaconsDataSource.getNotificationId(jsonObject.getString("_id"));
                                notificationManager.notify(notificationId, notificationDetail);
                            } else {
                                if (detailsDataSource.isDetailInDB(jsonObject.getString("_id"))) {
                                    Intent yesIntent = new Intent(context, NotificationButtonListener.class);
                                    yesIntent.setAction("Yes");
                                    yesIntent.putExtra("id", jsonObject.getString("_id"));
                                    yesIntent.putExtra("name", jsonObject.getString("name"));

                                    notificationView = new RemoteViews(context.getPackageName(), R.layout.beacon_notification_layout);
                                    notificationView.setTextViewText(R.id.detailName, jsonObject.getString("name"));
                                    notificationView.setTextViewText(R.id.detailDescription, jsonObject.getString("description"));

                                    if (result != null) {
                                        notificationView.setImageViewBitmap(R.id.leftImage, result);
                                    }

                                    PendingIntent pendingYesIntent =
                                            PendingIntent.getBroadcast(context, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    notificationView.setOnClickPendingIntent(R.id.btnYes, pendingYesIntent);


                                    Intent noIntent = new Intent(context, NotificationButtonListener.class);
                                    noIntent.setAction("No");
                                    noIntent.putExtra("id", jsonObject.getString("_id"));
                                    noIntent.putExtra("name", jsonObject.getString("name"));

                                    PendingIntent pendingNoIntent =
                                            PendingIntent.getBroadcast(context, 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    notificationView.setOnClickPendingIntent(R.id.btnNo, pendingNoIntent);

                                    Notification notificationDetail = new Notification.Builder(context)

                                            .setSmallIcon(R.drawable.beacon_gray)
                                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                                            .setContentIntent(pendingIntent)
                                            .setPriority(Notification.PRIORITY_MAX)
                                            .build();

                                    notificationDetail.bigContentView = notificationView;
                                    notificationManager.notify(Constants.PARENT_NOTIFICATION_ID, notificationDetail);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }.execute();
            } else {
                try {

                    JSONArray urlArray = jsonObject.getJSONArray("url");
                    infoIntent.putExtra("url", urlArray.get(0).toString());

                    infoIntent.putExtra("name", jsonObject.getString("name"));
                    PendingIntent pendingIntent =
                            PendingIntent.getActivity(context, 0, infoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    RemoteViews notificationView;

                    if (detailsDataSource.getChildDetailFromId(jsonObject.getString("_id")) == null) {
                        notificationView = new RemoteViews(context.getPackageName(), R.layout.nochild_notification_layout);
                        notificationView.setTextViewText(R.id.detailName, jsonObject.getString("name"));
                        notificationView.setTextViewText(R.id.detailDescription, jsonObject.getString("description"));

                        Notification notificationDetail = new Notification.Builder(context)

                                .setSmallIcon(R.drawable.beacon_gray)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                                .setContentIntent(pendingIntent)
                                .setPriority(Notification.PRIORITY_MAX)
                                .build();

                        notificationDetail.bigContentView = notificationView;
                        notificationId = beaconsDataSource.getNotificationId(jsonObject.getString("_id"));
                        notificationManager.notify(notificationId, notificationDetail);
                    } else {
                        if (detailsDataSource.isDetailInDB(jsonObject.getString("_id"))) {
                            notificationView = new RemoteViews(context.getPackageName(), R.layout.beacon_notification_layout);
                            notificationView.setTextViewText(R.id.detailName, jsonObject.getString("name"));
                            notificationView.setTextViewText(R.id.detailDescription, jsonObject.getString("description"));

                            Intent yesIntent = new Intent(context, NotificationButtonListener.class);
                            yesIntent.setAction("Yes");
                            yesIntent.putExtra("id", jsonObject.getString("_id"));
                            yesIntent.putExtra("name", jsonObject.getString("name"));

                            PendingIntent pendingYesIntent =
                                    PendingIntent.getBroadcast(context, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            notificationView.setOnClickPendingIntent(R.id.btnYes, pendingYesIntent);

                            Intent noIntent = new Intent(context, NotificationButtonListener.class);
                            noIntent.setAction("No");
                            noIntent.putExtra("id", jsonObject.getString("_id"));
                            noIntent.putExtra("name", jsonObject.getString("name"));

                            PendingIntent pendingNoIntent =
                                    PendingIntent.getBroadcast(context, 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            notificationView.setOnClickPendingIntent(R.id.btnNo, pendingNoIntent);

                            Notification notificationDetail = new Notification.Builder(context)

                                    .setSmallIcon(R.drawable.beacon_gray)
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                                    .setContentIntent(pendingIntent)
                                    .setPriority(Notification.PRIORITY_MAX)
                                    .build();

                            notificationDetail.bigContentView = notificationView;
                            notificationManager.notify(Constants.PARENT_NOTIFICATION_ID, notificationDetail);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}