package keystone_technologies.com.readingthecity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class NotificationButtonListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("Yes")) {
            Toast.makeText(context, " YES BUTTON PRESSED!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "NO BUTTON PRESSED!", Toast.LENGTH_SHORT).show();
        }
    }
}
