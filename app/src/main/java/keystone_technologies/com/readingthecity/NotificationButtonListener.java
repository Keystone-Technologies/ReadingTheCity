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
        BeaconDataSource dataSource = new BeaconDataSource(context);
        String id = intent.getExtras().getString("id");

        if (action.equals("Yes")) {
            Toast.makeText(context, "YES BUTTON PRESSED!", Toast.LENGTH_SHORT).show();
            dataSource.setYesResponse(id);
            cancelNotification(context, Constants.BEACON_NOTIFICATION_ID);
            BeaconDevice beacon = dataSource.getChildBeaconFromId(id);
            if (beacon != null) {
                if (dataSource.hasNotBeenNotified(id)) {
                    BeaconTrackingService.postNotification(beacon, context);
                    dataSource.setNotifiedFlag(id);
                }
            }
        } else {
            Toast.makeText(context, "NO BUTTON PRESSED!", Toast.LENGTH_SHORT).show();
            dataSource.setNoResponse(id);
            cancelNotification(context, Constants.BEACON_NOTIFICATION_ID);
        }
    }

    public void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(ns);
        notificationManager.cancel(notifyId);
    }
}