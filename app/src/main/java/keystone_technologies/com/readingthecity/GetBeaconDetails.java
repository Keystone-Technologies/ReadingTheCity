package keystone_technologies.com.readingthecity;

import android.content.Context;
import android.os.AsyncTask;

import com.estimote.sdk.Beacon;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GetBeaconDetails extends AsyncTask<Void, Void, String> implements Serializable {

    private String parentId;
    private StringBuilder sb = null;
    private int major;
    private int minor;
    private Context context;

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

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject row = jsonArray.getJSONObject(i);
                String fileName = row.getString("id");
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                fos.write(result.getBytes());
                fos.close();
                JSONObject value = row.getJSONObject("value");
                BeaconTrackingService.postNotification(new BeaconDevice(value.getString("_id"),
                        value.getString("name")), context);

            }









//            BeaconDataSource dataSource = new BeaconDataSource(context);
//            dataSource.setId(value.getInt("major"), value.getInt("minor"), id.toString());

//            if (!value.isNull("parent")) {
//                new GetBeaconDetails(value.get("parent").toString()).execute();
//            }// else {

           // }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}