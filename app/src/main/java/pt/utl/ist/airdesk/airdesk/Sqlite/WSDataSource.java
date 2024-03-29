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
            MySQLiteHelper.COLUMN_STORAGE, MySQLiteHelper.COLUMN_PATH, MySQLiteHelper.COLUMN_OWNER};

    public WSDataSource(Context context) {

        dbHelper = new MySQLiteHelper(context);

    }

    public void open() throws SQLException {

        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public WorkspaceRepresentation createWorkspaceRepresentation(String nameWs, int storage, String path, String owner) {


        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAMEWS, nameWs);
        values.put(MySQLiteHelper.COLUMN_STORAGE, storage);
        values.put(MySQLiteHelper.COLUMN_PATH, path);
        values.put(MySQLiteHelper.COLUMN_OWNER, owner);

        long insertId = database.insert(MySQLiteHelper.TABLE_WS, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_WS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);

        cursor.moveToFirst();
        WorkspaceRepresentation newComment = cursorToComment(cursor);
        cursor.close();

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


    public ArrayList<String> GetAllValues(String login){
        String aTable = "ws";
        String aColumn[] = {"nameWs"};

        ArrayList<String> list = new ArrayList<String>();
//ss

        Cursor cursor = database.rawQuery("SELECT nameWs FROM ws WHERE owner='"+login+"'", null);


        while(cursor.moveToNext()){
            list.add(cursor.getString(0));

        }




        return list;
    }

    /*
    public void getId(String login){
        String aTable = "ws";
        String aColumn[] = {"nameWs"};


        Cursor cursor = database.rawQuery("SELECT id FROM ws WHERE users='"+login+"'", null);




    }
*/
    public String getOwner(String workspace){
        String aTable = "ws";
        String aColumn[] = {"nameWs"};

        String owner = "lol";


        Cursor cursor = database.rawQuery("SELECT owner FROM ws WHERE nameWs='"+workspace+"'", null);


        while(cursor.moveToNext()){
           owner = cursor.getString(0);

        }

       return owner;

    }

    public void deleteWorkspaceEntry(String workspace){



        database.delete(MySQLiteHelper.TABLE_WS, MySQLiteHelper.COLUMN_NAMEWS
                + "=" + "'"+workspace+"'", null);


    }

    public String workSpaceOnTable(String workspace){
        String aTable = "ws";
        String aColumn[] = {"nameWs"};

        String ws = null;

        Cursor cursor = database.rawQuery("SELECT nameWs FROM ws WHERE nameWs='"+workspace+"'", null);

        while(cursor.moveToNext()){
            ws = cursor.getString(0);

        }

        return ws;

    }

    /*
    public void updateUser(String user, String workspace){
        String strSQL = "UPDATE ws SET users ='"+user+"'"+ "WHERE nameWs='"+workspace+"'";

        database.execSQL(strSQL);


    }*/

    public int getWorkspaceStorage(String workspace){

        Cursor st = database.rawQuery("SELECT storage FROM ws WHERE nameWs='"+workspace+"'", null);
        int stInt;
        String storage=null;

        while(st.moveToNext()){
            storage = st.getString(0);
        }

        stInt= Integer.parseInt(storage);

        return stInt;
    }

    /*
    public String getPermission(String workspace, String user){
        String aTable = "ws";
        String aColumn[] = {"nameWs"};

        String permission = "lol";


        Cursor cursor = database.rawQuery("SELECT permission FROM ws WHERE nameWs='"+workspace+"'", null);


        while(cursor.moveToNext()){
            permission = cursor.getString(0);

        }

        return permission;

    }*/




    public void resetDatabase(){
        dbHelper.onUpgrade(database,1,2);
    }

    private WorkspaceRepresentation cursorToComment(Cursor cursor) {
        WorkspaceRepresentation comment = new WorkspaceRepresentation();
        comment.setId(cursor.getLong(0));
        comment.setNameWs(cursor.getString(1));
        comment.setStorage(cursor.getString(2));
        comment.setPath(cursor.getString(3));
        comment.setPath(cursor.getString(4));
        return comment;
    }
}
