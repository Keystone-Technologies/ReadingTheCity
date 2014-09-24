package keystone_technologies.com.readingthecity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class GetBeaconInfo extends AsyncTask<Void, Void, String> {

    private int[] majorMinorArray;
    private static ProgressDialog dialog;
    private Context context;
    private InputStream is = null;
    private StringBuilder sb = null;

    public GetBeaconInfo(int[] majorMinorArray, Context context) {
        super();
        this.majorMinorArray = majorMinorArray;
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
            jsonArray.put(majorMinorArray[0]);
            jsonArray.put(majorMinorArray[1]);
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
                new GetBeaconDetails(value.get("parent").toString(),
                        value.get("major").toString(), value.get("minor").toString(), context).execute();
            }










//            FileOutputStream fos = context.openFileOutput("beaconStorage", Context.MODE_APPEND);
//            fos.write(result.getBytes());
//            fos.close();
        } catch (Exception e) {

        }

//        try {
//            // Getting adapter by passing json data ArrayList
//            adapter = new ContactUsActivity.OfficeAdapter(activity.getBaseContext(), R.layout.cell_office, result);
//            activity.setListAdapter(adapter);
//            adapter.notifyDataSetChanged();
//            dialog.dismiss();
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
    }
}
