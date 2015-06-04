package br.com.helpmecook.connection;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.helpmecook.model.Recipe;

/**
 * Created by Kandarpa on 27/05/2015.
 */
public class UrlConnection extends AsyncTask<String, String, String> {

    /*protected long id;
    protected String name;
    protected float taste;
    protected float difficulty;
    protected Bitmap picture;
    private List<Long> ingredientList;
    private List<Integer> numberOfIng;
    private List<String> units;
    private String text;
    private int estimatedTime;
    private String portionNum;
    private Calendar lastAcess;
    private boolean sync;*/


    ArrayList<Recipe> ResultRecipes = new ArrayList<Recipe>();
    final String TAG = "UrlConnection";
    StringBuffer UrlStringWS = new StringBuffer("http://helpmecook.com.br/ws/");

    /**
     * @return Retorna uma Lista de Recipe
     */
    public ArrayList<Recipe> getResult(){
        return this.ResultRecipes;
    }

    // JSONArray
    JSONArray dataJsonArr = null;

    @Override
    protected String doInBackground(String... params) {
        try {

            // instantiate our json parser
            JsonParser jParser = new JsonParser();

            // get json string from url
            //JSONObject json = jParser.getJSONFromUrl(UrlStringWS);

            // get the array of users
            //dataJsonArr = json.getJSONArray("searchresult");
            Log.i("LOADING", dataJsonArr.toString());

            // loop through all users
            for (int i = 0; i < dataJsonArr.length(); i++) {
                JSONObject c = dataJsonArr.getJSONObject(i);


                //Cria uma nova receita e carrega as informacoes
                Recipe recipe = new Recipe();
                recipe.setId(c.getLong("id"));
                recipe.setName(c.getString("name"));
                recipe.setTaste((float) c.getDouble("taste"));
                recipe.setDifficulty((float) c.getDouble("difficulty"));
                //recipe.setPicture(c.getJSONObject("picture"));

                /*recipe.setsIngredients(c.getString("ingredients"));
                recipe.setNumberOfIng(c.getInt("numberofing"));
                recipe.setUnits(c.getString("units"));
                recipe.setText(c.getString("text"));
                recipe.setEstimatedTime(c.getInt("estimatedtime"));
                recipe.setPortionNum(c.getString("portionnum"));*/

                ResultRecipes.add(recipe);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}