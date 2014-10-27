package keystone_technologies.com.readingthecity;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends ListActivity {

    private static BeaconDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dataSource = new BeaconDataSource(this);
        List<BeaconDevice> tempList = dataSource.getAllBeacons();
        List<BeaconDevice> beaconList = new ArrayList<BeaconDevice>();

        for (BeaconDevice bd : tempList) {
            if (bd.getId() != null) {
               // if (!dataSource.hasNotBeenNotified(bd.getId())) {
                    beaconList.add(bd);
                //}
            }
        }

        BeaconAdapter adapter = new BeaconAdapter(getBaseContext(), R.layout.beacon_cell, beaconList);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    // beacon adapter for array list
    public static class BeaconAdapter extends ArrayAdapter<BeaconDevice> {

        // list of items
        private static List<BeaconDevice> items;
        private Context context;

        public BeaconAdapter(Context context, int textViewResourceId, List<BeaconDevice> i) {
            super(context, textViewResourceId, i);
            items = i;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.beacon_cell, null);
            }

            Switch s = (Switch) v.findViewById(R.id.beaconName);

            if (s != null) {
                s.setText(items.get(position).getName());
                s.setChecked(items.get(position).getResponse() != 0);
                s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if(isChecked) {
                            //do stuff when Switch is ON
                            for (int i = 0; i < items.size(); i++) {
                                if (compoundButton.getText().toString().equals(items.get(i).getName())) {
                                    dataSource.setYesResponse(items.get(i).getId());
                                }
                            }
                        } else {
                            //do stuff when Switch if OFF
                            for (int i = 0; i < items.size(); i++) {
                                if (compoundButton.getText().toString().equals(items.get(i).getName())) {
                                    dataSource.setNoResponse(items.get(i).getId());
                                }
                            }
                        }
                    }
                });
            }
            return v;
        }
    }
}