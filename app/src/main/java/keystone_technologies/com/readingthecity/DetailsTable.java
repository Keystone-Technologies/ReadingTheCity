package keystone_technologies.com.readingthecity;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DetailsTable extends SQLiteOpenHelper {

    public static final String TABLE_DETAILS = "detailsTable";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FETCHING = "fetching";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_PARENT = "parent";
    public static final String COLUMN_DETAILS = "details";
    public static final String COLUMN_RESPONSE = "response";

    private static final String DATABASE_NAME = "dataDetails.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + TABLE_DETAILS + " ( " +
            COLUMN_ID + " TEXT PRIMARY KEY, " + COLUMN_FETCHING + " TEXT, "
            + COLUMN_AGE + " TEXT, " + COLUMN_PARENT + " TEXT, " + COLUMN_DETAILS + " TEXT, "
            + COLUMN_RESPONSE + " INTEGER );";

    public DetailsTable(Context context) {
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAILS);
        onCreate(db);
    }
}