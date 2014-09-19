package keystone_technologies.com.readingthecity;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Utilities.isBlueToothEnabled()) {
            BluetoothStateReceiver.stopTrackingService(this);
            BluetoothStateReceiver.stopEstimoteService(this);
            BluetoothStateReceiver.startEstimoteService(this);
            BluetoothStateReceiver.startTrackingService(this);
        }
    }
}
