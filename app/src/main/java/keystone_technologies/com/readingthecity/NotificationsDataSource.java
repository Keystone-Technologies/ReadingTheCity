package keystone_technologies.com.readingthecity;

import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationsDataSource {

    private SQLiteDatabase database;
    private NotificationsTable dbNotificationsTable;
    private String[] allColumns = { NotificationsTable.COLUMN_ID, NotificationsTable.COLUMN_NAME,
    NotificationsTable.COLUMN_RESPONSE};

    public NotificationsDataSource(Context context) {
        dbNotificationsTable = new NotificationsTable(context);
    }

    public void open() throws SQLException {
        database = dbNotificationsTable.getWritableDatabase();
    }

    public void close() {
        dbNotificationsTable.close();
    }

    public void createNotification(String id, String name, int response) {
        open();
        ContentValues values = new ContentValues();
        values.put(NotificationsTable.COLUMN_ID, id);
        values.put(NotificationsTable.COLUMN_NAME, name);
        values.put(NotificationsTable.COLUMN_RESPONSE, response);

        database.insert(NotificationsTable.TABLE_NOTIFICATIONS, null, values);
        close();
    }

    public Posts getPostsFromId(String id) {
        Posts post = null;

        List<Posts> postList = getAllPosts();
        for (Posts p : postList) {
            try {
                if (id.equals(p.getId())) {
                    post = new Posts(p.getId(), p.getName(), p.getResponse());
                    break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return post;
    }

    public int getNotificationResponse(String id) {
        int response = 99;
        List<Posts> postList = getAllPosts();

        for (Posts p : postList) {
            if (id.equals(p.getId())) {
                response = p.getResponse();
            }
        }
        return response;
    }

    public void setYesResponse(String id) {
        open();

        ContentValues values = new ContentValues();
        values.put(NotificationsTable.COLUMN_RESPONSE, 1);

        String where = NotificationsTable.COLUMN_ID + "=?";
        String whereArgs[] = new String[] {id};

        try {
            database.update(DetailsTable.TABLE_DETAILS, values, where, whereArgs);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        database.close();
    }

    public void setNoResponse(String id) {
        open();

        ContentValues values = new ContentValues();

        values.put(NotificationsTable.COLUMN_RESPONSE, 0);

        String where = NotificationsTable.COLUMN_ID + "=?";
        String whereArgs[] = new String[] {id};

        try {
            database.update(DetailsTable.TABLE_DETAILS, values, where, whereArgs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        database.close();
    }

    public boolean isPostInDB(String id) {
        List<Posts> postList = getAllPosts();
        boolean flag = false;

        for (Posts p : postList) {
            try {
                if (p.getId().equals(id)) {
                    flag = true;
                    break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return flag;
    }

    public void deletePost(String id) {
        open();
        database.delete(NotificationsTable.TABLE_NOTIFICATIONS, NotificationsTable.COLUMN_ID + "='" + id + "'", null);
        close();
    }

    public List<Posts> getAllPosts() {
        List<Posts> postList = new ArrayList<Posts>();
        open();

        Cursor cursor = database.query(NotificationsTable.TABLE_NOTIFICATIONS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Posts post = cursorToPost(cursor);
            postList.add(post);
            cursor.moveToNext();
        }

        cursor.close();
        close();
        return postList;
    }

    private Posts cursorToPost(Cursor cursor) {
        Posts post = new Posts();
        post.setId(cursor.getString(0));
        post.setName(cursor.getString(1));
        post.setResponse(cursor.getInt(2));
        return post;
    }
}