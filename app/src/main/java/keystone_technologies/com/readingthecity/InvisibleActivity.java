package keystone_technologies.com.readingthecity;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;


public class InvisibleActivity extends Activity {

    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_invisible);

        beaconManager = new BeaconManager(this);


        beaconManager.setRangingListener(new BeaconManager.RangingListener() {

            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {

                Log.d("before", "onbeaconsdiscovered");
                System.out.println("*****************************here**********************");
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("before", "inside run");
                        // Note that beacons reported here are already sorted by estimated
                        // distance between device and beacon.
                        //getActionBar().setSubtitle("Found beacons: " + beacons.size());
                        System.out.println("*****************************here**********************");
                        //Utilities.postNotification("Found beacon!", this);
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        //Log.e(TAG, "onStart");
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(Constants.ALL_ESTIMOTE_BEACONS_REGION);
                    Toast.makeText(getApplicationContext(), "try start ranging", Toast.LENGTH_LONG).show();

                } catch (RemoteException e) {
                    // Log.e(TAG, "Cannot start ranging", e);
                    Toast.makeText(getApplicationContext(), "Cannot start ranging", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
