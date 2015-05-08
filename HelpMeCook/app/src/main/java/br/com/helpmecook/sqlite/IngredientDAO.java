package br.com.helpmecook.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.List;

import br.com.helpmecook.model.Ingredient;

/**
 * Created by mariana on 04/05/15.
 */
public class IngredientDAO {

    private SQLiteDatabase database;
    private IngredientOpenHelper dbHelper;

    public static final String TABLE_NAME = "ingrediente";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ICONPATH = "icon_path";


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

        return database.insert(TABLE_NAME, null, values);
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

    //essa funcao sera util para quando o aplicativo for iniciado pela primeira vez os ingredientes do servidor possam ser adicionados ao banco local
    public boolean insertListIngredients (List<Ingredient> listOfIngredients){
        for (Ingredient ingredient : listOfIngredients){
            if (insert(ingredient) < 0)  return false;
        }
        return true;
    }

}