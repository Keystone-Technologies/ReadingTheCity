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
import java.net.URLEncoder;
import java.util.Date;

public class GetBeaconDetails extends AsyncTask<Void, Void, String> {

    private String parentId;
    private StringBuilder sb = null;
    private static Context context;
    private static DetailsDataSource detailsDataSource;

    public GetBeaconDetails() {
        super();
    }

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
                String line;

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

        detailsDataSource = new DetailsDataSource(context);

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("rows");

            JSONObject row = jsonArray.getJSONObject(0);
            JSONObject value = row.getJSONObject("value");

            if (detailsDataSource.isDetailInDB(value.getString("_id"))) {
                if (detailsDataSource.isDetailAgeExpired(value.getString("_id"))) {
                    if (detailsDataSource.notCurrentlyFetchingDetail(value.getString("_id"))) {
                        detailsDataSource.deleteDetail(value.getString("_id"));
                        if (value.isNull("parent")) {
                            detailsDataSource.createDetail(value.getString("_id"), new Date(), value.toString());
                            NotificationOutput output = new NotificationOutput();
                            output.postNotification(new Details(value), context);
                        } else {
                            detailsDataSource.createDetail(value.getString("_id"), new Date(),
                                    value.getString("parent"), value.toString());
                            new GetBeaconDetails(value.get("parent").toString(), context).execute();
                        }
                    }
                } else {
                    NotificationOutput output = new NotificationOutput();
                    output.postNotification(new Details(value), context);
                }
            } else {
                if (value.isNull("parent")) {
                    detailsDataSource.createDetail(value.getString("_id"), new Date(), value.toString());
                    NotificationOutput output = new NotificationOutput();
                    output.postNotification(new Details(value), context);
                } else {
                    detailsDataSource.createDetail(value.getString("_id"), new Date(),
                            value.getString("parent"), value.toString());
                    new GetBeaconDetails(value.get("parent").toString(), context).execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}