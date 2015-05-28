package br.com.helpmecook.connection;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.model.Recipe;

/**
 * Created by Kandarpa on 27/05/2015.
 */
public class UploadRecipe extends UrlConnection {
    protected long serverID;

    public UploadRecipe(Recipe recipe){
        this.UrlStringWS.append("UploadRecipe.php?name=" + recipe.getName());
        this.UrlStringWS.append("&taste=" + recipe.getTaste());
        this.UrlStringWS.append("&diffculty=" + recipe.getDifficulty());
        //Encoding the picture to string
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            recipe.getPicture().compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        this.UrlStringWS.append("&picture=" + encodedImage);
        //Binding the ingredients ID????? (Should be NAME to work at the moment might be ignored)
        List<Long> ingredients = recipe.getIngredientList();
        /*StringBuffer ingredientsBuffer = new StringBuffer();
        for (int i = 0; i<ingredients.size(); i++){
            ingredientsBuffer.append(ingredients.get(i));
            if (i < (ingredients.size()-1)){
                ingredientsBuffer.append(",");
            }
        }
        this.UrlStringWS.append("&ingredients=" + ingredientsBuffer);*/
        this.UrlStringWS.append("&numberofing=" + recipe.getNumberOfIng());
        //Nao sei para que isso serve
        //this.UrlStringWS.append("&units=" + recipe.getUnits());
        this.UrlStringWS.append("&text=" + recipe.getText());
        this.UrlStringWS.append("&estimatedtime=" + recipe.getEstimatedTime());
        this.UrlStringWS.append("&portionnum=" + recipe.getPortionNum());
    }

    protected String doInBackground(String... params) {
        try {

            // instantiate our json parser
            JsonParser jParser = new JsonParser();

            // get json string from url
            JSONObject json = jParser.getJSONFromUrl(UrlStringWS);

            // get the array of users
            dataJsonArr = json.getJSONArray("searchresult");
            Log.i("LOADING", dataJsonArr.toString());

            for (int i = 0; i < dataJsonArr.length(); i++) {
                JSONObject c = dataJsonArr.getJSONObject(i);
                serverID = c.getLong("id");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @return Retorna o ID inserido criado no Servidor
     */
    public long getNewId(){
        return this.serverID;
    }
}