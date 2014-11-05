package keystone_technologies.com.readingthecity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationButtonListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        DetailsDataSource detailDataSource = new DetailsDataSource(context);
        String id = intent.getExtras().getString("id");

        if (action.equals("Yes")) {
            Toast.makeText(context, "YES BUTTON PRESSED!", Toast.LENGTH_SHORT).show();
            detailDataSource.setYesResponse(id);
            cancelNotification(context);
            Details detail = detailDataSource.getChildDetailFromId(id);
            if (detail != null) {
                BeaconTrackingService.postNotification(detail, context);
            }
        } else {
            Toast.makeText(context, "NO BUTTON PRESSED!", Toast.LENGTH_SHORT).show();
            cancelNotification(context);
        }
    }

    public void cancelNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(ns);
        notificationManager.cancel(Constants.BEACON_NOTIFICATION_ID);
    }
}