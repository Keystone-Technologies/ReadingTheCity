package keystone_technologies.com.readingthecity;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ServiceTable extends SQLiteOpenHelper {

    public static final String TABLE_SERVICE = "service";
    public static final String COLUMN_MAJOR = "major";
    public static final String COLUMN_MINOR = "minor";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PARENT = "parent";
    public static final String COLUMN_RESPOMSE = "response";

    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + TABLE_SERVICE + " ( " +
            COLUMN_ID + " TEXT PRIMARY KEY, " + COLUMN_MAJOR + " INTEGER, "
            + COLUMN_MINOR + " INTEGER, " + COLUMN_DATE + " TEXT, "
            + COLUMN_PARENT + " TEXT, " + COLUMN_RESPOMSE + " INTEGER );";

    public ServiceTable(Context context) {
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICE);
        onCreate(db);
    }
}
