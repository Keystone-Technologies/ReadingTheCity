package keystone_technologies.com.readingthecity;

import android.app.ListActivity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BeaconDataSource dataSource = new BeaconDataSource(this);
        BeaconAdapter adapter = new BeaconAdapter(getBaseContext(), R.layout.beacon_cell, dataSource.getAllBeacons());
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
            s.setText(items.get(position).getUUID());
            s.setChecked(items.get(position).getResponse() == 0 ? false : true);

            return v;
        }
    }
}
