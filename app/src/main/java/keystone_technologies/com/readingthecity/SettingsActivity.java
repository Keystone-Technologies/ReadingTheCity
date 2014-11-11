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
import org.json.JSONObject;

import java.util.List;


public class SettingsActivity extends ListActivity {

    private static NotificationsDataSource notificationsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        notificationsDataSource = new NotificationsDataSource(this);
        List<Posts> notificationList = notificationsDataSource.getAllPosts();

        PostsAdapter adapter = new PostsAdapter(getBaseContext(), R.layout.detail_cell, notificationList);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    // beacon adapter for array list
    public static class PostsAdapter extends ArrayAdapter<Posts> {

        // list of items
        private static List<Posts> items;
        private Context context;

        public PostsAdapter(Context context, int textViewResourceId, List<Posts> i) {
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
                s.setText(items.get(position).getName());
                s.setChecked(items.get(position).getResponse() != 0);
                    s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if(isChecked) {
                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).getName().contains(compoundButton.getText().toString())) {
                                        notificationsDataSource.setYesResponse(items.get(i).getId());
                                   }
                                }
                            } else {
                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).getName().contains(compoundButton.getText().toString())) {
                                        notificationsDataSource.setNoResponse(items.get(i).getId());
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