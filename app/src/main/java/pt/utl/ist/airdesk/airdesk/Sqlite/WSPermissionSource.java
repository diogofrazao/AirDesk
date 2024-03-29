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
 * Created by duarte on 5/9/15.
 */
public class WSPermissionSource {
    // Database fields
    //ss
    //sdas
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID , MySQLiteHelper.COLUMN_WS,
            MySQLiteHelper.COLUMN_USER, MySQLiteHelper.COLUMN_RIGHTS};


    public WSPermissionSource(Context context) {

        dbHelper = new MySQLiteHelper(context);

    }

    public void open() throws SQLException {

        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public WSUsersPermission createPermissionsRepresentation(String nameWs,String user, String rights) {


        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_WS, nameWs);
        values.put(MySQLiteHelper.COLUMN_USER, user);
        values.put(MySQLiteHelper.COLUMN_RIGHTS, rights);


        long insertId = database.insert(MySQLiteHelper.TABLE_RIGHTS, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_RIGHTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);

        cursor.moveToFirst();
        WSUsersPermission newComment = cursorToComment(cursor);
        cursor.close();

        return newComment;
    }

    public void deleteComment(WorkspaceRepresentation comment) {
        long id = comment.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_RIGHTS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<WSUsersPermission> getAllComments() {
        List<WSUsersPermission> comments = new ArrayList<WSUsersPermission>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_WS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            WSUsersPermission workspaceRepresentation = cursorToComment(cursor);
            comments.add(workspaceRepresentation);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }


    public ArrayList<String> GetAllWSByUser(String user){

        ArrayList<String> list = new ArrayList<String>();

        Cursor cursor = database.rawQuery("SELECT colWs FROM tabRights WHERE colUser='"+user+"'", null);

        while(cursor.moveToNext()){
            list.add(cursor.getString(0));
        }

        return list;
    }

    public void deleteWorkspaceEntry(String workspace){

        database.delete(MySQLiteHelper.TABLE_WS, MySQLiteHelper.COLUMN_NAMEWS
                + "=" + "'"+workspace+"'", null);

    }
/*
    public String workSpaceOnTable(String workspace){
        String aTable = "t";
        String aColumn[] = {"nameWs"};

        String ws = null;

        Cursor cursor = database.rawQuery("SELECT nameWs FROM ws WHERE nameWs='"+workspace+"'", null);

        while(cursor.moveToNext()){
            ws = cursor.getString(0);

        }

        return ws;

    }

    */


    public void updatePermission(String user, String workspace,String permission){
        String strSQL = "UPDATE tabRights SET colRights='"+permission+"'WHERE colWs='"+workspace+"'" + "AND colUser='"+user+"'";

        database.execSQL(strSQL);


    }





    public String getPermission(String workspace, String user){
        String aTable = "ws";
        String aColumn[] = {"nameWs"};

        Log.v("conadamae", "O worspace recebido para a query foi: "+workspace);
        Log.v("conadamae","O user recebido para a query foi: "+user);
        String permission = "Not_Found";


        Cursor cursor = database.rawQuery("SELECT colRights FROM tabRights WHERE colWs='"+workspace+"'" + "AND colUser='"+user+"'", null);


        while(cursor.moveToNext()){
            permission = cursor.getString(0);

        }

        return permission;

    }




    public void resetDatabase(){
        dbHelper.onUpgrade(database,1,2);
    }

    private WSUsersPermission cursorToComment(Cursor cursor) {
        WSUsersPermission comment = new WSUsersPermission();

        comment.setId(cursor.getLong(0));

        comment.setWorkspaceName(cursor.getString(1));
        comment.setUser(cursor.getString(2));
        comment.setRights(cursor.getString(3));


        return comment;
    }
}

