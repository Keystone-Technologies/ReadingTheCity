package keystone_technologies.com.readingthecity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ServiceTable extends SQLiteOpenHelper {

    public static final String TABLE_SERVICE = "service";
    public static final String COLUMN_BEACON = "_id";
    public static final String COLUMN_RESPOMSE = "response";

    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + TABLE_SERVICE + "(" +
            COLUMN_BEACON + " text not null primary key," + COLUMN_RESPOMSE + " integer not null);";

    public ServiceTable(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICE);
        onCreate(db);
    }
}
