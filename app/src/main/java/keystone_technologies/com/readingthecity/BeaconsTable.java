package keystone_technologies.com.readingthecity;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BeaconsTable extends SQLiteOpenHelper {

    public static final String TABLE_BEACONS = "beaconsTable";
    public static final String COLUMN_MAJOR = "major";
    public static final String COLUMN_MINOR = "minor";
    public static final String COLUMN_NOTIFICATION_ID = "notificationId";
    public static final String COLUMN_FETCHING = "fetching";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PARENT = "parent";

    private static final String DATABASE_NAME = "dataBeacons.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + TABLE_BEACONS + " ( " +
            COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY, " + COLUMN_MAJOR + " INTEGER, "
            + COLUMN_MINOR + " INTEGER, " + COLUMN_FETCHING + " TEXT, " + COLUMN_AGE + " TEXT, " +
            COLUMN_ID + " TEXT, " + COLUMN_PARENT + " TEXT );";

    public BeaconsTable(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            database.execSQL(DATABASE_CREATE);
        } catch (SQLException e) {
            Log.e("SQLERROR", e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEACONS);
        onCreate(db);
    }
}