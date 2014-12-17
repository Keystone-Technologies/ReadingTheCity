package keystone_technologies.com.readingthecity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailsDataSource {

    private SQLiteDatabase database;
    private DetailsTable dbDetailsTable;
    private String[] allColumns = { DetailsTable.COLUMN_ID, DetailsTable.COLUMN_FETCHING,
            DetailsTable.COLUMN_AGE, DetailsTable.COLUMN_PARENT, DetailsTable.COLUMN_DETAILS,
            DetailsTable.COLUMN_RESPONSE};

    public DetailsDataSource(Context context) {
        dbDetailsTable = new DetailsTable(context);
    }

    public void open() throws SQLException {
        database = dbDetailsTable.getWritableDatabase();
    }

    public void close() {
        dbDetailsTable.close();
    }

    public void createDetail(String id, Date date, String detail) {
        open();
        ContentValues values = new ContentValues();
        values.put(DetailsTable.COLUMN_ID, id);
        values.put(DetailsTable.COLUMN_FETCHING, convertDateToString(date));
        values.put(DetailsTable.COLUMN_AGE, convertDateToString(date));
        values.put(DetailsTable.COLUMN_DETAILS, detail);

        database.insert(DetailsTable.TABLE_DETAILS, null, values);
        close();
    }

    public void createDetail(String id, Date date, String parent, String detail) {
        open();
        ContentValues values = new ContentValues();
        values.put(DetailsTable.COLUMN_ID, id);
        values.put(DetailsTable.COLUMN_FETCHING, convertDateToString(date));
        values.put(DetailsTable.COLUMN_AGE, convertDateToString(date));
        values.put(DetailsTable.COLUMN_PARENT, parent);
        values.put(DetailsTable.COLUMN_DETAILS, detail);

        database.insert(DetailsTable.TABLE_DETAILS, null, values);
        close();
    }

    private String convertDateToString(Date date) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return df.format(date);
    }

    public boolean isDetailInDB(String id) {
        List<Details> detailsList = getAllDetails();

        for (Details d : detailsList) {
            if (d.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDetailAgeExpired(String id) {
        List<Details> detailsList = getAllDetails();

        for (Details d : detailsList) {
            if (d.getId().equals(id)) {
                if (isSameDay(d.getAge())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean notCurrentlyFetchingDetail(String id) {
        List<Details> detailsList = getAllDetails();

        for (Details d : detailsList) {
            if (d.getId().equals(id)) {
                if (isTimestampExpired(d.getFetching())) {
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

    public Details getChildDetailFromId(String id) {
        Details detail = null;
        List<Details> detailsList = getAllDetails();
        try {
            for (Details d : detailsList) {
                JSONObject jsonObject = d.getDetail();
                if (!jsonObject.isNull("parent")) {
                    if (id.equals(jsonObject.getString("parent"))) {
                        detail = d;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return detail;
    }

    public void setYesResponse(String id) {
        open();

        ContentValues values = new ContentValues();
        values.put(DetailsTable.COLUMN_RESPONSE, 1);

        String where = DetailsTable.COLUMN_ID + "=?";
        String whereArgs[] = new String[] {id};

        try {
            database.update(DetailsTable.TABLE_DETAILS, values, where, whereArgs);
        } catch (SQLException e) {
            Log.e("Error", e.toString());
        }

        database.close();
    }

    public void setNoResponse(String id) {
        open();

        ContentValues values = new ContentValues();
        values.put(DetailsTable.COLUMN_RESPONSE, 0);

        String where = DetailsTable.COLUMN_ID + "=?";
        String whereArgs[] = new String[] {id};

        try {
            database.update(DetailsTable.TABLE_DETAILS, values, where, whereArgs);
        } catch (SQLException e) {
            Log.e("Error", e.toString());
        }

        database.close();
    }

    public void deleteDetail(String id) {
        open();
        database.delete(DetailsTable.TABLE_DETAILS, DetailsTable.COLUMN_ID + "='" + id + "'", null);
        close();
    }

    public List<Details> getAllDetails() {
        List<Details> details = new ArrayList<Details>();
        open();

        Cursor cursor = database.query(DetailsTable.TABLE_DETAILS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Details detail = cursorToDetail(cursor);
            details.add(detail);
            cursor.moveToNext();
        }

        cursor.close();
        close();
        return details;
    }

    private Details cursorToDetail(Cursor cursor) {
        Details detail = new Details();
        detail.setId(cursor.getString(0));
        detail.setFetching(stringToDate(cursor.getString(1)));
        detail.setAge(stringToDate(cursor.getString(2)));
        detail.setParent(cursor.getString(3));
        try {
            detail.setDetail(new JSONObject(cursor.getString(4)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        detail.setResponse(cursor.getInt(5));
        return detail;
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