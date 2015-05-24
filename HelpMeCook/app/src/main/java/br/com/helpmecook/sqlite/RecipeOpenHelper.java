package br.com.helpmecook.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.helpmecook.model.Recipe;

/**
 * Created by Thais Torres on 04/05/2015.
 */
public class RecipeOpenHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    private static final String RECIPE_TABLE_CREATE =
            "CREATE TABLE " + RecipeDAO.TABLE_NAME + " (" +
                    RecipeDAO.ID + " INTEGER PRIMARY KEY," +
                    RecipeDAO.NOME + " TEXT," +
                    RecipeDAO.TASTE + " REAL, " +
                    RecipeDAO.DIFFICULTY + " REAL, " +
                    RecipeDAO.INGREDIENT_LIST + " TEXT, " +
                    RecipeDAO.TEXT + " TEXT, " +
                    RecipeDAO.ESTIMATED_TIME + " INTEGER, " +
                    RecipeDAO.PORTION_NUM + " TEXT, " +
                    RecipeDAO.PICTURE + " Blob, " +
                    RecipeDAO.SYNC + " INTEGER);";

    public RecipeOpenHelper(Context context) {
        super(context, RecipeDAO.TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RECIPE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipeDAO.TABLE_NAME);
        onCreate(db);
    }

}
