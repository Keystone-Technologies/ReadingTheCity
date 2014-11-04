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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class SettingsActivity extends ListActivity {

    private static DetailsDataSource detailsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        detailsDataSource = new DetailsDataSource(this);
        List<Details> detailsList = detailsDataSource.getAllDetails();
        //List<Details> detailsList = new ArrayList<Details>();

        //for (Details d : tempList) {
        //    detailsList.add(d.);
       // }

        DetailAdapter adapter = new DetailAdapter(getBaseContext(), R.layout.detail_cell, detailsList);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    // beacon adapter for array list
    public static class DetailAdapter extends ArrayAdapter<Details> {

        // list of items
        private static List<Details> items;
        private Context context;

        public DetailAdapter(Context context, int textViewResourceId, List<Details> i) {
            super(context, textViewResourceId, i);
            items = i;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.detail_cell, null);
            }

            Switch s = (Switch) v.findViewById(R.id.detailName);

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(items.get(position).getDetail());
                    JSONObject value = jsonObject.getJSONObject("value");
                    s.setText(value.getString("name"));
                    s.setChecked(items.get(position).getResponse() != 0);
                    s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if(isChecked) {
                                //do stuff when Switch is ON
                               // for (int i = 0; i < items.size(); i++) {
                                //    if (compoundButton.getText().toString().equals(items.get(i).getName())) {
                                 //       dataSource.setYesResponse(items.get(i).getId());
                                 //   }
                               // }
                            } else {
                                //do stuff when Switch if OFF
                               // for (int i = 0; i < items.size(); i++) {
                               //     if (compoundButton.getText().toString().equals(items.get(i).getName())) {
                                //        dataSource.setNoResponse(items.get(i).getId());
                               //     }
                               // }
                            }
                        }
                    });
                } catch (Exception ex) {

                }
            }
            return v;
        }
    }
}