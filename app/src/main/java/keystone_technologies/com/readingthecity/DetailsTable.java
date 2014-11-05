package keystone_technologies.com.readingthecity;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DetailsTable extends SQLiteOpenHelper {

    public static final String TABLE_DETAILS = "detailsTable";
    public static final String COLUMN_DETAILS = "details";

    private static final String DATABASE_NAME = "dataDetails.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + TABLE_DETAILS + " ( " +
            COLUMN_DETAILS + " TEXT PRIMARY KEY );";

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