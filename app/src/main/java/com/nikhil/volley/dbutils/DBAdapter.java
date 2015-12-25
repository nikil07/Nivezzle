package com.nikhil.volley.dbutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
    SQLiteDatabase database_ob;
    DBHelper openHelper_ob;
    Context context;

    public DBAdapter(Context c)
    {
        context = c;
    }

    public DBAdapter opnToRead() {
        openHelper_ob = new DBHelper(context,
                openHelper_ob.DATABASE_NAME, null, openHelper_ob.VERSION);
        database_ob = openHelper_ob.getReadableDatabase();
        return this;
    }

    public DBAdapter opnToWrite() {
        openHelper_ob = new DBHelper(context,
                openHelper_ob.DATABASE_NAME, null, openHelper_ob.VERSION);
        database_ob = openHelper_ob.getWritableDatabase();
        return this;
    }

    public void Close() {
        database_ob.close();
    }

    public long insertDetails(String points) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(openHelper_ob.points, points);
        opnToWrite();
        long val = database_ob.insert(openHelper_ob.TABLE_NAME, null,
                contentValues);
        Close();
        return val;
    }

    public Cursor querySevenEntries()
    {
        String[] cols = {openHelper_ob.KEY_ID, openHelper_ob.points,
        };
        opnToWrite();
        Cursor c = database_ob.query(false,openHelper_ob.TABLE_NAME,cols,null,null,null,null,cols[0]+ " DESC", "7");

        c.moveToLast();
        return c;
    }
    public Cursor queryName() {
        String[] cols = {openHelper_ob.KEY_ID, openHelper_ob.points,
        };
        opnToWrite();
        Cursor c = database_ob.query(openHelper_ob.TABLE_NAME, cols, null,
                null, null, null, cols[0]+ " DESC");

        c.moveToLast();
        return c;
    }

    public Cursor queryAll(int nameId) {
        String[] cols = { openHelper_ob.KEY_ID, openHelper_ob.points,
                };
        opnToWrite();
        Cursor c = database_ob.query(openHelper_ob.TABLE_NAME, cols,
                openHelper_ob.KEY_ID + "=" + nameId, null, null, null, null);

        return c;
    }

    public long updateDetail(int rowId, String interval) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(openHelper_ob.points, interval);
        opnToWrite();
        long val = database_ob.update(openHelper_ob.TABLE_NAME, contentValues,
                openHelper_ob.KEY_ID + "=" + rowId, null);
        Close();
        return val;
    }

    public int deleteOneRecord(int rowId) {
        opnToWrite();
        int val = database_ob.delete(openHelper_ob.TABLE_NAME,
                openHelper_ob.KEY_ID + "=" + rowId, null);
        Close();
        return val;
    }
}
