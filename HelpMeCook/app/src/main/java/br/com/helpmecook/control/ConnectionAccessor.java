package br.com.helpmecook.control;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import br.com.helpmecook.connection.JsonParser;
import br.com.helpmecook.connection.UploadRecipe;
import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.model.Recipe;

/**
 * Created by Felipe on 30/04/2015.
 */
public class ConnectionAccessor {
    JsonParser jsonParser = new JsonParser();

    /**
     * @param id Numero inteiro que identifica uma receita.
     * @return Retorna a receita relativa ao id passado como parametro.
     */
    public Recipe getRecipeById(long id) {
        Recipe tempRecipe = new Recipe();

        Log.i("RecipeID",id+"");

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("param", id+""));
            JSONArray jsonArray = jsonParser.makeHttpRequest("http://helpmecook.com.br/ws/FullRecipe.php?param=" + id, params, "GET");
            Log.i("RespostaFullRecipe", jsonArray.toString());

            JSONObject jsonObject = jsonArray.getJSONObject(0);
            if (jsonObject.has("id")) {
                tempRecipe.setId(jsonObject.optLong("id"));
                tempRecipe.setName(jsonObject.optString("name"));
                tempRecipe.setTaste((float) jsonObject.optDouble("taste"));
                tempRecipe.setDifficulty((float) jsonObject.optDouble("difficulty"));
                tempRecipe.setPictureToString(jsonObject.optString("picture"));

                JSONArray ingJSONArray = jsonObject.getJSONArray("ingredientList");
                List<Long> ingredientList = new ArrayList<Long>();
                for (int j = 0; j < ingJSONArray.length(); j++){
                    ingredientList.add(ingJSONArray.getLong(j));
                }
                tempRecipe.setIngredientList(ingredientList);

                JSONArray unitsJSONArray = jsonObject.getJSONArray("units");
                List<String> units = new ArrayList<String>();
                for (int j = 0; j < unitsJSONArray.length(); j++) {
                    units.add(unitsJSONArray.getString(j));
                }
                tempRecipe.setUnits(units);

                tempRecipe.setText(jsonObject.getString("text"));
                if (jsonObject.has("estimatedTime")) {
                    tempRecipe.setEstimatedTime(jsonObject.getInt("estimatedTime"));
                }
                if (jsonObject.has("portionNum")) {
                    tempRecipe.setPortionNum(jsonObject.getString("portionNum"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (HttpHostConnectException e) {
            e.printStackTrace();
            return null;
        }
        return tempRecipe;
    }

    /**
     * @param wanted Lista de ingredientes desejaveis.
     * @param unwanted Lista de ingredientes indesejaveis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem exatamente a busca
     * por ingredientes, ou seja, contem receitas com exatamente os ingredientes desejados,
     * dadas as duas listas de ingredientes passadas como parametro.
     */

    public Pair<List<AbstractRecipe>,List<AbstractRecipe>> getResultByIngredientLists(
            List<Ingredient> wanted, List<Ingredient> unwanted) {

        List<AbstractRecipe> result = new ArrayList<AbstractRecipe>();
        List<AbstractRecipe> plus = new ArrayList<AbstractRecipe>();

        try {
            String url = "http://helpmecook.com.br/ws/IngredientSearch.php";

            String in = "[";
            for (int i = 0; i < wanted.size(); i++) {
                in += "\"" + wanted.get(i).getId() + "\"";
                if (i != wanted.size() - 1) in += ",";
            }
            in += "]";

            String out = "[";
            for (int i = 0; i < unwanted.size(); i++) {
                out += "\"" + unwanted.get(i).getId() + "\"";
                if (i != unwanted.size() - 1) out += ",";
            }
            out += "]";

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("in", in));
            if (unwanted.size() > 0) params.add(new BasicNameValuePair("out", out));

            JSONArray jsonArray = jsonParser.makeHttpRequest(url, params, "GET");

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Recipe tempRecipe = new Recipe();
                    tempRecipe.setId(jsonObject.optLong("id"));
                    tempRecipe.setName(jsonObject.optString("name"));
                    tempRecipe.setTaste((float) jsonObject.optDouble("taste"));
                    tempRecipe.setDifficulty((float) jsonObject.optDouble("difficulty"));
                    tempRecipe.setPictureToString(jsonObject.optString("picture"));

                    JSONArray ingJSONArray = jsonObject.getJSONArray("ingredientList");
                    List<Long> ingredientList = new ArrayList<Long>();
                    for (int j = 0; j < ingJSONArray.length(); j++) {
                        ingredientList.add(ingJSONArray.getLong(j));
                    }
                    tempRecipe.setIngredientList(ingredientList);

                    JSONArray unitsJSONArray = jsonObject.getJSONArray("units");
                    List<String> units = new ArrayList<String>();
                    for (int j = 0; j < unitsJSONArray.length(); j++) {
                        units.add(unitsJSONArray.getString(j));
                    }
                    tempRecipe.setUnits(units);

                    tempRecipe.setText(jsonObject.getString("text"));

                    if (jsonObject.has("estimatedTime")) {
                        tempRecipe.setEstimatedTime(jsonObject.getInt("estimatedTime"));
                    }
                    if (jsonObject.has("portionNum")) {
                        tempRecipe.setPortionNum(jsonObject.getString("portionNum"));
                    }

                    if (tempRecipe.getIngredientNum() <= wanted.size()) {
                        result.add(tempRecipe);
                    } else {
                        plus.add(tempRecipe);
                    }
                    Log.i("HomeFragment", tempRecipe.getId() + " " + tempRecipe.getName());
                }
            }
        } catch (HttpHostConnectException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Pair<List<AbstractRecipe>,List<AbstractRecipe>>(result,plus);
    }

    /**
     * @param name Nome ou parte do nome de uma receita.
     * @return Retorna uma lista de identificadores de receitas que possuem a String name passada como parametro.
     * no seu nome.
     */
    public List<AbstractRecipe> getResultByRecipeName(String name) {
        List<AbstractRecipe> results = new ArrayList<AbstractRecipe>();

        try {
            String url = "http://helpmecook.com.br/ws/NameSearch.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("param", name));

            JSONArray jsonArray = jsonParser.makeHttpRequest(url, params, "GET");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Recipe tempRecipe = new Recipe();
                tempRecipe.setId(jsonObject.optLong("id"));
                tempRecipe.setName(jsonObject.optString("name"));
                tempRecipe.setTaste((float) jsonObject.optDouble("taste"));
                tempRecipe.setDifficulty((float) jsonObject.optDouble("difficulty"));
                tempRecipe.setPictureToString(jsonObject.optString("picture"));

                JSONArray ingJSONArray = jsonObject.getJSONArray("ingredientList");
                List<Long> ingredientList = new ArrayList<Long>();
                for (int j = 0; j < ingJSONArray.length(); j++){
                    ingredientList.add(ingJSONArray.getLong(j));
                }
                tempRecipe.setIngredientList(ingredientList);

                JSONArray unitsJSONArray = jsonObject.getJSONArray("units");
                List<String> units = new ArrayList<String>();
                for (int j = 0; j < unitsJSONArray.length(); j++) {
                    units.add(unitsJSONArray.getString(j));
                }
                tempRecipe.setUnits(units);

                tempRecipe.setText(jsonObject.getString("text"));
                if (jsonObject.has("estimatedTime")) {
                    tempRecipe.setEstimatedTime(jsonObject.getInt("estimatedTime"));
                }
                if (jsonObject.has("portionNum")) {
                    tempRecipe.setPortionNum(jsonObject.getString("portionNum"));
                }

                results.add(tempRecipe);

                Log.i("HomeFragment", tempRecipe.getId() + " " + tempRecipe.getName());
            }
        } catch (HttpHostConnectException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;
    }

    /**
     * @return Retorna as receitas mais populares, ou seja, mais visualizadas pelo usuarios.
     */
    public List<AbstractRecipe> getPopularRecipes() throws HttpHostConnectException {
        List<AbstractRecipe> temp = new ArrayList<AbstractRecipe>();
        try {
            JSONArray jsonArray = jsonParser.makeHttpRequest("http://helpmecook.com.br/ws/MostVisualised.php",
                    new ArrayList<NameValuePair>(), "POST");

            Log.i("JSONArrayPopularSize", jsonArray.length()+"");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Recipe tempRecipe = new Recipe();
                tempRecipe.setId(jsonObject.optLong("id"));
                tempRecipe.setName(jsonObject.optString("name"));
                tempRecipe.setTaste((float) jsonObject.optDouble("taste"));
                tempRecipe.setDifficulty((float) jsonObject.optDouble("difficulty"));
                tempRecipe.setPictureToString(jsonObject.optString("picture"));

                JSONArray ingJSONArray = jsonObject.getJSONArray("ingredientList");
                List<Long> ingredientList = new ArrayList<Long>();
                for (int j = 0; j < ingJSONArray.length(); j++){
                    ingredientList.add(ingJSONArray.getLong(j));
                }
                tempRecipe.setIngredientList(ingredientList);

                JSONArray unitsJSONArray = jsonObject.getJSONArray("units");
                List<String> units = new ArrayList<String>();
                for (int j = 0; j < unitsJSONArray.length(); j++) {
                    units.add(unitsJSONArray.getString(j));
                }
                tempRecipe.setUnits(units);

                tempRecipe.setText(jsonObject.getString("text"));

                if (jsonObject.has("estimatedTime")) {
                    tempRecipe.setEstimatedTime(jsonObject.getInt("estimatedTime"));
                }
                if (jsonObject.has("portionNum")) {
                    tempRecipe.setPortionNum(jsonObject.getString("portionNum"));
                }

                temp.add(tempRecipe);
                Log.i("HomeFragment", tempRecipe.getId() + " " + tempRecipe.getName());
            }

        } catch (HttpHostConnectException e) {
            throw e;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return temp;
    }

    /**
     * @param recipe Receita a ser registrada no Banco de Dados no Servidor
     * @return Retorna true se a receita for registrada no banco de dados no servidor e retorna false
     * se a receita nao for registrada no banco de dados no servidor,
     * mas for registrada no Banco de dados local.
     */
    public long registerRecipe(Recipe recipe) {
        Recipe temp = new Recipe();

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            List<NameValuePair> paramsImage = new ArrayList<NameValuePair>();

            String ingrendientList = "[";
            for (int i = 0; i < recipe.getIngredientNum(); i++) {
                ingrendientList += "\"" + recipe.getIngredientList().get(i) + "\"";
                if (i != recipe.getIngredientNum() - 1) ingrendientList += ",";
            }
            ingrendientList += "]";

            String units = "[";
            for (int i = 0; i < recipe.getUnits().size(); i++) {
                units += "\"" + recipe.getUnits().get(i) + "\"";
                if (i != recipe.getUnits().size() - 1) units += ",";
            }
            units += "]";

            params.add(new BasicNameValuePair("name", recipe.getName()));
            params.add(new BasicNameValuePair("text",recipe.getText()));
            if (recipe.getPortionNum() != null && recipe.getPortionNum() != "") {
                params.add(new BasicNameValuePair("portion",recipe.getPortionNum()));
            }
            if (recipe.getEstimatedTime() > 0) {
                params.add(new BasicNameValuePair("time",recipe.getEstimatedTime()+""));
            }
            params.add(new BasicNameValuePair("ingredientList", ingrendientList));
            params.add(new BasicNameValuePair("units", units));

            Log.i("URL", "http://helpmecook.com.br/ws/UploadRecipe.php");

            JSONArray jsonArray = jsonParser.makeHttpRequest("http://helpmecook.com.br/ws/UploadRecipe.php",params,"GET");

            temp.setId(jsonArray.getLong(0));

            paramsImage.add(new BasicNameValuePair("image", recipe.getPictureToString()));
            Log.i("StringPicture", recipe.getPictureToString());
            paramsImage.add(new BasicNameValuePair("id", temp.getId() + ""));
            jsonParser.makeHttpRequest("http://helpmecook.com.br/ws/upload_image.php", paramsImage, "POST");

        } catch (HttpHostConnectException e) {

            return -1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("RegisterRecipeID", temp.getId()+"");
        return temp.getId();
    }

    /**
     * @param id Identificador de uma receita.
     * @param taste Valor de classificação de sabor de uma receita.
     * @return Retorna true se a classificação da receita for atualizada no banco de dados do servidor
     * e retorna false se a classificação da receita não for atualizada no banco de dados do servidor.
     */
    public boolean classifyTaste(long id, float taste) {
        Log.i("CLASSIFY taste", "id= "+id+" taste= "+taste);
        try {
            /*String url = "http://helpmecook.com.br/ws/Classify.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id",id+""));
            params.add(new BasicNameValuePair("rate", taste+""));
            params.add(new BasicNameValuePair("flag", 0 + ""));
            JSONArray jsonArray = jsonParser.makeHttpRequest(url, params, "POST");*/

            String url = "http://helpmecook.com.br/ws/Classify.php?id="+id+"&rate="+taste+"&flag=0";
            JSONArray jsonArray = jsonParser.makeHttpRequest(url,
                    new ArrayList<NameValuePair>(), "POST");

            Log.i("ClassifyTaste", taste + " " + jsonArray.getDouble(0));

        } catch (HttpHostConnectException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @param id Identificador de uma receita.
     * @param difficulty Valor de classificação de dificuldade de uma receita.
     * @return Retorna true se a classificação da receita for atualizada no banco de dados do servidor
     * e retorna false se a classificação da receita não for atualizada no banco de dados do servidor.
     */
    public boolean classifyDifficulty(long id, float difficulty) {
        Log.i("CLASSIFY taste", "id= "+id+" difficulty= "+difficulty);
        try {
            /*String url = "http://helpmecook.com.br/ws/Classify.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id",id+""));
            params.add(new BasicNameValuePair("rate", difficulty+""));
            params.add(new BasicNameValuePair("flag", 1 + ""));

            JSONArray jsonArray = jsonParser.makeHttpRequest(url, params, "POST");*/

            String url = "http://helpmecook.com.br/ws/Classify.php?id="+id+"&rate="+difficulty+"&flag=1";
            JSONArray jsonArray = jsonParser.makeHttpRequest(url,
                    new ArrayList<NameValuePair>(), "POST");

            Log.i("ClassifyDifficult",difficulty + " " + jsonArray.getDouble(0));

        } catch (HttpHostConnectException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @param recipes Lista com todas as receitas do usuário
     * @return Retorna a lista de receitas com as receitas de entrada que foram modificadas no servidor.
     */
    public List<Recipe> syncRecipes(List<Recipe> recipes) {
        return null;
    }
}