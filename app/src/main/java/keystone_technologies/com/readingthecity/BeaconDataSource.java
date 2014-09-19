package keystone_technologies.com.readingthecity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.util.Log;

import com.estimote.sdk.Beacon;

import java.util.ArrayList;
import java.util.List;

public class BeaconDataSource {

    private SQLiteDatabase database;
    private ServiceTable dbServiceTable;
    private String[] allColumns = { ServiceTable.COLUMN_BEACON, ServiceTable.COLUMN_RESPOMSE };

    public BeaconDataSource(Context context) {
        dbServiceTable = new ServiceTable(context);
    }

    public void open() throws SQLException {
        database = dbServiceTable.getWritableDatabase();
    }

    public void close() {
        dbServiceTable.close();
    }

    public void createBeacon(String UUID, int response) {
        open();
        ContentValues values = new ContentValues();
        values.put(ServiceTable.COLUMN_BEACON, UUID);
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

//    public int getBeaconResponse(String uuid) {
//        open();
//
//        int response = 0;
//
//        String[] result_column = new String[] {ServiceTable.COLUMN_BEACON, ServiceTable.COLUMN_RESPOMSE};
//        String where = ServiceTable.COLUMN_BEACON + "=" + uuid;
//
//        String whereArgs[] = null;
//        String groupBy = null;
//        String having = null;
//        String order = null;
//
//        Cursor cursor = database.query(ServiceTable.TABLE_SERVICE, result_column, where, whereArgs, groupBy, having, order);
//        int RESPONSE_INDEX = cursor.getColumnIndexOrThrow(ServiceTable.COLUMN_RESPOMSE);
//        while (cursor.moveToNext()) {
//            response = cursor.getInt(RESPONSE_INDEX);
//        }
//        cursor.close();
//        return response;
//    }

    public void setYesResponse(String uuid) {
        open();

        ContentValues values = new ContentValues();

        //if (getBeaconResponse(uuid) == 0) {
            values.put(ServiceTable.COLUMN_RESPOMSE, 1);
        //} else {
        //    values.put(ServiceTable.COLUMN_RESPOMSE, 0);
       // }

        String where = ServiceTable.COLUMN_BEACON + "=?";
        String whereArgs[] = new String[] {uuid};

        try {
            database.update(ServiceTable.TABLE_SERVICE, values, where, whereArgs);
        } catch (SQLException e) {
            Log.e("Error", e.toString());
        }

        database.close();
    }

    public void setNoResponse(String uuid) {
        open();

        ContentValues values = new ContentValues();

        //if (getBeaconResponse(uuid) == 0) {
        values.put(ServiceTable.COLUMN_RESPOMSE, 0);
        //} else {
        //    values.put(ServiceTable.COLUMN_RESPOMSE, 0);
        // }

        String where = ServiceTable.COLUMN_BEACON + "=?";
        String whereArgs[] = new String[] {uuid};

        try {
            database.update(ServiceTable.TABLE_SERVICE, values, where, whereArgs);
        } catch (SQLException e) {
            Log.e("Error", e.toString());
        }
        database.close();
    }

    public void deleteBeacon(BeaconDevice beacon) {
        String UUID = beacon.getUUID();
        database.delete(ServiceTable.TABLE_SERVICE, ServiceTable.COLUMN_BEACON + " = " + UUID, null);
    }

    public BeaconDevice getBeaconByUUID(String UUID) {
        List<BeaconDevice> beacons = getAllBeacons();
        for (BeaconDevice b : beacons) {
            if (UUID.equals(b.getUUID())) {
                return b;
            }
        }
        return null;
    }

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
        beacon.setUUID(cursor.getString(0));
        beacon.setResponse(cursor.getInt(1));
        return beacon;
    }
}
