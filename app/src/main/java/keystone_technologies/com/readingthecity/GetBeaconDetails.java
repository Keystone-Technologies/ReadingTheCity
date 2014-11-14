package keystone_technologies.com.readingthecity;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Date;

public class GetBeaconDetails extends AsyncTask<Void, Void, String> implements Serializable {

    private String parentId;
    private StringBuilder sb = null;
    private static Context context;

    public GetBeaconDetails(String parentId) {
        super();
        this.parentId = parentId;
    }

    public GetBeaconDetails(String parentId, Context context) {
        super();
        this.parentId = parentId;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String output = "";

        try {

            HttpClient httpClient = new DefaultHttpClient();
            String url = Constants.BEACON_DETAILS + URLEncoder.encode('"' + parentId + '"', "UTF-8");
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);

            if (response != null) {
                InputStream is = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                output = sb.toString();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("rows");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject row = jsonArray.getJSONObject(i);
                DetailsDataSource detailsDataSource = new DetailsDataSource(context);

                JSONObject value = row.getJSONObject("value");

                if (value.has("parent")) {
                    if (detailsDataSource.isDetailInDB(value.getString("_id"))) {
                        detailsDataSource.deleteDetail(value.getString("_id"));
                    }

                    new GetBeaconDetails(value.getString("parent")).execute();
                    detailsDataSource.createDetail(value.getString("_id"), value.toString());
                } else {
                    if (detailsDataSource.isDetailInDB(value.getString("_id"))) {
                        detailsDataSource.deleteDetail(value.getString("_id"));
                    }
                    detailsDataSource.createDetail(value.getString("_id"), value.toString());
                    BeaconTrackingService.postNotification(new Details(row.toString()), context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}