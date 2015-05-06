package br.com.helpmecook.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Felipe on 04/05/2015.
 */
public class RecentsDAO {
    private SQLiteDatabase database;
    private RecentsOpenHelper dbHelper;

    public static final String TABLE_NAME = "recents";
    public static final String ID = "id";
    public static final String LAST_ACCESS = "last_access";

    private String[] allColumns = {ID, LAST_ACCESS};

    public RecentsDAO(Context context) {
        dbHelper = new RecentsOpenHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        database.close();
        database = null;
    }

    public long insert(int id, Calendar lastAccess) {
        ContentValues values = new ContentValues();
        values.put(ID,id);
        values.put(LAST_ACCESS,lastAccess.getTimeInMillis());

        return database.insert(RecentsDAO.TABLE_NAME, null, values);
    }

    public long update(int id, Calendar lastAccess) {
        ContentValues values = new ContentValues();
        values.put(ID,id);
        values.put(LAST_ACCESS,lastAccess.getTimeInMillis());

        return database.update(RecentsDAO.TABLE_NAME, values, ID + " = " + id, null);
    }

    public boolean delete(int id, Calendar lastAccess) {
        long wReturn = database.delete(TABLE_NAME, ID + " = " + id, null);

        if (wReturn == 0) {
            return false;
        } else {
            return true;
        }
    }

    public Calendar read(int id) {
        Cursor c = database.query(TABLE_NAME, allColumns, null, null, null, null, null);
        Calendar calendar;

        if (c.moveToFirst()) {
            int indexLastAccess = c.getColumnIndex(LAST_ACCESS);

            calendar = new GregorianCalendar();
            calendar.setTimeInMillis(c.getLong(indexLastAccess));
            c.close();
            return calendar;
        } else {
            c.close();
            return null;
        }
    }

    public List<Integer> readAll() {
        Cursor c = database.query(TABLE_NAME, allColumns, null, null, null, null, LAST_ACCESS + " DESC");
        int id;
        ArrayList<Integer> recipes = new ArrayList<Integer>();

        if (c.moveToFirst()) {
            int indexId = c.getColumnIndex(ID);

            do {
                id = c.getInt(indexId);
                recipes.add(id);
            } while(c.moveToNext());
        }

        c.close();

        return recipes;
    }
}
