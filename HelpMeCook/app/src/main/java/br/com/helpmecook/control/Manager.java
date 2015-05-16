package br.com.helpmecook.control;

import android.content.Context;
import android.content.SharedPreferences;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

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
    private static Context context;

    public static void setContext(Context newContext){
         context = newContext;
    }

    /**
     * @param id Número inteiro que identifica uma receita.
     * @return Retorna a receita relativa ao id passado como parâmetro.
     */
    public static Recipe getRecipeById(long id) {
        RecipeDAO recipeDAO = new RecipeDAO(context);
        RecentsDAO recentsDAO = new RecentsDAO(context);

        try{
            recipeDAO.open();
            recentsDAO.open();

            //Tenta achar a receita no banco de dados local.
            Recipe recipe = recipeDAO.read(id);

            //Se a receita não estiver no banco de dados local, procura no servidor.
            if (recipe == null) {
                recipe = accessor.getRecipeById(id);
            }

            //Se a receita foi encontrada, atualiza o último acesso dela e adiciona ela na tabela Recents
            if (recipe != null) {
                Calendar cal = Calendar.getInstance();
                recipe.updateLastAcess(cal);
                recipeDAO.update(recipe);

                if (recentsDAO.read(recipe.getId()) == null){
                    recentsDAO.insert(recipe);
                } else {
                    recentsDAO.update(recipe);
                }
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
    public static List<AbstractRecipe> getAbstractRecipes(List<Integer> ids) {
        return accessor.getAbstractRecipes(ids);
    }

    /**
     * @param wanted Lista de ingredientes desejaveis.
     * @param unwanted Lista de ingredientes indesejaveis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem exatamente a busca
     * por ingredientes, ou seja, contem receitas com exatamente os ingredientes desejados,
     * dadas as duas listas de ingredientes passadas como parametro.
     */
    public static List<Long> getResultByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return accessor.getResultByIngredientLists(wanted, unwanted);
    }

    /**
     * @param wanted Lista de ingredientes desejaveis.
     * @param unwanted Lista de ingredientes indesejaveis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem a busca
     * por ingredientes, mas tem 1 ingrediente a mais, ou seja, contem receitas com exatamente os ingredientes
     * desejados mais 1 ingrediente, dadas as duas listas de ingredientes passadas como parametro.
     */
    public static List<Long> getPlusByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return accessor.getPlusByIngredientLists(wanted, unwanted);
    }

    /**
     * @return Retorna uma lista com os IDs das receitas visualizadas recentemente.
     */
    public static List<Long> getRecentRecipes() {
        RecentsDAO recentsDAO = new RecentsDAO(context);

        try {
            recentsDAO.open();
            List<Long> recents = recentsDAO.readAll(); //Esse metodo esta retornar ordenado por LastAccess.
            recentsDAO.close();

            return  recents;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param recipe Receita a ser adicionada no cookbook.
     * @return Retorna true se a receita foi adicionada ao cookbook e false se ela não foi.
     */
    public static Boolean addToCookbook(Recipe recipe) {
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
     * @return Lista dos ingredientes da aplicacao
     */
    public static List<Ingredient> getIngredients() {
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

    public static List<Ingredient> getRecipeIngredients(List<Long> ids) {
        IngredientDAO ingredientDAO = new IngredientDAO(context);
        List<Ingredient> ingredients = null;

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

    /**
     * @param recipe Receita que sera removida do cookbook
     * @return Retorna true se a receita foi removida do cookbook e false se ela não foi.
     */
    public static boolean removeFromCookbook(Recipe recipe) {
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
        return accessor.classifyDifficulty(id,difficulty);
    }

    /**
     * Essa funcao garante que todas as receitas no banco de dados local sejam enviadas para o
     * servidor e que todas as funcoes que estao no banco de dados local e que estão no servidor
     * e foram modificadas nele sejam atualizadas do banco de dados local
     * @return Retorna true se ela garantiu tudo e falso se não
     */
    public static boolean syncAll() {
        RecipeDAO recipeDAO = new RecipeDAO(context);

        try {
            recipeDAO.open();
            List<Recipe> notSyncs = recipeDAO.readNotSync();
            for (Recipe recipe : notSyncs) {
                registerRecipe(recipe);
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
     * @return Retorna um objeto Cookbook com a lista de receitas do Cookbook
     */
    public static Cookbook getCookbook(){
        CookbookDAO cookbookDAO = new CookbookDAO(context);
        RecipeDAO recipeDAO = new RecipeDAO(context);
        Cookbook cookbook = new Cookbook();

        try{
            cookbookDAO.open();
            recipeDAO.open();

            List<Long> ids = cookbookDAO.readAll();
            for (long id : ids) {
                cookbook.addRecipe(recipeDAO.read(id));
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
     * @param recipe Receita que sera registrada
     * @return Retorna 0 se nao esta nem no local nem remoto, 1 se esta apenas no local e 2 se esta tanto mo local quanto no remoto
     */
    public static int registerRecipe(Recipe recipe){
        CookbookDAO cookbookDAO = new CookbookDAO(context);
        long internalDB, remoteDBId;
        remoteDBId = accessor.registerRecipe(recipe);

        try {
            cookbookDAO.open();

            if ((remoteDBId == -1) && (recipe.getId() == -1)){
                SharedPreferences data = context.getSharedPreferences("localId",0);
                SharedPreferences.Editor editor = data.edit();
                long localId = data.getLong("localIdValue", Long.MAX_VALUE);

                recipe.setId(localId);
                recipe.setSync(false);

                editor.putLong("localIdValue", localId - 1);
                editor.commit();

                cookbookDAO.insert(recipe);
                cookbookDAO.close();
                return 1;
            }else{
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