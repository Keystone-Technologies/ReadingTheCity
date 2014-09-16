package keystone_technologies.com.readingthecity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class YesButtonListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "yes pressed", Toast.LENGTH_LONG);
    }
}
