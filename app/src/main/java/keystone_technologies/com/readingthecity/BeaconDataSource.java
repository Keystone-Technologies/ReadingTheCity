package keystone_technologies.com.readingthecity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;

import com.estimote.sdk.Beacon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BeaconDataSource {

    private SQLiteDatabase database;
    private BeaconTable dbBeaconTable;
    private String[] allColumns = { BeaconTable.COLUMN_MAJOR, BeaconTable.COLUMN_MINOR,
            BeaconTable.COLUMN_DATE, BeaconTable.COLUMN_ID };

    public BeaconDataSource(Context context) {
        dbBeaconTable = new BeaconTable(context);
    }

    public void open() throws SQLException {
        database = dbBeaconTable.getWritableDatabase();
    }

    public void close() {
        dbBeaconTable.close();
    }

    public void createBeacon(int major, int minor, String date) {
        open();
        ContentValues values = new ContentValues();
        values.put(BeaconTable.COLUMN_MAJOR, major);
        values.put(BeaconTable.COLUMN_MINOR, minor);
        values.put(BeaconTable.COLUMN_DATE, date);
        //values.put(BeaconTable.COLUMN_ID, id);

        database.insert(BeaconTable.TABLE_BEACON, null, values);
        close();
    }

//    public String getIdFromDB(Beacon beacon) {
//        List<Device> beaconList = getAllBeacons();
//        String id = null;
//
//        for (Device d : beaconList) {
//            if (d.getMajor() == beacon.getMajor()) {
//                if (d.getMinor() == beacon.getMinor()) {
//                    if (d.getId() != null) {
//                        id = d.getId();
//                        break;
//                    }
//                }
//            }
//        }
//        return id;
//    }


    public boolean isBeaconNotInDB(Beacon beacon) {
        List<Device> beaconList = getAllBeacons();
        boolean flag = true;

        for (Device b : beaconList) {
            if (b.getMajor() == beacon.getMajor()) {
                if (b.getMinor() == beacon.getMinor()) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    public void deleteBeacon(Beacon beacon) {
        open();
        database.delete(BeaconTable.TABLE_BEACON, BeaconTable.COLUMN_MAJOR + "=" + beacon.getMajor() +
                BeaconTable.COLUMN_MINOR + "=" + beacon.getMinor(), null);
        close();
    }

    public List<Device> getAllBeacons() {
        List<Device> beacons = new ArrayList<Device>();
        open();

        Cursor cursor = database.query(BeaconTable.TABLE_BEACON, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Device beacon = cursorToBeacon(cursor);
            beacons.add(beacon);
            cursor.moveToNext();
        }

        cursor.close();
        close();
        return beacons;
    }

    private Device cursorToBeacon(Cursor cursor) {
        Device beacon = new Device();
        beacon.setMajor(cursor.getInt(0));
        beacon.setMinor(cursor.getInt(1));
        String date = cursor.getString(2);
        try {
            beacon.setDate(new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(date));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return beacon;
    }
}