package keystone_technologies.com.readingthecity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.estimote.sdk.Beacon;

public class NotificationButtonListener extends BroadcastReceiver {

    BeaconDataSource dataSource;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        dataSource = new BeaconDataSource(context);
        String uuid = intent.getExtras().getString("uuid");
        BeaconDevice beacon = dataSource.getBeaconByUUID(uuid);

        if (action.equals("Yes")) {
            Toast.makeText(context, " YES BUTTON PRESSED!", Toast.LENGTH_SHORT).show();
            dataSource.
        } else {
            Toast.makeText(context, "NO BUTTON PRESSED!", Toast.LENGTH_SHORT).show();
        }
    }
}
