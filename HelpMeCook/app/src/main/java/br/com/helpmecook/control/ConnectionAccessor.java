package br.com.helpmecook.control;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.helpmecook.connection.UploadRecipe;
import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.model.Recipe;

/**
 * Created by Felipe on 30/04/2015.
 */
public class ConnectionAccessor {
    Recipe recipe;
    long result;

    /**
     * @param id Numero inteiro que identifica uma receita.
     * @return Retorna a receita relativa ao id passado como parametro.
     */
    public Recipe getRecipeById(long id) {
        GetRecipeTask task = new GetRecipeTask(id);
        recipe = null;

        task.execute();

        return recipe;
    }

    /**
     * @param wanted Lista de ingredientes desejaveis.
     * @param unwanted Lista de ingredientes indesejaveis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem exatamente a busca
     * por ingredientes, ou seja, contem receitas com exatamente os ingredientes desejados,
     * dadas as duas listas de ingredientes passadas como parametro.
     */
    public List<AbstractRecipe> getResultByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return null;
    }

    /**
     * @param wanted Lista de ingredientes desejaveis.
     * @param unwanted Lista de ingredientes indesejaveis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem a busca
     * por ingredientes, mas tem 1 ingrediente a mais, ou seja, contem receitas com exatamente os ingredientes
     * desejados mais 1 ingrediente, dadas as duas listas de ingredientes passadas como parametro.
     */
    public List<AbstractRecipe> getPlusByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return null;
    }

    /**
     * @param name Nome ou parte do nome de uma receita.
     * @return Retorna uma lista de identificadores de receitas que possuem a String name passada como parametro.
     * no seu nome.
     */
    public List<AbstractRecipe> getResultByRecipeName(String name) {
        return null;
    }

    /**
     * @return Retorna as receitas mais populares, ou seja, mais visualizadas pelo usuarios.
     */
    public List<AbstractRecipe> getPopularRecipes() {
        return null;
    }

    /**
     * @param recipe Receita a ser registrada no Banco de Dados no Servidor
     * @return Retorna true se a receita for registrada no banco de dados no servidor e retorna false
     * se a receita nao for registrada no banco de dados no servidor,
     * mas for registrada no Banco de dados local.
     */
    public long registerRecipe(Recipe recipe) {
        RegisterRecipeTask task = new RegisterRecipeTask(recipe);
        result = -1;

        task.execute();

        return result;
    }

    /**
     * @param id Identificador de uma receita.
     * @param taste Valor de classificação de sabor de uma receita.
     * @return Retorna true se a classificação da receita for atualizada no banco de dados do servidor
     * e retorna false se a classificação da receita não for atualizada no banco de dados do servidor.
     */
    public boolean classifyTaste(long id, float taste) {
        return false;
    }

    /**
     * @param id Identificador de uma receita.
     * @param difficulty Valor de classificação de dificuldade de uma receita.
     * @return Retorna true se a classificação da receita for atualizada no banco de dados do servidor
     * e retorna false se a classificação da receita não for atualizada no banco de dados do servidor.
     */
    public boolean classifyDifficulty(long id, float difficulty) {
        return false;
    }

    /**
     * @param recipes Lista com todas as receitas do usuário
     * @return Retorna a lista de receitas com as receitas de entrada que foram modificadas no servidor.
     */
    public List<Recipe> syncRecipes(List<Recipe> recipes) {
        return null;
    }

    public String callUrl(String url) {
        StringBuffer buffer_string = new StringBuffer(url);
        String replyString = "";

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(buffer_string.toString());
        try {
            HttpResponse response = httpclient.execute(httpget);
            InputStream is = response.getEntity().getContent();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(20);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            // Resultado: string do JSON!
            replyString = new String(baf.toByteArray());
            System.out.println("JSON " + replyString.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(replyString);
        return replyString.trim();
    }

    private class GetRecipeTask extends AsyncTask {
        private long id;
        String temp;

        public GetRecipeTask(long id) {
            this.id = id;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            temp = callUrl("http://helpmecook.com.br/ws/FullRecipe.php?param=" + id);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (temp == null) {
                //Lançar alguma exceção
            }
            recipe = parseRecipe(temp);
        }

        private Recipe parseRecipe(final String response) {
            Recipe temp = new Recipe();

            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("id")) {
                    temp.setId(jsonObject.optLong("id"));
                    temp.setName(jsonObject.optString("name"));
                    temp.setTaste((float) jsonObject.optDouble("taste"));
                    temp.setDifficulty((float) jsonObject.optDouble("difficulty"));
                    temp.setPictureToString(jsonObject.optString("picture"));

                    JSONArray ingJSONArray = jsonObject.getJSONArray("ingredientList");
                    List<Long> ingredientList = new ArrayList<Long>();
                    for (int i = 0; i < ingJSONArray.length(); i++){
                        ingredientList.add(ingJSONArray.getLong(i));
                    }
                    temp.setIngredientList(ingredientList);

                    JSONArray unitsJSONArray = jsonObject.getJSONArray("units");
                    List<String> units = new ArrayList<String>();
                    for (int i = 0; i < unitsJSONArray.length(); i++) {
                        units.add(unitsJSONArray.getString(i));
                    }
                    temp.setUnits(units);

                    temp.setText(jsonObject.getString("text"));
                    temp.setEstimatedTime(jsonObject.getInt("estimatedTime"));
                    temp.setPortionNum(jsonObject.getString("portionNum"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return temp;
        }
    }

    private class RegisterRecipeTask extends AsyncTask {
        Recipe recipeReg;
        String temp;

        public RegisterRecipeTask(Recipe recipeReg) {
            this.recipeReg = recipeReg;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            String url = "http://helpmecook.com.br/ws/UploadRecipe.php?param={\"recipe\":{" +
                    "\"id\":\"" + recipeReg.getId() +
                    "\",\"name\":\"" + recipeReg.getName() +
                    "\",\"taste\":\"" + recipeReg.getTaste() +
                    "\",\"difficulty\":\"" + recipeReg.getDifficulty() +
                    "\",\"picture\":\"" + recipeReg.getPictureToString() +
                    "\",\"ingredientList\":[";
            for (int i = 0; i < recipeReg.getIngredientNum(); i++) {
                url += "\"" + recipeReg.getIngredientList().get(i) + "\"";
                if (i != recipeReg.getIngredientNum()-1) url += ",";
            }
            url += "],\"units\":[";
            for (int i = 0; i < recipeReg.getIngredientNum(); i++) {
                url += "\"" + recipeReg.getUnits().get(i) + "\"";
                if (i != recipeReg.getIngredientNum()-1) url += ",";
            }
            url += "],\"text\":" + recipeReg.getText() +
                    "\",\"estimatedTime\":\"" + recipeReg.getEstimatedTime() +
                    "\",\"portionNum\":\"" + recipeReg.getPortionNum() + "\"}}";

            Log.i("URL", url);
            Log.i("URL", url.length() + "");

            temp = callUrl(url);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            result = Long.parseLong(temp);
        }
    }

}