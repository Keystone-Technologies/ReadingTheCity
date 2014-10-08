package keystone_technologies.com.readingthecity;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.util.Log;

import com.estimote.sdk.Beacon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BeaconDataSource {

    private SQLiteDatabase database;
    private ServiceTable dbServiceTable;
    private String[] allColumns = { ServiceTable.COLUMN_MAJOR, ServiceTable.COLUMN_MINOR,
            ServiceTable.COLUMN_DATE, ServiceTable.COLUMN_NAME,
            ServiceTable.COLUMN_ID, ServiceTable.COLUMN_PARENT, ServiceTable.COLUMN_RESPOMSE };

    public BeaconDataSource(Context context) {
        dbServiceTable = new ServiceTable(context);
    }

    public void open() throws SQLException {
        database = dbServiceTable.getWritableDatabase();
    }

    public void close() {
        dbServiceTable.close();
    }

    public void createBeacon(int major, int minor, String date, int response) {
        open();
        ContentValues values = new ContentValues();
        values.put(ServiceTable.COLUMN_MAJOR, major);
        values.put(ServiceTable.COLUMN_MINOR, minor);
        values.put(ServiceTable.COLUMN_DATE, date);
        values.put(ServiceTable.COLUMN_RESPOMSE, response);

        database.insert(ServiceTable.TABLE_SERVICE, null, values);
        close();

//        Cursor cursor = database.query(ServiceTable.TABLE_SERVICE, allColumns, ServiceTable.COLUMN_BEACON +
//                " = " + UUID, null, null, null, null);
//        cursor.moveToFirst();
//        BeaconDevice newBeacon = cursorToBeacon(cursor);
//        cursor.close();
//        return newBeacon;
    }

    public void createBeacon(int major, int minor, String date, String name, String id, int response) {
        open();
        ContentValues values = new ContentValues();
        values.put(ServiceTable.COLUMN_MAJOR, major);
        values.put(ServiceTable.COLUMN_MINOR, minor);
        values.put(ServiceTable.COLUMN_DATE, date);
        values.put(ServiceTable.COLUMN_NAME, name);
        values.put(ServiceTable.COLUMN_ID, id);
        values.put(ServiceTable.COLUMN_RESPOMSE, response);

        database.insert(ServiceTable.TABLE_SERVICE, null, values);
        close();
    }

    public int getBeaconResponse(String id) {
        open();

        int response = 0;

        String[] result_column = new String[] {ServiceTable.COLUMN_ID, ServiceTable.COLUMN_RESPOMSE};
        String where = ServiceTable.COLUMN_ID + "=" + id;

        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        Cursor cursor = database.query(ServiceTable.TABLE_SERVICE, result_column, where, whereArgs, groupBy, having, order);
        int RESPONSE_INDEX = cursor.getColumnIndexOrThrow(ServiceTable.COLUMN_RESPOMSE);
        while (cursor.moveToNext()) {
            response = cursor.getInt(RESPONSE_INDEX);
        }
        cursor.close();
        return response;
    }

    public void setYesResponse(String id) {
        open();

        ContentValues values = new ContentValues();

        //if (getBeaconResponse(uuid) == 0) {
            values.put(ServiceTable.COLUMN_RESPOMSE, 1);
        //} else {
        //    values.put(ServiceTable.COLUMN_RESPOMSE, 0);
       // }

        String where = ServiceTable.COLUMN_ID + "=?";
        String whereArgs[] = new String[] {id};

        try {
            database.update(ServiceTable.TABLE_SERVICE, values, where, whereArgs);
        } catch (SQLException e) {
            Log.e("Error", e.toString());
        }

        database.close();
    }

    public void setId(int major, int minor, String id) {
        open();

        ContentValues values = new ContentValues();
        values.put(ServiceTable.COLUMN_ID, id);

        String where = ServiceTable.COLUMN_MAJOR + "=?" + " AND " + ServiceTable.COLUMN_MINOR + "=?";
        String whereArgs[] = new String[] {String.valueOf(major), String.valueOf(minor)};

        try {
            database.update(ServiceTable.TABLE_SERVICE, values, where, whereArgs);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        database.close();
    }

    public void setName(String id, String name) {
        open();

        ContentValues values = new ContentValues();
        values.put(ServiceTable.COLUMN_NAME, name);

        String where = ServiceTable.COLUMN_ID + "=?";
        String whereArgs[] = new String[] {String.valueOf(id)};

        try {
            database.update(ServiceTable.TABLE_SERVICE, values, where, whereArgs);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        database.close();
    }

    public void setNoResponse(String id) {
        open();

        ContentValues values = new ContentValues();

        //if (getBeaconResponse(uuid) == 0) {
        values.put(ServiceTable.COLUMN_RESPOMSE, 0);
        //} else {
        //    values.put(ServiceTable.COLUMN_RESPOMSE, 0);
        // }

        String where = ServiceTable.COLUMN_ID + "=?";
        String whereArgs[] = new String[] {id};

        try {
            database.update(ServiceTable.TABLE_SERVICE, values, where, whereArgs);
        } catch (SQLException e) {
            Log.e("Error", e.toString());
        }
        database.close();
    }

    public void deleteBeacon(BeaconDevice beacon) {
        String id = beacon.getId();
        database.delete(ServiceTable.TABLE_SERVICE, ServiceTable.COLUMN_ID + " = " + id, null);
    }

//    public BeaconDevice getBeaconByUUID(String UUID) {
//        List<BeaconDevice> beacons = getAllBeacons();
//        for (BeaconDevice b : beacons) {
//            if (UUID.equals(b.getUUID())) {
//                return b;
//            }
//        }
//        return null;
//    }

    public List<BeaconDevice> getAllBeacons() {
        List<BeaconDevice> beacons = new ArrayList<BeaconDevice>();
        open();

        Cursor cursor = database.query(ServiceTable.TABLE_SERVICE, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            BeaconDevice beacon = cursorToBeacon(cursor);
            beacons.add(beacon);
            cursor.moveToNext();
        }

        cursor.close();
        close();
        return beacons;
    }

    private BeaconDevice cursorToBeacon(Cursor cursor) {
        BeaconDevice beacon = new BeaconDevice();
        beacon.setMajor(cursor.getInt(0));
        beacon.setMinor(cursor.getInt(1));
        String date = cursor.getString(2);
        try {
            beacon.setDate(new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(date));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        beacon.setId(cursor.getString(3));
        beacon.setParent(cursor.getString(4));
        beacon.setResponse(cursor.getInt(5));
        return beacon;
    }
}