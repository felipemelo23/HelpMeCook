package br.com.helpmecook.sqlite;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Felipe on 04/05/2015.
 */
public class RecentsOpenHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String RECENTS_TABLE_CREATE =
            "CREATE TABLE " + RecentsDAO.TABLE_NAME + " (" +
            RecentsDAO.ID + " INTEGER PRIMARY KEY, " +
            RecentsDAO.LAST_ACCESS + " INTEGER);";

    public RecentsOpenHelper(Context context) {
        super(context, RecentsDAO.TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RECENTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecentsDAO.TABLE_NAME);
        onCreate(db);
    }
}
