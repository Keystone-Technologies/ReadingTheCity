package keystone_technologies.com.readingthecity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URLEncoder;

public class GetBeaconDetails extends AsyncTask<Void, Void, String> implements Serializable {

    private String parentId;
    private StringBuilder sb = null;
    private static Context context;
    private static DetailsDataSource detailsDataSource;
    private static NotificationsDataSource notificationsDataSource;

    public GetBeaconDetails() {
        super();
    }

    public GetBeaconDetails(String parentId) {
        super();
        this.parentId = parentId;
    }

    public GetBeaconDetails(String parentId, Context context) {
        super();
        this.parentId = parentId;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String output = "";

        try {

            HttpClient httpClient = new DefaultHttpClient();
            String url = Constants.BEACON_DETAILS + URLEncoder.encode('"' + parentId + '"', "UTF-8");
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);

            if (response != null) {
                InputStream is = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                output = sb.toString();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("rows");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject row = jsonArray.getJSONObject(i);
                DetailsDataSource detailsDataSource = new DetailsDataSource(context);

                JSONObject value = row.getJSONObject("value");

                if (value.has("parent")) {
                    if (detailsDataSource.isDetailInDB(value.getString("_id"))) {
                        detailsDataSource.deleteDetail(value.getString("_id"));
                    }

                    new GetBeaconDetails(value.getString("parent")).execute();
                    detailsDataSource.createDetail(value.getString("_id"), value.toString());
                } else {
                    if (detailsDataSource.isDetailInDB(value.getString("_id"))) {
                        detailsDataSource.deleteDetail(value.getString("_id"));
                    }
                    detailsDataSource.createDetail(value.getString("_id"), value.toString());
                    postNotification(new Details(row.toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postNotification(Details detail) {
        detailsDataSource = new DetailsDataSource(context);
        notificationsDataSource = new NotificationsDataSource(context);
        final Intent infoIntent = new Intent(context, BeaconInfoActivity.class);

        //  final PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, infoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            final JSONObject jsonObject = new JSONObject(detail.getDetail());
            if (jsonObject.isNull("value")) {
                if (!notificationsDataSource.isPostInDB(jsonObject.getString("_id"))) {
                    final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);




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
                            protected void onPostExecute( Bitmap result ) {
                                RemoteViews notificationView = null;

                                try {
                                    JSONArray urlArray = jsonObject.getJSONArray("url");
                                    infoIntent.putExtra("url", urlArray.get(0).toString());

                                    infoIntent.putExtra("name", jsonObject.getString("name"));
                                    PendingIntent pendingIntent =
                                            PendingIntent.getActivity(context, 0, infoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                    if (detailsDataSource.getChildDetailFromId(jsonObject.getString("_id")) == null) {
                                        notificationView = new RemoteViews(context.getPackageName(), R.layout.nochild_notification_layout);
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
                                        notificationManager.notify(Constants.BEACON_NOTIFICATION_ID, notificationDetail);
                                    } else {
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

                                        PendingIntent pendingYesIntent = PendingIntent.getBroadcast(context, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        notificationView.setOnClickPendingIntent(R.id.btnYes, pendingYesIntent);


                                        Intent noIntent = new Intent(context, NotificationButtonListener.class);
                                        noIntent.setAction("No");
                                        noIntent.putExtra("id", jsonObject.getString("_id"));
                                        noIntent.putExtra("name", jsonObject.getString("name"));

                                        PendingIntent pendingNoIntent = PendingIntent.getBroadcast(context, 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        notificationView.setOnClickPendingIntent(R.id.btnNo, pendingNoIntent);

                                        Notification notificationDetail = new Notification.Builder(context)

                                                .setSmallIcon(R.drawable.beacon_gray)
                                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                                                .setContentIntent(pendingIntent)
                                                .setPriority(Notification.PRIORITY_MAX)
                                                .build();

                                        notificationDetail.bigContentView = notificationView;
                                        notificationManager.notify(Constants.BEACON_NOTIFICATION_ID, notificationDetail);
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

                            RemoteViews notificationView = null;

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
                                notificationManager.notify(Constants.BEACON_NOTIFICATION_ID, notificationDetail);
                            } else {
                                Intent yesIntent = new Intent(context, NotificationButtonListener.class);
                                yesIntent.setAction("Yes");
                                yesIntent.putExtra("id", jsonObject.getString("_id"));
                                yesIntent.putExtra("name", jsonObject.getString("name"));

                                PendingIntent pendingYesIntent = PendingIntent.getBroadcast(context, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                notificationView.setOnClickPendingIntent(R.id.btnYes, pendingYesIntent);

                                Intent noIntent = new Intent(context, NotificationButtonListener.class);
                                noIntent.setAction("No");
                                noIntent.putExtra("id", jsonObject.getString("_id"));
                                noIntent.putExtra("name", jsonObject.getString("name"));

                                PendingIntent pendingNoIntent = PendingIntent.getBroadcast(context, 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                notificationView.setOnClickPendingIntent(R.id.btnNo, pendingNoIntent);

                                Notification notificationDetail = new Notification.Builder(context)

                                        .setSmallIcon(R.drawable.beacon_gray)
                                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                                        .setContentIntent(pendingIntent)
                                        .setPriority(Notification.PRIORITY_MAX)
                                        .build();

                                notificationDetail.bigContentView = notificationView;
                                notificationManager.notify(Constants.BEACON_NOTIFICATION_ID, notificationDetail);
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    try {
                        if (notificationsDataSource.getNotificationResponse(jsonObject.getString("_id")) != 0) {
                            Details childDetail = detailsDataSource.getChildDetailFromId(jsonObject.getString("_id"));
                            if (childDetail != null) {
                                postNotification(childDetail);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                final JSONObject value = jsonObject.getJSONObject("value");
                if (!notificationsDataSource.isPostInDB(value.getString("_id"))) {
                    final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    /** set a custom layout to the notification in notification drawer  */
                    final RemoteViews notificationView = new RemoteViews(context.getPackageName(), R.layout.beacon_notification_layout);

                    notificationView.setTextViewText(R.id.detailName, value.getString("name"));
                    notificationView.setTextViewText(R.id.detailDescription, value.getString("description"));


                    if (!value.isNull("thumbnail")) {
                        new AsyncTask<Void, Void, Bitmap>() {
                            @Override
                            protected Bitmap doInBackground(final Void... params) {
                                Bitmap icon = null;
                                try {
                                    String url = value.getString("thumbnail");

                                    InputStream in = new java.net.URL(url).openStream();
                                    icon = BitmapFactory.decodeStream(in);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                return icon;
                            }

                            @Override
                            protected void onPostExecute( Bitmap result ) {
                                if (result != null) {
                                    notificationView.setImageViewBitmap(R.id.leftImage, result);
                                }

                                try {

                                    JSONArray urlLinkArray = value.getJSONArray("url");
                                    infoIntent.putExtra("url", urlLinkArray.get(0).toString());

                                    //infoIntent.putExtra("url", jsonObject.getString("url"));

                                    infoIntent.putExtra("name", value.getString("name"));
                                    PendingIntent pendingIntent =
                                            PendingIntent.getActivity(context, 0, infoIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                                    Intent yesIntent = new Intent(context, NotificationButtonListener.class);
                                    yesIntent.setAction("Yes");
                                    yesIntent.putExtra("id", value.getString("_id"));
                                    yesIntent.putExtra("name", value.getString("name"));

                                    PendingIntent pendingYesIntent = PendingIntent.getBroadcast(context, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    notificationView.setOnClickPendingIntent(R.id.btnYes, pendingYesIntent);

                                    Intent noIntent = new Intent(context, NotificationButtonListener.class);
                                    noIntent.setAction("No");
                                    noIntent.putExtra("id", value.getString("_id"));
                                    noIntent.putExtra("name", value.getString("name"));

                                    Notification notificationDetail = new Notification.Builder(context)
                                            .setSmallIcon(R.drawable.beacon_gray)
                                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                                            .setContentIntent(pendingIntent)
                                            .setPriority(Notification.PRIORITY_MAX)
                                            .build();

                                    notificationDetail.bigContentView = notificationView;
                                    notificationManager.notify(Constants.BEACON_NOTIFICATION_ID, notificationDetail);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }.execute();
                    } else {
                        try {
                            JSONArray urlLinkArray = value.getJSONArray("url");
                            infoIntent.putExtra("url", urlLinkArray.get(0).toString());

                            //infoIntent.putExtra("url", jsonObject.getString("url"));


                            infoIntent.putExtra("name", value.getString("name"));
                            PendingIntent pendingIntent =
                                    PendingIntent.getActivity(context, 0, infoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            Intent yesIntent = new Intent(context, NotificationButtonListener.class);
                            yesIntent.setAction("Yes");
                            yesIntent.putExtra("id", value.getString("_id"));
                            yesIntent.putExtra("name", value.getString("name"));

                            PendingIntent pendingYesIntent = PendingIntent.getBroadcast(context, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            notificationView.setOnClickPendingIntent(R.id.btnYes, pendingYesIntent);

                            Intent noIntent = new Intent(context, NotificationButtonListener.class);
                            noIntent.setAction("No");
                            noIntent.putExtra("id", value.getString("_id"));
                            noIntent.putExtra("name", value.getString("name"));

                            Notification notificationDetail = new Notification.Builder(context)
                                    .setSmallIcon(R.drawable.beacon_gray)
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                                    .setContentIntent(pendingIntent)
                                    .setPriority(Notification.PRIORITY_MAX)
                                    .build();

                            notificationDetail.bigContentView = notificationView;
                            notificationManager.notify(Constants.BEACON_NOTIFICATION_ID, notificationDetail);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    try {
                        if (notificationsDataSource.getNotificationResponse(value.getString("_id")) != 0) {
                            Details childDetail = detailsDataSource.getChildDetailFromId(value.getString("_id"));
                            if (childDetail != null) {
                                postNotification(childDetail);
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
}