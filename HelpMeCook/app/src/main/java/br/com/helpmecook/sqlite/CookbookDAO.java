package br.com.helpmecook.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.helpmecook.model.Cookbook;

/**
 * Created by Felipe on 04/05/2015.
 */
public class CookbookDAO {
    private SQLiteDatabase database;
    private CookbookOpenHelper dbHelper;

    public static final String TABLE_NAME = "cookbook";
    public static final String ID = "id";

    private String[] allColumns = {ID};

    public CookbookDAO(Context context) {
        dbHelper = new CookbookOpenHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        database.close();
        database = null;
    }

    public long insert(int id) {
        ContentValues values = new ContentValues();
        values.put(ID,id);
        return database.insert(TABLE_NAME, null, values);
    }

    public boolean delete(int id) {
        long wReturn = database.delete(TABLE_NAME, ID + " = '" + id + "'", null);

        if (wReturn == 0) {
            return false;
        } else {
            return true;
        }
    }

    public List<Integer> readAll() {
        int id;
        ArrayList<Integer> recipes = new ArrayList<Integer>();

        Cursor c = database.query(TABLE_NAME, allColumns, null, null, null, null, null);

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
