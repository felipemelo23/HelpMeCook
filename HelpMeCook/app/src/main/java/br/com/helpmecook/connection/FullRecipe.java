package br.com.helpmecook.connection;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.helpmecook.model.Recipe;

/**
 * Created by Kandarpa on 27/05/2015.
 */
public class FullRecipe extends UrlConnection {
    /**
     * @param id Numero long que identifica uma receita.
     */
    public FullRecipe(long id){
        this.UrlStringWS.append("FullRecipe.php?id=" + id);
    }

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
                //recipe.setPicture(c.getString("picture"));

                //recipe.setsIngredients(c.getString("ingredients"));
                //recipe.setNumberOfIng(c.getInt("numberofing"));
                //recipe.setUnits(c.getString("units"));
                recipe.setText(c.getString("text"));
                recipe.setEstimatedTime(c.getInt("estimatedtime"));
                recipe.setPortionNum(c.getString("portionnum"));

                ResultRecipes.add(recipe);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}