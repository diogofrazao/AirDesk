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
            MySQLiteHelper.COLUMN_STORAGE, MySQLiteHelper.COLUMN_PATH, MySQLiteHelper.COLUMN_OWNER, MySQLiteHelper.COLUMN_USERS, MySQLiteHelper.COLUMN_PERMISSION };

    public WSDataSource(Context context) {

        dbHelper = new MySQLiteHelper(context);

    }

    public void open() throws SQLException {

        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public WorkspaceRepresentation createWorkspaceRepresentation(String nameWs, int storage, String path, String owner, String users, String permission) {


        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAMEWS, nameWs);
        values.put(MySQLiteHelper.COLUMN_STORAGE, storage);
        values.put(MySQLiteHelper.COLUMN_PATH, path);
        values.put(MySQLiteHelper.COLUMN_OWNER, owner);
        values.put(MySQLiteHelper.COLUMN_USERS, users);
        values.put(MySQLiteHelper.COLUMN_PERMISSION, permission);

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

        Cursor cursor = database.rawQuery("SELECT nameWs FROM ws WHERE users='"+login+"'", null);


        while(cursor.moveToNext()){
            Log.d("ficheiros:", cursor.getString(0));
            list.add(cursor.getString(0));

        }




        return list;
    }

    public void getId(String login){
        String aTable = "ws";
        String aColumn[] = {"nameWs"};


        Cursor cursor = database.rawQuery("SELECT id FROM ws WHERE users='"+login+"'", null);


        while(cursor.moveToNext()){
            Log.d("ficheiros:", cursor.getString(0));

        }


    }

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

        //database.rawQuery("DELETE FROM ws WHERE nameWs='"+workspace+"'", null);

        //if(!(cursor==null)){
        //    return true;
        //}
        //else{return false;}
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

    public void updateUser(String user, String workspace){
        String strSQL = "UPDATE ws SET users ='"+user+"'"+ "WHERE nameWs='"+workspace+"'";

        database.execSQL(strSQL);


    }

    public int getWorkspaceStorage(String workspace){

        Cursor st = database.rawQuery("SELECT storage FROM ws WHERE nameWs='"+workspace+"'", null);
        int stInt;
        String storage=null;

        while(st.moveToNext()){
            storage = st.getString(0);
        }

        stInt= Integer.parseInt(storage);
        Log.d("tamanho",stInt+"");

        return stInt;
    }

    public String getPermission(String workspace, String user){
        String aTable = "ws";
        String aColumn[] = {"nameWs"};

        String permission = "lol";


        Cursor cursor = database.rawQuery("SELECT permission FROM ws WHERE nameWs='"+workspace+"'", null);


        while(cursor.moveToNext()){
            permission = cursor.getString(0);

        }

        return permission;

    }




    public void resetDatabase(){
        dbHelper.onUpgrade(database,1,2);
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
        comment.setPath(cursor.getString(4));
        comment.setUsers(cursor.getString(4));
        comment.setPermission(cursor.getString(5));
        return comment;
    }
}
