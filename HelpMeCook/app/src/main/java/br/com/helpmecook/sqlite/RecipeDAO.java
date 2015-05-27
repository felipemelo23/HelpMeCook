package br.com.helpmecook.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Recipe;

public class RecipeDAO {

    private SQLiteDatabase database;
    private RecipeOpenHelper dbHelper;

    public static final String TABLE_NAME = "recipe";
    public static final String ID = "id";
    public static final String NOME = "nome";
    public static final String TASTE = "taste";
    public static final String DIFFICULTY = "difficulty";
    public static final String INGREDIENT_LIST = "ingredientList";
    public static final String INGREDIENT_QUANT = "ingredientQuant";
    public static final String INGREDIENT_UNITS = "ingredientUnits";
    public static final String TEXT = "text";
    public static final String ESTIMATED_TIME = "estimatedTime";
    public static final String PORTION_NUM = "portionNum";
    public static final String PICTURE = "picture";
    public static final String SYNC = "sync";

    private String[] allColumns = { ID, NOME, TASTE, DIFFICULTY, INGREDIENT_LIST, INGREDIENT_QUANT,
            INGREDIENT_UNITS, TEXT, ESTIMATED_TIME, PORTION_NUM, PICTURE, SYNC };

    public RecipeDAO(Context context){
        dbHelper = new RecipeOpenHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        database.close();
        database = null;
    }

