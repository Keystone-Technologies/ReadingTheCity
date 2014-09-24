package keystone_technologies.com.readingthecity;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GetBeaconDetails extends AsyncTask<Void, Void, String> {

    private String parentId;
    private StringBuilder sb = null;
    private String major;
    private String minor;

    public GetBeaconDetails(String parentId) {
        super();
        this.parentId = parentId;
    }

    public GetBeaconDetails(String parentId, String major, String minor) {
        super();
        this.parentId = parentId;
        this.major = major;
        this.minor = minor;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String output = "";

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("key", parentId);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.BEACON_DETAILS);
            StringEntity se = new StringEntity(jsonObject.toString());
            se.setContentType("application/json;charset=UTF-8");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
            httppost.setEntity(se);

            HttpResponse response = httpclient.execute(httppost);

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


        } catch (UnsupportedEncodingException ex) {

        } catch (IOException ex) {

        } catch (JSONException ex) {

        }


        return output;

    }

    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("rows");

            JSONObject row = jsonArray.getJSONObject(0);
            JSONObject value = row.getJSONObject("value");

            if (!value.isNull("parent")) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                BeaconDevice beacon = new BeaconDevice(major, minor, value.get("name").toString(),
                        value.get("parent").toString(), dateFormat.format(date),
                        value.get("url").toString(), value.get("description").toString());
                new GetBeaconDetails(value.get("parent").toString()).execute();
            } else {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                BeaconDevice beacon = new BeaconDevice(major, minor, value.get("name").toString(),
                        value.get("parent").toString(), dateFormat.format(date),
                        value.get("url").toString(), value.get("description").toString());
            }










//            FileOutputStream fos = context.openFileOutput("beaconStorage", Context.MODE_APPEND);
//            fos.write(result.getBytes());
//            fos.close();
        } catch (Exception e) {

        }
    }
}
