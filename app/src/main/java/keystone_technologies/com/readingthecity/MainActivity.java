package keystone_technologies.com.readingthecity;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.estimote.sdk.Beacon;

import java.util.List;

public class MainActivity extends Activity {

    private static View view;
    private static TextView major1;
    private static Context context;

//    public MainActivity() {
//        super();
//    }

    public static void populateList(List<Beacon> beacons) {
        //for (int i = 0; i < beacons.size(); i++) {
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       // view = inflater.inflate(R.layout.activity_main, null);
            if (beacons.size() == 1) {

                major1.setText(beacons.get(0).getMajor());

                TextView minor1 = (TextView) view.findViewById(R.id.minor1);
                minor1.setText(beacons.get(0).getMinor());

                TextView rssi1 = (TextView) view.findViewById(R.id.rssi1);
                rssi1.setText(beacons.get(0).getRssi());
            } else if (beacons.size() == 2) {
                TextView major1 = (TextView) view.findViewById(R.id.major1);
                TextView major2 = (TextView) view.findViewById(R.id.major2);
                major1.setText(beacons.get(0).getMajor());
                major2.setText(beacons.get(1).getMajor());

                TextView minor1 = (TextView) view.findViewById(R.id.minor1);
                TextView minor2 = (TextView) view.findViewById(R.id.minor2);
                minor1.setText(beacons.get(0).getMinor());
                minor2.setText(beacons.get(1).getMinor());

                TextView rssi1 = (TextView) view.findViewById(R.id.rssi1);
                TextView rssi2 = (TextView) view.findViewById(R.id.rssi2);
                rssi1.setText(beacons.get(0).getRssi());
                rssi2.setText(beacons.get(1).getRssi());
            } else if (beacons.size() == 3) {
                TextView major1 = (TextView) view.findViewById(R.id.major1);
                TextView major2 = (TextView) view.findViewById(R.id.major2);
                TextView major3 = (TextView) view.findViewById(R.id.major3);
                major1.setText(beacons.get(0).getMajor());
                major2.setText(beacons.get(1).getMajor());
                major3.setText(beacons.get(2).getMajor());

                TextView minor1 = (TextView) view.findViewById(R.id.minor1);
                TextView minor2 = (TextView) view.findViewById(R.id.minor2);
                TextView minor3 = (TextView) view.findViewById(R.id.minor3);
                minor1.setText(beacons.get(0).getMinor());
                minor2.setText(beacons.get(1).getMinor());
                minor3.setText(beacons.get(2).getMinor());

                TextView rssi1 = (TextView) view.findViewById(R.id.rssi1);
                TextView rssi2 = (TextView) view.findViewById(R.id.rssi2);
                TextView rssi3 = (TextView) view.findViewById(R.id.rssi3);
                rssi1.setText(beacons.get(0).getRssi());
                rssi2.setText(beacons.get(1).getRssi());
                rssi3.setText(beacons.get(2).getRssi());
            } else if (beacons.size() == 4) {
                TextView major1 = (TextView) view.findViewById(R.id.major1);
                TextView major2 = (TextView) view.findViewById(R.id.major2);
                TextView major3 = (TextView) view.findViewById(R.id.major3);
                TextView major4 = (TextView) view.findViewById(R.id.major4);
                major1.setText(beacons.get(0).getMajor());
                major2.setText(beacons.get(1).getMajor());
                major3.setText(beacons.get(2).getMajor());
                major4.setText(beacons.get(3).getMajor());

                TextView minor1 = (TextView) view.findViewById(R.id.minor1);
                TextView minor2 = (TextView) view.findViewById(R.id.minor2);
                TextView minor3 = (TextView) view.findViewById(R.id.minor3);
                TextView minor4 = (TextView) view.findViewById(R.id.minor4);
                minor1.setText(beacons.get(0).getMinor());
                minor2.setText(beacons.get(1).getMinor());
                minor3.setText(beacons.get(2).getMinor());
                minor4.setText(beacons.get(3).getMinor());

                TextView rssi1 = (TextView) view.findViewById(R.id.rssi1);
                TextView rssi2 = (TextView) view.findViewById(R.id.rssi2);
                TextView rssi3 = (TextView) view.findViewById(R.id.rssi3);
                TextView rssi4 = (TextView) view.findViewById(R.id.rssi4);
                rssi1.setText(beacons.get(0).getRssi());
                rssi2.setText(beacons.get(1).getRssi());
                rssi3.setText(beacons.get(2).getRssi());
                rssi4.setText(beacons.get(3).getRssi());
            }
     //   }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        major1 = (TextView) findViewById(R.id.major1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isBlueToothEnabled()) {
            if (!isMyServiceRunning(com.estimote.sdk.service.BeaconService.class)) {
                BluetoothStateReceiver.startEstimoteService(this);
            }
            if (!isMyServiceRunning(BeaconTrackingService.class)) {
                BluetoothStateReceiver.startTrackingService(this);
            }
        }
    }

    public static Boolean isBlueToothEnabled() {
        BluetoothAdapter blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueToothAdapter == null || !blueToothAdapter.isEnabled()) {
            // Device doesn't support bluetooth or not enabled
            return false;
        }
        return true;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void settings(View v) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class );
        startActivity(settingsIntent);
    }
}
