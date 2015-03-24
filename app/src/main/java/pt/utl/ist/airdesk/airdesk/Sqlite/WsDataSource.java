package pt.utl.ist.airdesk.airdesk.Sqlite;

/**
 * Created by diogofrazao on 24/03/15.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WSDataSource {
//sdas
    // Database fields
    //ss
    //sdas
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID , MySQLiteHelper.COLUMN_NAMEWS,
            MySQLiteHelper.COLUMN_STORAGE, MySQLiteHelper.COLUMN_PATH };

    public WSDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public WorkspaceRepresentation createWorkspaceRepresentation(String nameWs, String storage, String path) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAMEWS, nameWs);
        values.put(MySQLiteHelper.COLUMN_STORAGE, storage);
        values.put(MySQLiteHelper.COLUMN_PATH, path);

        long insertId = database.insert(MySQLiteHelper.TABLE_WS, null,
                values);
        Log.v("conadamae","create1");
        Cursor cursor = database.query(MySQLiteHelper.TABLE_WS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        Log.v("conadamae","create1.5");
        cursor.moveToFirst();
        WorkspaceRepresentation newComment = cursorToComment(cursor);
        cursor.close();
        Log.v("conadamae","create2");
        return newComment;
    }

    public void deleteComment(WorkspaceRepresentation comment) {
        long id = comment.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_WS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<WorkspaceRepresentation> getAllComments() {
        List<WorkspaceRepresentation> comments = new ArrayList<WorkspaceRepresentation>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_WS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WorkspaceRepresentation workspaceRepresentation = cursorToComment(cursor);
            comments.add(workspaceRepresentation);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private WorkspaceRepresentation cursorToComment(Cursor cursor) {
        Log.v("conadamae","cursor1");
        WorkspaceRepresentation comment = new WorkspaceRepresentation();
        Log.v("conadamae","cursor2");
        comment.setId(cursor.getLong(0));
        comment.setNameWs(cursor.getString(1));
        Log.v("conadamae","cursor3");
        comment.setStorage(cursor.getString(2));
        Log.v("conadamae","cursor4");
        comment.setPath(cursor.getString(3));
        Log.v("conadamae","cursor4");
        return comment;
    }
}
