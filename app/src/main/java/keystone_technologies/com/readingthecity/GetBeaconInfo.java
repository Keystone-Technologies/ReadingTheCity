package keystone_technologies.com.readingthecity;

import android.content.Context;
import android.os.AsyncTask;
import com.estimote.sdk.Beacon;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class GetBeaconInfo extends AsyncTask<Void, Void, String> {

    private Context context;
    private StringBuilder sb = null;
    private Beacon beacon;

    public GetBeaconInfo(Beacon beacon, Context context) {
        super();
        this.beacon = beacon;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String output = "";

        JSONObject jsonObject = new JSONObject();

        try {

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(beacon.getMajor());
            jsonArray.put(beacon.getMinor());
            jsonObject.put("key", jsonArray);

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(Constants.BEACON_QUERY + "[" + beacon.getMajor() + "," + beacon.getMinor() + "]");

            HttpResponse response = httpclient.execute(httpget);

              if (response != null) {
                InputStream is = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                output = sb.toString();
            }

        } catch(UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return output;
    }

    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("rows");
            JSONObject row = jsonArray.getJSONObject(0);
            JSONObject value = row.getJSONObject("value");

            BeaconsDataSource beaconsDataSource = new BeaconsDataSource(context);
            beaconsDataSource.setBeaconParent(beacon.getMajor(), beacon.getMinor(), value.get("parent").toString());
            beaconsDataSource.setBeaconId(beacon.getMajor(), beacon.getMinor(), value.get("_id").toString());
            new GetBeaconDetails(value.get("parent").toString(), context).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}