package br.com.helpmecook.control;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Recipe;

/**
 * Created by Felipe on 30/04/2015.
 */
public class RecipeManager {
    private ConnectionAccessor accessor = new ConnectionAccessor();

    /**
     * @param id N�mero inteiro que identifica uma receita.
     * @return Retorna a receita relativa ao id passado como par�metro.
     */
    public Recipe getRecipeById(int id) {
        RecipeDAO recipeDAO = new RecipeDAO(this);

        Recipe recipe = recipeDAO.read(id);
        if (recipe == null) {
            recipe = accessor.getRecipeById(id);
        }

        return recipe;
    }

    /**
     * @param ids Lista de Identificadores de Receita.
     * @return Retorna uma lista de receitas resumidas.
     */
    public List<AbstractRecipe> getAbstractRecipes(List<Integer> ids) {
        return accessor.getAbstractRecipes(ids);
    }

    /**
     * @param wanted Lista de ingredientes desej�veis.
     * @param unwanted Lista de ingredientes indesej�veis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem exatamente a busca
     * por ingredientes, ou seja, cont�m receitas com exatamente os ingredientes desejados,
     * dadas as duas listas de ingredientes passadas como par�metro.
     */
    public List<Integer> getResultByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return accessor.getResultByIngredientLists(wanted, unwanted);
    }

    /**
     * @param wanted Lista de ingredientes desej�veis.
     * @param unwanted Lista de ingredientes indesej�veis.
     * @return Retorna uma lista de identificadores de receitas que satisfazem a busca
     * por ingredientes, mas t�m 1 ingrediente a mais, ou seja, cont�m receitas com exatamente os ingredientes
     * desejados mais 1 ingrediente, dadas as duas listas de ingredientes passadas como par�metro.
     */
    public List<Integer> getPlusByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return accessor.getPlusByIngredientLists(wanted, unwanted);
    }

    /**
     * @param name Nome ou parte do nome de uma receita.
     * @return Retorna uma lista de identificadores de receitas que possuem a String name passada como par�metro.
     * no seu nome.
     */
    public List<Integer> getResultByRecipeName(String name) {
        return accessor.getResultByRecipeName(name);
    }

    /**
     * @return Retorna as receitas visualizadas recentemente.
     */
    public List<Integer> getRecentRecipes() {
        RecentDAO recentDAO = new RecentDAO(this);
        return recentDAO.readAll("ASC"); //Esse m�todo deve retornar ordenado por LastAccess.
    }

    /**
     * @return Retorna as receitas mais populares, ou seja, mais visualizadas pelo usu�rios.
     */
    public List<Integer> getPopularRecipes() {
        return accessor.getPopularRecipes();
    }

    /**
     * @param recipe Receita a ser registrada no Banco de Dados no Servidor
     * @return Retorna true se a receita for registrada no banco de dados no servidor e retorna false
     * se a receita n�o for registrada no banco de dados no servidor,
     * mas for registrada no Banco de dados local.
     */
    public Boolean registerRecipe(Recipe recipe) {
        Recipe newRecipe = recipe;
        RecipeDAO recipeDAO = new RecipeDAO(this);
        UnsyncDAO unsyncDAO = new UnsyncDAO(this);
        int id = accessor.registerRecipe(recipe);
        if (id >= 0) {
            newRecipe.setId(id);
            recipeDAO.insert(newRecipe);
            return true;
        } else {
            newRecipe.setId((int) Math.random()%100000);
            unsyncDAO.insert(newRecipe);
            return false;
        }
    }

    /**
     * @param id Identificador de uma receita.
     * @param taste Valor de classifica��o de sabor de uma receita.
     * @param difficult Valor de classifica��o de dificuldade de uma receita.
     * @return Retorna true se a classifica��o da receita for atualizada no banco de dados do servidor
     * e retorna false se a classifica��o da receita n�o for atualizada no banco de dados do servidor.
     */
    public Boolean classifyRecipe(int id, float taste, float difficult) {
        return accessor.classifyRecipe(id,taste,difficult);
    }

    /**
     * Garante que todas a receitas que est�o no banco de dados local sejam sincronizadas no servidor.
     * @return Retorna true se todas as receitas no banco de dados local conseguirem ser atualizadas
     * no servidor e false se o alguma receita n�o puder ser atualizadas.
     */
    public Boolean syncAll() {
        UnsyncDAO unsyncDAO = new UnsyncDAO(this);
        List<Recipe> unsyncs = new ArrayList<Recipe>();
        Boolean allSynced = true;

        for (Recipe recipe : unsyncs) {
            if (registerRecipe(recipe)) {
                unsyncDAO.delete(recipe);
            } else {
                allSynced = false;
            }
        }

        return allSynced;
    }
}
