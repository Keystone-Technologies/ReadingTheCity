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
import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends ListActivity {

    private static DetailsDataSource detailsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        detailsDataSource = new DetailsDataSource(this);
        List<Details> detailsList = detailsDataSource.getAllDetails();

        DetailsAdapter adapter = new DetailsAdapter(getBaseContext(), R.layout.detail_cell, filterDetailList(detailsList));
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private List<Details> filterDetailList(List<Details> list) {
        List<Details> settingsList = new ArrayList<Details>();

        for (Details d : list) {
            if (d.getResponse() == 0 || d.getResponse() == 1) {
                settingsList.add(d);
            }
        }

        return settingsList;
    }

    // beacon adapter for array list
    public static class DetailsAdapter extends ArrayAdapter<Details> {

        // list of items
        private static List<Details> items;
        private Context context;

        public DetailsAdapter(Context context, int textViewResourceId, List<Details> i) {
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
                    JSONObject jsonObject = items.get(position).getDetail();
                    JSONArray jsonArray = jsonObject.getJSONArray("rows");

                    JSONObject row = jsonArray.getJSONObject(0);
                    JSONObject value = row.getJSONObject("value");

                    s.setText(value.getString("name"));

                    int flag = items.get(position).getResponse();
                    if (flag == 0) {
                        s.setChecked(false);
                    } else {
                        s.setChecked(true);
                    }

                    s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if(isChecked) {
                                for (int i = 0; i < items.size(); i++) {
                                    if (getName(i, items).contains(compoundButton.getText().toString())) {
                                        detailsDataSource.setYesResponse(items.get(i).getId());
                                    }
                                }
                            } else {
                                for (int i = 0; i < items.size(); i++) {
                                    if (getName(i, items).contains(compoundButton.getText().toString())) {
                                        detailsDataSource.setNoResponse(items.get(i).getId());
                                    }
                                }
                            }
                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return v;
        }
    }

    private static String getName(int i, List<Details> items) {
        String name = null;

        try {
            JSONObject jsonObject = items.get(i).getDetail();
            JSONArray jsonArray = jsonObject.getJSONArray("rows");

            JSONObject row = jsonArray.getJSONObject(0);
            JSONObject value = row.getJSONObject("value");
            name = value.getString("name");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return name;
    }
}