package br.com.helpmecook.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.helpmecook.model.Cookbook;

/**
 * Created by Felipe on 04/05/2015.
 */
public class CookbookOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    private static final String COOKBOOK_TABLE_CREATE =
            "CREATE TABLE " + CookbookDAO.TABLE_NAME + " (" + CookbookDAO.ID + " INTERGER PRIMARY KEY);";

    public CookbookOpenHelper(Context context) {
        super(context, CookbookDAO.TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(COOKBOOK_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CookbookDAO.TABLE_NAME);
        onCreate(db);
    }
}
