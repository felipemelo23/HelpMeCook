package br.com.helpmecook.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.helpmecook.model.Ingredient;

/**
 * Created by Mariana on 04/05/15.
 */
public class IngredientDAO {

    private SQLiteDatabase database;
    private IngredientOpenHelper dbHelper;

    public static final String TABLE_NAME = "ingrediente";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ICONPATH = "icon_path";

    private String[] allColumns = { ID, NAME, ICONPATH };

    public IngredientDAO(Context context) {
        dbHelper = new IngredientOpenHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        database.close();
        database = null;
    }

    //As proximas funcoes estarao disponiveis para acesso dos usuarios comuns
    // elas so serao usadas para mudancas nos ingredientes no banco de dados geral da aplicacao

    public long insert(Ingredient ingredient) {
        ContentValues values = new ContentValues();
        values.put(ID, ingredient.getId());
        values.put(NAME, ingredient.getName());
        values.put(ICONPATH, ingredient.getIconPath());

        try {
            long result = database.insert(TABLE_NAME, null, values);
            return result;
        } catch (SQLiteConstraintException e) {

        }
        return -1;
    }

    public long update(Ingredient ingredient) {
        ContentValues values = new ContentValues();
        values.put(ID, ingredient.getId());
        values.put(NAME, ingredient.getName());
        values.put(ICONPATH, ingredient.getIconPath());

        return database.update(TABLE_NAME, values, ID + " = '" + ingredient.getId() + "'", null);
    }

    public boolean delete(Ingredient ingredient) {
        long wReturn = database.delete(TABLE_NAME, ID + " = '" + ingredient.getId() + "'", null);
        if(wReturn == 0) {
            return false;
        } else {
            return true;
        }
    }

    public Ingredient read(long id) {
        Ingredient ingredient;
        Cursor c = database.query(TABLE_NAME, allColumns, ID + " ='" + id + "'", null, null, null, null);

        if(c.moveToFirst()) {
            int indexId = c.getColumnIndex(ID);
            int indexName = c.getColumnIndex(NAME);
            int indexIconPath = c.getColumnIndex(ICONPATH);

            ingredient = new Ingredient();
            ingredient.setId(c.getLong(indexId));
            ingredient.setName(c.getString(indexName));
            ingredient.setIconPath(c.getInt(indexIconPath));

            c.close();

            return ingredient;
        } else {
            return null;
        }
    }

    public List<Ingredient> readAll() {
        Ingredient ingredient;
        List<Ingredient> ingredients;

        Cursor c = database.query(TABLE_NAME, allColumns, null, null, null, null, null);

        if(c.moveToFirst()) {
            ingredients = new ArrayList<Ingredient>();

            int indexId = c.getColumnIndex(ID);
            int indexName = c.getColumnIndex(NAME);
            int indexIconPath = c.getColumnIndex(ICONPATH);

            do {
                ingredient = new Ingredient();
                ingredient.setId(c.getLong(indexId));
                ingredient.setName(c.getString(indexName));
                ingredient.setIconPath(c.getInt(indexIconPath));

                ingredients.add(ingredient);
            } while (c.moveToNext());

            c.close();
            return ingredients;
        } else {
            return null;
        }
    }

    //essa funcao sera util para quando o aplicativo for iniciado pela primeira vez os ingredientes do
    //servidor possam ser adicionados ao banco local
    public boolean insertListIngredients (List<Ingredient> listOfIngredients){
        for (Ingredient ingredient : listOfIngredients){
            if (insert(ingredient) < 0)  return false;
        }
        return true;
    }

}
