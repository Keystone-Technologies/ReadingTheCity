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
    private Context context;

    public GetBeaconDetails(String parentId) {
        super();
        this.parentId = parentId;
    }

    public GetBeaconDetails(String parentId, String major, String minor, Context context) {
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
        ArrayList<BeaconDevice> beaconList = new ArrayList<BeaconDevice>();
        Date date = new Date();
        //DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("rows");

            JSONObject row = jsonArray.getJSONObject(0);
            JSONObject value = row.getJSONObject("value");

            if (!value.isNull("parent")) {
                BeaconDevice beacon = new BeaconDevice(major, minor, value.get("name").toString(),
                        value.get("parent").toString(), date,
                        value.get("url").toString(), value.get("description").toString());
                new GetBeaconDetails(value.get("parent").toString()).execute();
                beaconList = getBeaconListDeserialized();
                if (beaconList.size() == 0) {
                    beaconList.add(beacon);
                } else {
                    for (BeaconDevice b : beaconList) {
                        if (beacon.getMajor().equals(b.getMajor()) && beacon.getMinor().equals(b.getMinor())) {
                            if (beacon.getDate().compareTo(b.getDate()) >= 0) {
                                beaconList.remove(b);
                                beaconList.add(beacon);
                            }
                        }
                    }
                }
                serializeBeaconList(beaconList);
            } else {
                BeaconDevice beacon = new BeaconDevice(major, minor, value.get("name").toString(),
                        value.get("parent").toString(), date,
                        value.get("url").toString(), value.get("description").toString());
                beaconList = getBeaconListDeserialized();
                if (beaconList.size() == 0) {
                    beaconList.add(beacon);
                } else {
                    for (BeaconDevice b : beaconList) {
                        if (beacon.getMajor().equals(b.getMajor()) && beacon.getMinor().equals(b.getMinor())) {
                            if (beacon.getDate().compareTo(b.getDate()) >= 0) {
                                beaconList.remove(b);
                                beaconList.add(beacon);
                            }
                        }
                    }
                }
                serializeBeaconList(beaconList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void serializeBeaconList(ArrayList<BeaconDevice> beaconList) {
        try {
            File f = new File("beaconStorage");
            if (f.exists() && !f.isDirectory()) {
                FileOutputStream fileOut = context.openFileOutput("beaconStorage", Context.MODE_PRIVATE);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(beaconList);
                out.close();
                fileOut.close();
            } else {
                FileOutputStream fileOut = context.openFileOutput("beaconStorage", Context.MODE_PRIVATE);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(beaconList);
                out.close();
                fileOut.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private ArrayList<BeaconDevice> getBeaconListDeserialized() {
        ArrayList<BeaconDevice> beaconList = new ArrayList<BeaconDevice>();
        try {
           // File f = new File("beaconStorage");
           // if (f.exists() && !f.isDirectory()) {
            FileInputStream in = context.openFileInput("beaconStorage");
            ObjectInputStream objectInputStream = new ObjectInputStream(in);

            //InputStreamReader reader = new InputStreamReader(in);
            //BufferedReader buf = new BufferedReader(reader);

            while (objectInputStream.readObject() != null ) {
                BeaconDevice beacon = (BeaconDevice) objectInputStream.readObject();
                beaconList.add(beacon);
            }

//                BufferedInputStream buf = new BufferedInputStream(new FileInputStream("beaconStorage"));
//                //FileInputStream fileInput = context.openFileInput("beaconStorage");
//
//                while (in.available() != 0) {
//                    BeaconDevice beacon = (BeaconDevice) in.readObject();
//                    beaconList.add(beacon);
//                }
                in.close();
           // }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return beaconList;
    }
}
