package keystone_technologies.com.readingthecity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GetBeaconInfo extends AsyncTask<Void, Void, Void> {

    private String UUID;
    private static ProgressDialog dialog;
    private Context context;
    private String output = "";
    private InputStream is = null;
    private StringBuilder sb = null;

    public GetBeaconInfo(String uuid, Context context) {
        super();
        this.UUID = uuid;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setCancelable(true);
        dialog.setMessage("Loading...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        //http post

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.BEACON_INFO_URL);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {

        }

        // convert response to string

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            output = sb.toString();
        } catch (Exception e) {

        }

        try {
            JSONObject jsonObject = new JSONObject(output);
            JSONObject jObject = jsonObject.getJSONObject("offices");
            JSONArray jOffices = jObject.getJSONArray("office");

            for (int j = 0; j < jOffices.length(); j++) {
                JSONObject jsonDataOffices = jOffices.getJSONObject(j);

//                BeaconDevice device = new BeaconDevice();
//                o.setCity(jsonDataOffices.getString("City") + "-" + jsonDataOffices.getString("Code"));
//                o.setEmail(jsonDataOffices.getString("Email"));
//                o.setPhone(jsonDataOffices.getString("Phone"));
//                officeList.add(o);
            }
        } catch (JSONException e1) {

        } catch (ParseException e1) {

        }

        return null;
    }

//    protected void onPostExecute(ArrayList<Office> result) {
//        try {
//            // Getting adapter by passing json data ArrayList
//            adapter = new ContactUsActivity.OfficeAdapter(activity.getBaseContext(), R.layout.cell_office, result);
//            activity.setListAdapter(adapter);
//            adapter.notifyDataSetChanged();
//            dialog.dismiss();
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//    }
}
