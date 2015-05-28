package br.com.helpmecook.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.helpmecook.model.Recipe;

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

    public long insert(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(ID, recipe.getId());
        values.put(LAST_ACCESS, recipe.getLastAcess().getTimeInMillis());

        return database.insert(RecentsDAO.TABLE_NAME, null, values);
    }

    public long update(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(ID, recipe.getId());
        values.put(LAST_ACCESS, recipe.getLastAcess().getTimeInMillis());

        return database.update(RecentsDAO.TABLE_NAME, values, ID + " = " + recipe.getId(), null);
    }

    public boolean delete(long id, Calendar lastAccess) {
        long wReturn = database.delete(TABLE_NAME, ID + " = " + id, null);

        if (wReturn == 0) {
            return false;
        } else {
            return true;
        }
    }

    public Calendar read(long id) {
        Cursor c = database.query(TABLE_NAME, allColumns, ID + " = '" + id + "'", null, null, null, null);
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

    public List<Long> readAll() {
        Cursor c = database.query(TABLE_NAME, allColumns, null, null, null, null, LAST_ACCESS + " DESC");
        long id;
        ArrayList<Long> recipes = new ArrayList<Long>();

        Log.i("RecentsDAO", String.valueOf(c.getCount()));

        if (c.moveToFirst()) {
            int indexId = c.getColumnIndex(ID);

            do {
                id = c.getLong(indexId);
                Log.i("RecentsDAO", String.valueOf(id));
                recipes.add(id);
            } while(c.moveToNext());
        }

        c.close();

        return recipes;
    }
}
