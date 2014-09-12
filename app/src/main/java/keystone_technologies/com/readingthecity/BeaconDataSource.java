package keystone_technologies.com.readingthecity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;

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
        //open();
        ContentValues values = new ContentValues();
        values.put(ServiceTable.COLUMN_BEACON, UUID);
        values.put(ServiceTable.COLUMN_RESPOMSE, response);

        database.insert(ServiceTable.TABLE_SERVICE, null, values);
        //close();

//        Cursor cursor = database.query(ServiceTable.TABLE_SERVICE, allColumns, ServiceTable.COLUMN_BEACON +
//                " = " + UUID, null, null, null, null);
//        cursor.moveToFirst();
//        BeaconDevice newBeacon = cursorToBeacon(cursor);
//        cursor.close();
//        return newBeacon;
    }

    public void deleteBeacon(BeaconDevice beacon) {
        String UUID = beacon.getUUID();
        database.delete(ServiceTable.TABLE_SERVICE, ServiceTable.COLUMN_BEACON + " = " + UUID, null);
    }

    public List<BeaconDevice> getAllBeacons() {
        List<BeaconDevice> beacons = new ArrayList<BeaconDevice>();
        //open();

        Cursor cursor = database.query(ServiceTable.TABLE_SERVICE, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            BeaconDevice beacon = cursorToBeacon(cursor);
            beacons.add(beacon);
            cursor.moveToNext();
        }

        cursor.close();
       // close();
        return beacons;
    }

    private BeaconDevice cursorToBeacon(Cursor cursor) {
        BeaconDevice beacon = new BeaconDevice();
        beacon.setUUID(cursor.getString(0));
        beacon.setResponse(cursor.getInt(1));
        return beacon;
    }
}
