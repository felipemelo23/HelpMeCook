package br.com.helpmecook.control;

import android.content.Context;

import java.util.Calendar;
import java.util.List;

import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Cookbook;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.model.Recipe;
import br.com.helpmecook.sqlite.CookbookDAO;
import br.com.helpmecook.sqlite.RecentsDAO;
import br.com.helpmecook.sqlite.RecipeDAO;

/**
 * Created by Felipe on 30/04/2015.
 */
public class Manager {
    private ConnectionAccessor accessor = new ConnectionAccessor();
    private Context context;

    public void setContext(Context context){
        this.context = context;
    }

    /**
     * @param id Número inteiro que identifica uma receita.
     * @return Retorna a receita relativa ao id passado como parâmetro.
     */
    public Recipe getRecipeById(int id) {
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
    public List<AbstractRecipe> getAbstractRecipes(List<Integer> ids) {
        return accessor.getAbstractRecipes(ids);
    }

    /**
     * @param wanted Lista de ingredientes desejaveis.
     * @param unwanted Lista de ingredientes indesejaveis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem exatamente a busca
     * por ingredientes, ou seja, contem receitas com exatamente os ingredientes desejados,
     * dadas as duas listas de ingredientes passadas como parametro.
     */
    public List<Integer> getResultByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return accessor.getResultByIngredientLists(wanted, unwanted);
    }

    /**
     * @param wanted Lista de ingredientes desejaveis.
     * @param unwanted Lista de ingredientes indesejaveis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem a busca
     * por ingredientes, mas tem 1 ingrediente a mais, ou seja, contem receitas com exatamente os ingredientes
     * desejados mais 1 ingrediente, dadas as duas listas de ingredientes passadas como parametro.
     */
    public List<Integer> getPlusByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return accessor.getPlusByIngredientLists(wanted, unwanted);
    }

    /**
     * @return Retorna as receitas visualizadas recentemente.
     */
    public List<Integer> getRecentRecipes() {
        RecentsDAO recentsDAO = new RecentsDAO(context);

        try {
            recentsDAO.open();
            List<Integer> recents = recentsDAO.readAll(); //Esse metodo deve retornar ordenado por LastAccess.
            recentsDAO.close();

            return  recents;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param recipe Receita a ser adicionada no cookbook.
     * @return Retorna true se a receita foi adicionada e false se ela não foi.
     */
    public Boolean addToCookbook(Recipe recipe) {
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

    public List<Ingredient> getIngredients() {
        IngredientDAO ingredientDAO = new IngredientDAO(context);

        try {
            ingredientDAO.open();
            List<Ingredient> ingredients = ingredientDAO.readAll();
            ingredientDAO.close();

            return ingredients;
        } catch(java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean removeFromCookbook(Recipe recipe) {
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

    public boolean classifyTaste(int id, float taste) {
        return accessor.classifyTaste(id,taste);
    }

    public boolean classifyDifficulty(int id, float difficulty) {
        return accessor.classifyDifficulty(id,difficulty);
    }

    public boolean syncAll() {
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

    public List<AbstractRecipe> getResultByRecipeName(String name){
        return accessor.getResultByRecipeName(name);
    }

    public List<AbstractRecipe> getPopularRecipes (){
        return accessor.getPopularRecipes();
    }

    public Cookbook getCookbook(){
        CookbookDAO cookbookDAO = new CookbookDAO(context);
        RecipeDAO recipeDAO = new RecipeDAO(context);
        Cookbook cookbook = new Cookbook();

        try{
            cookbookDAO.open();
            recipeDAO.open();

            List<Integer> ids = cookbookDAO.readAll();
            for (int id : ids) {
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

    // 0 = nao está nem no local nem remoto
    // 1 = está apenas no local
    // 2 = está tanto no local quanto remoto
    public int registerRecipe(Recipe recipe){
        CookbookDAO cookbookDAO = new CookbookDAO(context);
        long internalDB, remoteDBId;
        remoteDBId = accessor.registerRecipe(recipe);

        try {
            cookbookDAO.open();

            if ((remoteDBId == -1) && (recipe.getId() == -1)){
                recipe.setLocalId();
                cookbookDAO.insert(recipe);
                //setLocalId() deve setar o unSync() = false
                return 1;
            }else{
                recipe.setId(remoteDBId);
                recipe.setSync(true);
                internalDB = cookbookDAO.insert(recipe);
                return 2;
            }

            cookbookDAO.close();
        } catch (Exception e) {
            return 0;
        }
    }

}