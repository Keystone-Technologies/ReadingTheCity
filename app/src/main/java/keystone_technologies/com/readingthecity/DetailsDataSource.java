package keystone_technologies.com.readingthecity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.estimote.sdk.Beacon;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailsDataSource {

    private SQLiteDatabase database;
    private DetailsTable dbDetailsTable;
    private String[] allColumns = { DetailsTable.COLUMN_ID, DetailsTable.COLUMN_DETAILS,
            DetailsTable.COLUMN_DATE, DetailsTable.COLUMN_RESPONSE};

    public DetailsDataSource(Context context) {
        dbDetailsTable = new DetailsTable(context);
    }

    public void open() throws SQLException {
        database = dbDetailsTable.getWritableDatabase();
    }

    public void close() {
        dbDetailsTable.close();
    }

    public void createDetail(String detail, String date, String id) {
        open();
        ContentValues values = new ContentValues();
        values.put(DetailsTable.COLUMN_DETAILS, detail);
        values.put(DetailsTable.COLUMN_DATE, date);
        values.put(DetailsTable.COLUMN_ID, id);

        database.insert(DetailsTable.TABLE_DETAILS, null, values);
        close();
    }

    public String getDetailsFromDevice(Device device) {
        String details = null;

        List<Details> detailsList = getAllDetails();
        for (Details d : detailsList) {
            if (d.getId().equals(device.getId())) {
                details = d.getDetail();
            }
        }
        return details;
    }

    public Details getChildDetailFromId(String id) {
        Details detail = new Details();
        List<Details> detailsList = getAllDetails();
        try {
            for (Details d : detailsList) {
                JSONObject jsonObject = new JSONObject(d.getDetail());
                JSONArray jsonArray = jsonObject.getJSONArray("rows");
                JSONObject row = jsonArray.getJSONObject(0);
                JSONObject value = row.getJSONObject("value");
                if (id.equals(value.getString("parent"))) {
                    detail = d;
                }
            }
        } catch (Exception ex) {

        }
        return detail;
    }

//    public int getBeaconResponse(String id) {
//        open();
//
//        int response = 0;
//
//        String[] result_column = new String[] {ServiceTable.COLUMN_ID, ServiceTable.COLUMN_RESPONSE};
//        String where = ServiceTable.COLUMN_ID + "=" + id;
//
//        String whereArgs[] = null;
//        String groupBy = null;
//        String having = null;
//        String order = null;
//
//        Cursor cursor = database.query(ServiceTable.TABLE_SERVICE, result_column, where, whereArgs, groupBy, having, order);
//        int RESPONSE_INDEX = cursor.getColumnIndexOrThrow(ServiceTable.COLUMN_RESPONSE);
//        while (cursor.moveToNext()) {
//            response = cursor.getInt(RESPONSE_INDEX);
//        }
//        cursor.close();
//        return response;
//    }

    public void setYesResponse(String id) {
        open();

        ContentValues values = new ContentValues();

        //if (getBeaconResponse(uuid) == 0) {
            values.put(DetailsTable.COLUMN_RESPONSE, 1);
        //} else {
        //    values.put(ServiceTable.COLUMN_RESPOMSE, 0);
       // }

        String where = DetailsTable.COLUMN_ID + "=?";
        String whereArgs[] = new String[] {id};

        try {
            database.update(DetailsTable.TABLE_DETAILS, values, where, whereArgs);
        } catch (SQLException e) {
            Log.e("Error", e.toString());
        }

        database.close();
    }

    public void setNoResponse(String id) {
        open();

        ContentValues values = new ContentValues();

        //if (getBeaconResponse(uuid) == 0) {
        values.put(DetailsTable.COLUMN_RESPONSE, 0);
        //} else {
        //    values.put(ServiceTable.COLUMN_RESPOMSE, 0);
        // }

        String where = DetailsTable.COLUMN_ID + "=?";
        String whereArgs[] = new String[] {id};

        try {
            database.update(DetailsTable.TABLE_DETAILS, values, where, whereArgs);
        } catch (SQLException e) {
            Log.e("Error", e.toString());
        }
        database.close();
    }

    public boolean isDetailInDB(String id) {
        List<Details> detailsList = getAllDetails();
        boolean flag = false;

        for (Details bd : detailsList) {
            if (bd.getId() != null) {
                if (id.equals(bd.getId())) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    public void deleteDetail(String id) {
        open();
        database.delete(DetailsTable.TABLE_DETAILS, DetailsTable.COLUMN_ID + "='" + id + "'", null);
        close();
    }

    public List<Details> getAllDetails() {
        List<Details> details = new ArrayList<Details>();
        open();

        Cursor cursor = database.query(DetailsTable.TABLE_DETAILS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Details detail = cursorToBeacon(cursor);
            details.add(detail);
            cursor.moveToNext();
        }

        cursor.close();
        close();
        return details;
    }

    private Details cursorToBeacon(Cursor cursor) {
        Details detail = new Details();
        detail.setId(cursor.getString(0));
        detail.setDetail(cursor.getString(1));
        String date = cursor.getString(2);
        try {
            detail.setDate(new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(date));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        detail.setResponse(cursor.getInt(3));
        return detail;
    }
}