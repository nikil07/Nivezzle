package com.nikhil.volley.dbutils;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "POINTS_DB";
    public static final String TABLE_NAME = "POINTS_TABLE";
    public static final int VERSION = 1;
    public static final String KEY_ID = "_id";
    public static final String points = "POINTS";
   // public static final String LNAME = "L_NAME";
    public static final String SCRIPT = "create table "
            + TABLE_NAME + " ("
            + KEY_ID
            + " integer primary key autoincrement, "
            + points
            + " text not null );";

    public DBHelper(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
