package br.com.helpmecook.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mariana on 04/05/15.
 */
public class IngredientOpenHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String INGREDIENT_TABLE_CREATE =
            "CREATE TABLE " + IngredientDAO.TABLE_NAME + " (" +
                    IngredientDAO.ID + " INTEGER PRIMARY KEY, " +
                    IngredientDAO.NAME + " TEXT, " +
                    IngredientDAO.ICONPATH + " INTEGER);";


    public IngredientOpenHelper(Context context) {
        super(context, IngredientDAO.TABLE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(INGREDIENT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + IngredientDAO.TABLE_NAME);
        onCreate(db);
    }
}
