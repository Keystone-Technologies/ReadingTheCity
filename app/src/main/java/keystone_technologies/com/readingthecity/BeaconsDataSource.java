package keystone_technologies.com.readingthecity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BeaconsDataSource {

    private SQLiteDatabase database;
    private BeaconsTable dbBeaconsTable;
    private String[] allColumns = { BeaconsTable.COLUMN_MAJOR, BeaconsTable.COLUMN_MINOR,
            BeaconsTable.COLUMN_NOTIFICATION_ID, BeaconsTable.COLUMN_FETCHING,
            BeaconsTable.COLUMN_AGE, BeaconsTable.COLUMN_PARENT, BeaconsTable.COLUMN_ID};

    public BeaconsDataSource(Context context) {
        dbBeaconsTable = new BeaconsTable(context);
    }

    public void open() throws SQLException {
        database = dbBeaconsTable.getWritableDatabase();
    }

    public void close() {
        dbBeaconsTable.close();
    }

    public void createBeacon(int major, int minor, int notificationId, Date date) {
        open();

        ContentValues values = new ContentValues();
        values.put(BeaconsTable.COLUMN_MAJOR, String.valueOf(major));
        values.put(BeaconsTable.COLUMN_MINOR, String.valueOf(minor));
        values.put(BeaconsTable.COLUMN_NOTIFICATION_ID, String.valueOf(notificationId));
        values.put(BeaconsTable.COLUMN_AGE, convertDateToString(date));
        values.put(BeaconsTable.COLUMN_FETCHING, convertDateToString(date));

        database.insert(BeaconsTable.TABLE_BEACONS, null, values);
        close();
    }

    private String convertDateToString(Date date) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return df.format(date);
    }

    public BeaconDevice getBeacon(int notificationId) {
        List<BeaconDevice> beaconList = getAllBeacons();
        BeaconDevice beacon = null;

        for (BeaconDevice bd : beaconList) {
            if (notificationId == bd.getNotificationId()) {
                beacon = bd;
            }
        }

        return beacon;
    }

    public boolean isBeaconInDB(int notificationId) {
        List<BeaconDevice> beacons = getAllBeacons();

        for (BeaconDevice bd : beacons) {
            if (bd.getNotificationId() == notificationId) {
                return true;
            }
        }
        return false;
    }

    public boolean isBeaconAgeExpired(int notificationId) {
        List<BeaconDevice> beacons = getAllBeacons();

        for (BeaconDevice bd : beacons) {
            if (bd.getNotificationId() == notificationId) {
                if (isSameDay(bd.getAge())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean notCurrentlyFetchingBeacon(int notificationId) {
        List<BeaconDevice> beacons = getAllBeacons();

        for (BeaconDevice bd : beacons) {
            if (bd.getNotificationId() == notificationId) {
                if (isTimestampExpired(bd.getFetching())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isTimestampExpired(Date d) {
        long julianDayNumber1 = new Date().getTime() / Constants.MILLIS_PER_DAY;
        long julianDayNumber2 = d.getTime() / Constants.MILLIS_PER_DAY;

        long seconds = (julianDayNumber1 - julianDayNumber2)/1000;

        return seconds > 30 ? true : false;
    }

    private boolean isSameDay(Date d) {
        long julianDayNumber1 = new Date().getTime() / Constants.MILLIS_PER_DAY;
        long julianDayNumber2 = d.getTime() / Constants.MILLIS_PER_DAY;

        // If they now are equal then it is the same day.
        return julianDayNumber1 == julianDayNumber2;
    }

    public int getNotificationId(String id) {
        List<BeaconDevice> beacons = getAllBeacons();

        for (BeaconDevice bd : beacons) {
            if (bd.getParent().equals(id)) {
                return bd.getNotificationId();
            }
        }
        return 99;
    }

    public BeaconDevice getBeaconFromDB(int notificationId) {
        List<BeaconDevice> beacons = getAllBeacons();

        for (BeaconDevice bd : beacons) {
            if (bd.getNotificationId() == notificationId) {
                return bd;
            }
        }
        return null;
    }

    public void setBeaconParent(int major, int minor, String parent) {
        open();

        ContentValues values = new ContentValues();
        values.put(BeaconsTable.COLUMN_PARENT, parent);

        String where = BeaconsTable.COLUMN_NOTIFICATION_ID + "=?";
        String whereArgs[] = new String[] {String.valueOf(major) + String.valueOf(minor)};

        try {
            database.update(BeaconsTable.TABLE_BEACONS, values, where, whereArgs);
        } catch (SQLException e) {
            Log.e("Error", e.toString());
        }

        database.close();
    }

    public void setBeaconId(int major, int minor, String id) {
        open();

        ContentValues values = new ContentValues();
        values.put(BeaconsTable.COLUMN_ID, id);

        String where = BeaconsTable.COLUMN_NOTIFICATION_ID + "=?";
        String whereArgs[] = new String[] {String.valueOf(major) + String.valueOf(minor)};

        try {
            database.update(BeaconsTable.TABLE_BEACONS, values, where, whereArgs);
        } catch (SQLException e) {
            Log.e("Error", e.toString());
        }

        database.close();
    }

    public void deleteBeacon(int notificationId) {
        open();
        database.delete(BeaconsTable.TABLE_BEACONS, BeaconsTable.COLUMN_NOTIFICATION_ID + "='" + notificationId + "'", null);
        close();
    }

    public List<BeaconDevice> getAllBeacons() {
        List<BeaconDevice> beacons = new ArrayList<BeaconDevice>();
        open();

        Cursor cursor = database.query(BeaconsTable.TABLE_BEACONS, allColumns, null, null, null, null, null);

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
        beacon.setNotificationId(cursor.getInt(2));
        beacon.setFetching(stringToDate(cursor.getString(3)));
        beacon.setAge(stringToDate(cursor.getString(4)));
        beacon.setParent(cursor.getString(5));
        beacon.setId(cursor.getString(6));
        return beacon;
    }

    private Date stringToDate(String strDate) {
        Date date = null;
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        try {
            date = format.parse(strDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return date;
    }
}