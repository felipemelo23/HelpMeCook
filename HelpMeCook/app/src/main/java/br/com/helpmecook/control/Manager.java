package br.com.helpmecook.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Cookbook;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.model.Recipe;
import br.com.helpmecook.sqlite.CookbookDAO;
import br.com.helpmecook.sqlite.IngredientDAO;
import br.com.helpmecook.sqlite.RecentsDAO;
import br.com.helpmecook.sqlite.RecipeDAO;

/**
 * Created by Felipe on 30/04/2015.
 */
public class Manager {
    private static ConnectionAccessor accessor = new ConnectionAccessor();

    /**
     * @param id Número inteiro que identifica uma receita.
     * @param context Contexto da aplicação
     * @return Retorna a receita relativa ao id passado como parâmetro.
     */
    public static Recipe getRecipeById(long id, Context context) {
        RecipeDAO recipeDAO = new RecipeDAO(context);
        RecentsDAO recentsDAO = new RecentsDAO(context);

        try{
            Log.i("Manager", "try");
            recipeDAO.open();
            recentsDAO.open();

            //Tenta achar a receita no banco de dados local.
            Recipe recipe = recipeDAO.read(id);

            //Se a receita não estiver no banco de dados local, procura no servidor.
            if (recipe == null) {
                Log.i("Manager", "if (recipe == null)");
                recipe = accessor.getRecipeById(id);
            }

            //Se a receita foi encontrada, atualiza o último acesso dela e adiciona ela na tabela Recents
            if (recipe != null) {
                Log.i("Manager","recipe != null");
                Calendar cal = Calendar.getInstance();
                recipe.updateLastAcess(cal);
                recipeDAO.update(recipe);

                if (recentsDAO.read(recipe.getId()) == null){
                    Log.i("Manager","insert");
                    recentsDAO.insert(recipe);
                } else {
                    Log.i("Manager","update");
                    recentsDAO.update(recipe);
                    Log.i("Maneger", recentsDAO.readAll().size() + "");
                }
            } else {
                Log.i("Manager","recipe == null");
            }

        recipeDAO.close();
        recentsDAO.close();

        return recipe;

        } catch (java.sql.SQLException e) {
            return null;
        }
    }

    /**
     * @param ids Lista de Identificadores de Receita.
     * @return Retorna uma lista de receitas resumidas.
     */
    public static List<AbstractRecipe> getAbstractRecipes(List<Long> ids, Context context) {
        RecipeDAO recipeDAO = new RecipeDAO(context);
        AbstractRecipe recipe;
        List<AbstractRecipe> recipes = null;

        try {
            recipeDAO.open();

            for (long id : ids) {
                recipe = recipeDAO.readAbstractRecipe(id);
                recipes.add(recipe);
            }

            recipeDAO.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return recipes;
    }

    /**
     * @param wanted Lista de ingredientes desejaveis.
     * @param unwanted Lista de ingredientes indesejaveis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem exatamente a busca
     * por ingredientes, ou seja, contem receitas com exatamente os ingredientes desejados,
     * dadas as duas listas de ingredientes passadas como parametro.
     */
    public static List<AbstractRecipe> getResultByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return accessor.getResultByIngredientLists(wanted, unwanted);
    }