    public long insert(Recipe recipe){
        ContentValues values = new ContentValues();
        values.put(ID, recipe.getId());
        values.put(NOME, recipe.getName());
        values.put(TASTE, recipe.getTaste());
        values.put(DIFFICULTY, recipe.getDifficulty());
        values.put(INGREDIENT_LIST, idListToString(recipe));
        values.put(INGREDIENT_QUANT, qntListToString(recipe));
        values.put(INGREDIENT_UNITS, unitsListToString(recipe));
        values.put(TEXT, recipe.getText());
        values.put(ESTIMATED_TIME, recipe.getEstimatedTime());
        values.put(PORTION_NUM, recipe.getPortionNum());
        // transformando a foto de bitmap para byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        recipe.getPicture().compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] picture = baos.toByteArray();
        values.put(PICTURE, picture);
        // checando se já foi sincronizada com o servidor
        if (recipe.isSync()) {
            values.put(SYNC, 1);
        } else {
            values.put(SYNC, 0);
        }

        try {
            long result = database.insert(TABLE_NAME, null, values);
            Log.i("DebugRecipeDAO", "Insert: " + result);
            return result;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
        return -1;

    }

    public long update(Recipe recipe){
        ContentValues values = new ContentValues();
        values.put(ID, recipe.getId());
        values.put(NOME, recipe.getName());
        values.put(TASTE, recipe.getTaste());
        values.put(DIFFICULTY, recipe.getDifficulty());
        values.put(INGREDIENT_LIST, idListToString(recipe));
        values.put(INGREDIENT_QUANT, qntListToString(recipe));
        values.put(INGREDIENT_UNITS, unitsListToString(recipe));
        values.put(TEXT, recipe.getText());
        values.put(ESTIMATED_TIME, recipe.getEstimatedTime());
        values.put(PORTION_NUM, recipe.getPortionNum());
        // transformando a foto de bitmap para byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        recipe.getPicture().compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] picture = baos.toByteArray();
        values.put(PICTURE, picture);
        // checando se já foi sincronizada com o servidor
        if (recipe.isSync()) {
            values.put(SYNC, 1);
        } else {
            values.put(SYNC, 0);
        }

        return database.update(TABLE_NAME, values, ID + " = '" + recipe.getId() + "'", null);
    }

    public boolean dalete(long id){
        long deleted = database.delete(TABLE_NAME, ID + " = '" + id + "'", null);

        if (deleted == 0) {
            return false;
        } else {
            return true;
        }

    }

    public Recipe read(long id){
        Recipe recipe;
        Cursor c = database.query(TABLE_NAME, allColumns, ID + " ='" + id + "'", null, null, null, null);
        Log.i("DebugCookbookManagerDAO", "Tamanho da Tabela " + c.getCount());

        if(c.moveToFirst()) {
            int indexId = c.getColumnIndex(ID);
            int indexName = c.getColumnIndex(NOME);
            int indexTaste = c.getColumnIndex(TASTE);
            int indexDifficulty = c.getColumnIndex(DIFFICULTY);
            int indexIngredientList = c.getColumnIndex(INGREDIENT_LIST);
            int indexQuantityList = c.getColumnIndex(INGREDIENT_QUANT);
            int indexUnitsList = c.getColumnIndex(INGREDIENT_UNITS);
            int indexText = c.getColumnIndex(TEXT);
            int indexEstimatedTime = c.getColumnIndex(ESTIMATED_TIME);
            int indexPortionNum = c.getColumnIndex(PORTION_NUM);
            int indexPicture = c.getColumnIndex(PICTURE);
            int indexSync = c.getColumnIndex(SYNC);

            recipe = new Recipe();
            recipe.setId(c.getLong(indexId));
            recipe.setName(c.getString(indexName));
            recipe.setTaste(c.getFloat(indexTaste));
            recipe.setDifficulty(c.getFloat(indexDifficulty));
            recipe.setIngredientList(stringToIdList(c.getString(indexIngredientList)));
            recipe.setNumberOfIng(stringToQntList(c.getString(indexQuantityList)));
            recipe.setUnits(stringToUnitsList(c.getString(indexUnitsList)));
            recipe.setText(c.getString(indexText));
            recipe.setEstimatedTime(c.getInt(indexEstimatedTime));
            recipe.setPortionNum(c.getString(indexPortionNum));
            recipe.setPicture(BitmapFactory.decodeStream(new ByteArrayInputStream(c.getBlob(indexPicture))));
            if (c.getInt(indexSync) == 0) {
                recipe.setSync(true);
            }
            if (c.getInt(indexSync) == 1) {
                recipe.setSync(false);
            }

            c.close();

            return recipe;
        } else {
            Log.i("DebugCookbookManagerDAO", "Exceção");
            return null;
        }

    }

    public AbstractRecipe readAbstractRecipe(long id){
        AbstractRecipe recipe;
        Cursor c = database.query(TABLE_NAME, allColumns, ID + " ='" + id + "'", null, null, null, null);
        Log.i("DebugCookbookManagerDAO", "Tamanho da Tabela " + c.getCount());

        if(c.moveToFirst()) {
            int indexId = c.getColumnIndex(ID);
            int indexName = c.getColumnIndex(NOME);
            int indexTaste = c.getColumnIndex(TASTE);
            int indexDifficulty = c.getColumnIndex(DIFFICULTY);
            int indexPicture = c.getColumnIndex(PICTURE);

            recipe = new Recipe();
            recipe.setId(c.getLong(indexId));
            recipe.setName(c.getString(indexName));
            recipe.setTaste(c.getFloat(indexTaste));
            recipe.setDifficulty(c.getFloat(indexDifficulty));
            recipe.setPicture(BitmapFactory.decodeStream(new ByteArrayInputStream(c.getBlob(indexPicture))));
            c.close();

            return recipe;
        } else {
            Log.i("DebugCookbookManagerDAO", "Exceção");
            return null;
        }
    }

    public List<Recipe> readAll() {
        Recipe recipe;
        List<Recipe> recipes = new ArrayList();

        Cursor c = database.query(TABLE_NAME, allColumns, null, null, null, null, null);

        if(c.moveToFirst()) {
            recipes = new ArrayList<>();

            int indexId = c.getColumnIndex(ID);
            int indexNome = c.getColumnIndex(NOME);
            int indexTaste = c.getColumnIndex(TASTE);
            int indexDifficulty = c.getColumnIndex(DIFFICULTY);
            int indexIngredientList = c.getColumnIndex(INGREDIENT_LIST);
            int indexQuantityList = c.getColumnIndex(INGREDIENT_QUANT);
            int indexUnitsList = c.getColumnIndex(INGREDIENT_UNITS);
            int indexText = c.getColumnIndex(TEXT);
            int indexEstimatedTime = c.getColumnIndex(ESTIMATED_TIME);
            int indexPortionNum = c.getColumnIndex(PORTION_NUM);
            int indexPicture = c.getColumnIndex(PICTURE);
            int indexSync = c.getColumnIndex(SYNC);

            do {
                recipe = new Recipe();
                recipe.setId(c.getLong(indexId));
                recipe.setName(c.getString(indexNome));
                recipe.setTaste(c.getFloat(indexTaste));
                recipe.setDifficulty(c.getFloat(indexDifficulty));
                recipe.setIngredientList(stringToIdList(c.getString(indexIngredientList)));
                recipe.setNumberOfIng(stringToQntList(c.getString(indexQuantityList)));
                recipe.setUnits(stringToUnitsList(c.getString(indexUnitsList)));
                recipe.setText(c.getString(indexText));
                recipe.setEstimatedTime(c.getInt(indexEstimatedTime));
                recipe.setPicture(BitmapFactory.decodeStream(new ByteArrayInputStream(c.getBlob(indexPicture))));
                recipe.setPortionNum(c.getString(indexPortionNum));
                if (c.getInt(indexSync) == 0) {
                    recipe.setSync(true);
                }
                if (c.getInt(indexSync) == 1) {
                    recipe.setSync(false);
                }

                recipes.add(recipe);
            } while (c.moveToNext());
        }
            c.close();
            return recipes;
    }

    public List<Recipe> readNotSync() {
        Recipe recipe;
        ArrayList<Recipe> recipes = new ArrayList();

        Cursor c = database.query(TABLE_NAME, allColumns, null, null, null, null, null);

        if(c.moveToFirst()) {
            recipes = new ArrayList<>();

            int indexId = c.getColumnIndex(ID);
            int indexNome = c.getColumnIndex(NOME);
            int indexTaste = c.getColumnIndex(TASTE);
            int indexDifficulty = c.getColumnIndex(DIFFICULTY);
            int indexIngredientList = c.getColumnIndex(INGREDIENT_LIST);
            int indexQuantityList = c.getColumnIndex(INGREDIENT_QUANT);
            int indexUnitsList = c.getColumnIndex(INGREDIENT_UNITS);
            int indexText = c.getColumnIndex(TEXT);
            int indexEstimatedTime = c.getColumnIndex(ESTIMATED_TIME);
            int indexPortionNum = c.getColumnIndex(PORTION_NUM);
            int indexPicture = c.getColumnIndex(PICTURE);
            int indexSync = c.getColumnIndex(SYNC);


            do {
                if (c.getInt(indexSync) == 1) {
                    recipe = new Recipe();
                    recipe.setId(c.getLong(indexId));
                    recipe.setName(c.getString(indexNome));
                    recipe.setTaste(c.getFloat(indexTaste));
                    recipe.setDifficulty(c.getFloat(indexDifficulty));
                    recipe.setIngredientList(stringToIdList(c.getString(indexIngredientList)));
                    recipe.setNumberOfIng(stringToQntList(c.getString(indexQuantityList)));
                    recipe.setUnits(stringToUnitsList(c.getString(indexUnitsList)));
                    recipe.setText(c.getString(indexText));
                    recipe.setEstimatedTime(c.getInt(indexEstimatedTime));
                    recipe.setPortionNum(c.getString(indexPortionNum));
                    recipe.setPicture(BitmapFactory.decodeStream(new ByteArrayInputStream(c.getBlob(indexPicture))));
                    recipe.setSync(false);

                    recipes.add(recipe);
                }
            } while (c.moveToNext());
        }
        c.close();
        return recipes;
    }

    private String idListToString(Recipe recipe){
        String idList = "";
        for (long ingId : recipe.getIngredientList()) {
            idList = idList + " " + ingId;
            Log.i("Debug Ingredient 1", "" + ingId);

        }
        return idList;
    }

    private String qntListToString(Recipe recipe) {
        String idQnt = "";
        for (int qnt : recipe.getNumberOfIng()) {
            idQnt += " " + qnt;
        }
        return idQnt;
    }

    private String unitsListToString(Recipe recipe) {
        String idUnits = "";
        for (String unit : recipe.getUnits()) {
            idUnits += "$" + unit;
        }
        return idUnits;
    }

    private List stringToIdList(String idList) {
        List<Long> ingredientList = new ArrayList<Long>();
        StringTokenizer st = new StringTokenizer(idList);
        while (st.hasMoreTokens()) {
            ingredientList.add(Long.parseLong(st.nextToken()));
        }
        Log.i("Debug Ingredient 2", "" );

        return ingredientList;
    }

    private List stringToQntList(String qntList) {
        List<Integer> quantityList = new ArrayList<Integer>();
        StringTokenizer st = new StringTokenizer(qntList);
        while (st.hasMoreTokens()) {
            quantityList.add(Integer.parseInt(st.nextToken()));
        }
        return quantityList;
    }

    private List stringToUnitsList(String untList) {
        List<String> unitsList = new ArrayList<String>();
        String[] st = untList.split("$");

        for (int i = 0; i < st.length; i++) {
            unitsList.add(st[i]);
        }

        return unitsList;
    }

}