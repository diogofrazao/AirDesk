package pt.utl.ist.airdesk.airdesk.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diogofrazao on 01/04/15.
 */
public class UsersDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID , MySQLiteHelper.COLUMN_NAME};

    public UsersDataSource(Context context) {

        dbHelper = new MySQLiteHelper(context);

    }

    public void open() throws SQLException {

        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public UsersRepresentation createUsersRepresentation(String name) {


        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);

        long insertId = database.insert(MySQLiteHelper.TABLE_USERS, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);

        cursor.moveToFirst();
        UsersRepresentation newComment = cursorToComment(cursor);
        cursor.close();

        return newComment;
    }

    public void deleteComment(WorkspaceRepresentation comment) {
        long id = comment.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_WS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<UsersRepresentation> getAllComments() {
        List<UsersRepresentation> comments = new ArrayList<UsersRepresentation>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_USERS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            UsersRepresentation usersRepresentation = cursorToComment(cursor);
            comments.add(usersRepresentation);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }




    public void resetDatabase(){
        dbHelper.onUpgrade(database,1,2);
    }

    private UsersRepresentation cursorToComment(Cursor cursor) {

        UsersRepresentation comment = new UsersRepresentation();

        comment.setId(cursor.getLong(0));
        comment.setName(cursor.getString(1));

        return comment;
    }
}