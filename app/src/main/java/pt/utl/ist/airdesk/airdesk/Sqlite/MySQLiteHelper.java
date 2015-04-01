package pt.utl.ist.airdesk.airdesk.Sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cmov.db";
    private static final int DATABASE_VERSION = 1;


    public static final String TABLE_WS = "ws";
    public static final String COLUMN_NAMEWS = "nameWs";
    public static final String COLUMN_STORAGE = "storage";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_ID = "id";


    public static final String TABLE_USERS = "users";
    public static final String COLUMN_NAME = "name";


    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_WS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NAMEWS
            + " text not null, " + COLUMN_STORAGE
            + " text not null, " + COLUMN_PATH
            + " text not null);";


    private static final String DATABASE_CREATE2 = "create table "
            + TABLE_USERS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NAME
            + " text not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);
    }

}
