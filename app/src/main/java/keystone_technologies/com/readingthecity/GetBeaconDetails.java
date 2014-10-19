package keystone_technologies.com.readingthecity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.estimote.sdk.Beacon;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetBeaconDetails extends AsyncTask<Void, Void, String> implements Serializable {

    private String parentId;
    private StringBuilder sb = null;
    private int major;
    private int minor;
    private static Context context;

    public GetBeaconDetails(String parentId) {
        super();
        this.parentId = parentId;
    }

    public GetBeaconDetails(String parentId, int major, int minor, Context context) {
        super();
        this.parentId = parentId;
        this.major = major;
        this.minor = minor;
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
                BeaconDataSource dataSource = new BeaconDataSource(context);

                JSONObject value = row.getJSONObject("value");


                if (value.has("parent")) {
                    dataSource.createBeacon(new Date().toString(), value.getString("name"),
                            value.getString("_id"), value.getString("parent"));
                    new GetBeaconDetails(value.get("parent").toString()).execute();
                } else {
                    dataSource.createBeacon(new Date().toString(), value.getString("name"),
                            value.getString("_id"));
                    BeaconTrackingService.postNotification(new BeaconDevice(value.getString("name"),
                            value.getString("_id")), context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}