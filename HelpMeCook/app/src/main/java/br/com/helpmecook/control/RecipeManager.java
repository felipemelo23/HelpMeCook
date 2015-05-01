package br.com.helpmecook.control;

import java.util.ArrayList;
import java.util.Calendar;
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
        RecentDAO recentDAO = new RecentDAO(this);

        //Tenta achar a receita no banco de dados local.
        Recipe recipe = recipeDAO.read(id);

        //Se a receita n�o estiver no banco de dados local, procura no servidor.
        if (recipe == null) {
            recipe = accessor.getRecipeById(id);
        }

        //Se a receita foi encontrada, atualiza o �ltimo acesso dela e adiciona ela na tabela Recents
        if (recipe != null) {
            Calendar cal = Calendar.getInstance();
            recipe.updateLastAcess(cal);
            recipeDAO.update(recipe);
            int i = recentDAO.read(recipe.getId());
            if (i < 0) recentDAO.insert(recipe.getId());
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
     * @return Retorna um objeto Cookbook inteiro para ser exibido.
     */
    public Cookbook getCookbook() {
        CookbookDAO cookbookDAO = new CookbookDAO(this);
        UnsyncDAO unsyncDAO = new UnsyncDAO(this);
        RecipeDAO recipeDAO = new RecipeDAO(this);
        //Carrega os IDs das receitas no cookbook.
        List<Integer> cookbookRecipes = cookbookDAO.readAll("ASC");
        //Carrega as receitas que ainda n�o foram sincronizadas.
        List<Recipe> unsyncRecipes = unsyncDAO.realAll("ASC");
        Cookbook cookbook = new Cookbook();

        //Adicionar as receitas no objeto cookbook
        for (int id : cookbookRecipes) {
            Recipe recipe = recipeDAO.read(id);
            cookbook.addRecipe(recipe);
        }
        for (Recipe recipe : unsyncRecipes) {
            cookbook.addRecipe(recipe);
        }

        return cookbook;
    }

    /**
     * @param recipe Receita a ser adicionada no cookbook.
     * @return Retorna true se a receita foi adicionada e false se ela n�o foi.
     */
    public Boolean addToCookbook(Recipe recipe) {
        CookbookDAO cookbookDAO = new CookbookDAO(this);
        return cookbookDAO.insert(recipe.getId());
    }

    /**
     * @param recipe Receita a ser registrada no Banco de Dados no Servidor
     * @return Retorna true se a receita for registrada no banco de dados no servidor e retorna false
     * se a receita n�o for registrada no banco de dados no servidor,
     * mas for registrada no Banco de dados local.
     */
    public Boolean registerRecipe(Recipe recipe) {
        //Instancia uma nova receita. A receita � criada sem ID e um ID global s� � atribu�do a ela
        //quando ela � adicionada ao servidor.
        Recipe newRecipe = recipe;
        RecipeDAO recipeDAO = new RecipeDAO(this);
        UnsyncDAO unsyncDAO = new UnsyncDAO(this);
        CookbookDAO cookbookDAO = new CookbookDAO(this);
        Boolean inserted;

        //Tenta adicionar a receita no servidor e pega dele o ID que ele deu para ela.
        int id = accessor.registerRecipe(recipe);

        //Se conseguiu adicioar (Se o ID n�o � negativo).
        if (id >= 0) {
            //Atribui o ID a receita.
            newRecipe.setId(id);
            //Adiciona ela na tabela local de receitas.
            recipeDAO.insert(newRecipe);
            cookbookDAO.insert(newRecipe.getId());
            return true;
        } else { //Se n�o conseguiu adicionar.
            //Atribui um ID aleat�rio e n�o repetido para a receita.
            do {
                newRecipe.setId((int) Math.random() % 1000000);
            } while (unsyncDAO.read(newRecipe.getId()) != null);

            //Insere na tabela de receitas n�o sincronizadas.
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
        unsyncs = unsyncDAO.readAll("ASC");
        Boolean allSynced = true; //Assume-se inicialmente que todas a receitas ser�o sincronizadas.

        //Para todas as receitas na tabela de receitas n�o sincrinizadas.
        for (Recipe recipe : unsyncs) {
            //Tenta registrar a receita.
            if (registerRecipe(recipe)) {
                //Se conseguir, como a receita j� foi adicionada na tabela local de receitas,
                //apenas exclui ela da tabela de receitas n�o sincronizadas.
                unsyncDAO.delete(recipe);
            } else {
                //Se n�o conseguir n�o pode-se mais dizer que todas as receitas foram sincronizadas.
                allSynced = false;
            }
        }

        return allSynced;
    }
}