    /**
     * @param wanted Lista de ingredientes desejaveis.
     * @param unwanted Lista de ingredientes indesejaveis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem a busca
     * por ingredientes, mas tem 1 ingrediente a mais, ou seja, contem receitas com exatamente os ingredientes
     * desejados mais 1 ingrediente, dadas as duas listas de ingredientes passadas como parametro.
     */
    public static List<AbstractRecipe> getPlusByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return accessor.getPlusByIngredientLists(wanted, unwanted);
    }

    /**
     * @param context Contexto da aplicação
     * @return Retorna uma lista com os IDs das receitas visualizadas recentemente.
     */
    public static List<AbstractRecipe> getRecentRecipes(Context context) {
        RecentsDAO recentsDAO = new RecentsDAO(context);

        try {
            recentsDAO.open();
            List<Long> ids = recentsDAO.readAll(); //Esse metodo esta retornar ordenado por LastAccess.
            List<AbstractRecipe> recents = getAbstractRecipes(ids, context);
            return  recents;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param recipe Receita a ser adicionada no cookbook.
     * @param context Contexto da aplicação
     * @return Retorna true se a receita foi adicionada ao cookbook e false se ela não foi.
     */
    public static Boolean addToCookbook(Recipe recipe, Context context) {
        CookbookDAO cookbookDAO = new CookbookDAO(context);

        try{
            cookbookDAO.open();

            if (cookbookDAO.insert(recipe) != -1){
                cookbookDAO.close();
                return true;
            } else {
                cookbookDAO.close();
                return  false;
            }
        } catch (java.sql.SQLException e) {
            return false;
        }
    }

    /**
     * @param context Contexto da aplicação
     * @return Lista dos ingredientes da aplicacao
     */
    public static List<Ingredient> getIngredients(Context context) {
        IngredientDAO ingredientDAO = new IngredientDAO(context);
        List<Ingredient> ingredients = null;

        try {
            ingredientDAO.open();
            ingredients = ingredientDAO.readAll();
            ingredientDAO.close();

        } catch(java.sql.SQLException e) {
            e.printStackTrace();
        }

        return ingredients;
    }

    public static List<Ingredient> getRecipeIngredients(List<Long> ids, Context context) {
        IngredientDAO ingredientDAO = new IngredientDAO(context);
        List<Ingredient> ingredients = new ArrayList<Ingredient>();

        try {
            ingredientDAO.open();

            for (long id : ids){
                ingredients.add(ingredientDAO.read(id));
            }

            ingredientDAO.close();

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return ingredients;
    }

    public static void insertAllIngredients(Context context) {
        String names[] = new String[]{"Açucar", "Arroz", "Aveia", "Azeite", "Azeitona", "Biscoitos", "Café", "Catchup", "Chocolate em pó", "Creme de leite", "Ervilha", "Farinha de linhaça", "Farinha de milho", "Farinha de trigo", "Farinha de mandioca", "Farofa", "Feijão carioca", "Feijão preto", "Fermento em pó", "Gelatina", "Geléia", "Leite condensado", "Leite de coco", "Leite em pó", "Macarrão", "Maionese", "Maisena", "Milho (Pipoca)", "Molho de tomate", "Molho inglês", "Mostarda", "Óleo", "Pão", "Torrada", "Tomate", "Catupiry", "Danone", "Iogurte", "Leite", "Manteiga", "Margarina", "Ovo", "Requeijão", "Queijo branco", "Queijo amarelo", "Presunto"};
        IngredientDAO ingredientDAO = new IngredientDAO(context);

        try {
            ingredientDAO.open();

            Ingredient ingredient = new Ingredient();
            for (int i = 0; i < names.length; i++) {
                ingredient.setName(names[i]);
                ingredient.setId(i);
                ingredient.setIconPath(R.drawable.checkbox_blank_circle);
                ingredientDAO.insert(ingredient);
            }

            ingredientDAO.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param recipe Receita que sera removida do cookbook
     * @param context Contexto da aplicação
     * @return Retorna true se a receita foi removida do cookbook e false se ela não foi.
     */
    public static boolean removeFromCookbook(Recipe recipe, Context context) {
        CookbookDAO cookbookDAO = new CookbookDAO(context);

        try {
            cookbookDAO.open();
            boolean deleted = cookbookDAO.delete(recipe.getId());
            cookbookDAO.close();
            return deleted;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param id ID da receita que foi classificada
     * @param taste Valor que foi atribuido ao sabor da receita
     * @return Retorna true se a classificacao foi enviada para o servidor e false se nao
     */
    public static boolean classifyTaste(long id, float taste) {
        return accessor.classifyTaste(id, taste);
    }

    /**
     * @param id ID da receita que foi classificada
     * @param difficulty Valor que foi atribuido a dificuldade da receita
     * @return Retorna true se a classificacao foi enviada para o servidor e false se nao
     */
    public static boolean classifyDifficulty(long id, float difficulty) {
        return accessor.classifyDifficulty(id, difficulty);
    }

    /**
     * Essa funcao garante que todas as receitas no banco de dados local sejam enviadas para o
     * servidor e que todas as funcoes que estao no banco de dados local e que estão no servidor
     * e foram modificadas nele sejam atualizadas do banco de dados local
     * @param context Contexto da aplicação
     * @return Retorna true se ela garantiu tudo e falso se não
     */
    public static boolean syncAll(Context context) {
        RecipeDAO recipeDAO = new RecipeDAO(context);

        try {
            recipeDAO.open();
            List<Recipe> notSyncs = recipeDAO.readNotSync();
            for (Recipe recipe : notSyncs) {
                registerRecipe(recipe, context);
            }

            List<Recipe> modifieds = accessor.syncRecipes(recipeDAO.readAll());

            for (Recipe recipe : modifieds) {
                recipeDAO.update(recipe);
            }

            recipeDAO.close();

            return true;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param name Nome das receitas que se deseja encontrar
     * @return Uma lista de AbstractRecipe que contem o nome procurado
     */
    public static List<AbstractRecipe> getResultByRecipeName(String name){
        return accessor.getResultByRecipeName(name);
    }

    /**
     * @return Uma lista de AbstractRecipe com as receitas populares
     */
    public static List<AbstractRecipe> getPopularRecipes (){
        return accessor.getPopularRecipes();
    }

    /**
     * @param context Contexto da aplicação
     * @return Retorna um objeto Cookbook com a lista de receitas do Cookbook
     */
    public static Cookbook getCookbook(Context context){
        CookbookDAO cookbookDAO = new CookbookDAO(context);
        RecipeDAO recipeDAO = new RecipeDAO(context);
        Cookbook cookbook = new Cookbook();

        try{
            cookbookDAO.open();
            recipeDAO.open();

            List<Long> ids = cookbookDAO.readAll();

            for (long id : ids) {
                Recipe recipe = recipeDAO.read(id);
                Log.i("Debug Cookbook Manager", id + "");
                cookbook.addRecipe(recipe);
            }

            recipeDAO.close();
            cookbookDAO.close();

            return cookbook;
        } catch (java.sql.SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param recipe Receita a ser verificada se está no Cookbook.
     * @param context Contexto da aplicação
     * @return retorna true se a receita está no Cookbook ou false se a receita não está no Cookbook.
     */
    public static boolean isOnCookbook(Recipe recipe, Context context) {
        List<AbstractRecipe> recipesOnCookbook = getCookbook(context).getRecipeList();
        return recipesOnCookbook.contains(recipe);
    }

    /**
     * @param recipe Receita que sera registrada
     * @param context Contexto da aplicação
     * @return Retorna 0 se nao esta nem no local nem remoto, 1 se esta apenas no local e 2 se esta tanto mo local quanto no remoto
     */
    public static int registerRecipe(Recipe recipe, Context context){
        CookbookDAO cookbookDAO = new CookbookDAO(context);
        RecipeDAO recipeDAO = new RecipeDAO(context);
        long internalDB, remoteDBId;
        remoteDBId = accessor.registerRecipe(recipe);

        try {
            Log.i("DebugManager", "Foi inserido no bd local");
            cookbookDAO.open();
            recipeDAO.open();

            if ((remoteDBId == -1) && (recipe.getId() == -1)){
                SharedPreferences data = context.getSharedPreferences("localId",0);
                SharedPreferences.Editor editor = data.edit();
                long localId = data.getLong("localIdValue", Long.MAX_VALUE);

                recipe.setId(localId);
                recipe.setSync(false);

                editor.putLong("localIdValue", localId - 1);
                editor.commit();

                recipeDAO.insert(recipe);
                cookbookDAO.insert(recipe);
                cookbookDAO.close();
                recipeDAO.close();
                return 1;
            }else{
                Log.i("DebugManager", "Foi inserido no servidor");
                recipe.setId(remoteDBId);
                recipe.setSync(true);
                internalDB = cookbookDAO.insert(recipe);
                cookbookDAO.close();
                return 2;
            }
        } catch (Exception e) {
            return 0;
        }
    }
}