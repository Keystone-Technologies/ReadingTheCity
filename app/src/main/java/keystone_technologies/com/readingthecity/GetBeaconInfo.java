package keystone_technologies.com.readingthecity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.estimote.sdk.Beacon;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class GetBeaconInfo extends AsyncTask<Void, Void, String> {

    private static ProgressDialog dialog;
    private Context context;
    private InputStream is = null;
    private StringBuilder sb = null;
    private Beacon beacon;

    public GetBeaconInfo(Beacon beacon, Context context) {
        super();
        this.beacon = beacon;
        this.context = context;
    }

//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        dialog = new ProgressDialog(context);
//        dialog.setCancelable(true);
//        dialog.setMessage("Loading...");
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        dialog.show();
//    }

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
            HttpPost httppost = new HttpPost(Constants.BEACON_QUERY);
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
            String fileName = String.valueOf(beacon.getMajor()) + String.valueOf(beacon.getMinor());

            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(result.getBytes());
            fos.close();

            JSONObject value = row.getJSONObject("value");

//            BeaconDataSource dataSource = new BeaconDataSource(context);
//            dataSource.setId(value.getInt("major"), value.getInt("minor"), id.toString());

            if (!value.isNull("parent")) {
                new GetBeaconDetails(value.get("parent").toString(),
                        value.getInt("major"), value.getInt("minor"), context).execute();
            } //else {
             //   BeaconTrackingService.postNotification(new BeaconDevice(value.getString("_id")), context);
           // }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}