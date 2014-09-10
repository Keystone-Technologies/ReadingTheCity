package keystone_technologies.com.readingthecity;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

public class BluetoothStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction())){
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
           // Notification.Builder builder = new Notification.Builder(context);
          //  if (Utilities.isBlueToothEnabled()) {

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Utilities.stopService(context);
                    Utilities.stopTrackingService(context);
                    Utilities.postNotification("bluetooth", context);
                    break;
                case BluetoothAdapter.STATE_ON:
                    Utilities.startService(context);
                    Utilities.startTrackingService(context);
                    //Intent listenerIntent = new Intent(context, InvisibleActivity.class);
                    //listenerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //context.startActivity(listenerIntent);
                    Utilities.postNotification("bluetooth", context);
                    break;
            }




            //  .setAutoCancel(true)
            //  .setContentIntent(pendingIntent)
            //  .build();
            //notification.defaults |= Notification.DEFAULT_SOUND;
            //notification.defaults |= Notification.DEFAULT_LIGHTS;
            //notificationManager.notify(Constants.NOTIFICATION_ID, notification);

//            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(1, builder.build());

        }
    }
}