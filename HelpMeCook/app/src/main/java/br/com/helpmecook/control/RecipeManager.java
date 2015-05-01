package br.com.helpmecook.control;

<<<<<<< HEAD

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Calendar;
=======
>>>>>>> 229ff594554c2008ce8c6cbf3d094595e0641231
import java.util.List;

/**
 * Created by Felipe on 30/04/2015.
 */
<<<<<<< HEAD
public class RecipeManager {
    private ConnectionAccessor accessor = new ConnectionAccessor();

    /**
     * @param id Número inteiro que identifica uma receita.
     * @return Retorna a receita relativa ao id passado como parâmetro.
     */
    public Recipe getRecipeById(int id) {
        RecipeDAO recipeDAO = new RecipeDAO(this);
        RecentDAO recentDAO = new RecentDAO(this);

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
            int i = recentDAO.read(recipe.getId());
            if (i < 0) recentDAO.insert(recipe.getId());
        }

        return recipe;
=======
public class RecipeManager {/*
    private ConnectionAccessor accessor;

    public Recipe getRecipeById() {
        return null;
>>>>>>> 229ff594554c2008ce8c6cbf3d094595e0641231
    }

    public List<AbstractRecipe> getAbstractRecipes(List<Integer>) {
        return null;
    }

    public List<Integer> getResultByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return null;
    }

    public List<Integer> getPlusByIngredientLists(List<Ingredient> wanted, List<Ingredient> unwanted) {
        return null;
    }

    public List<Integer> getResultByRecipeName(String name) {
        return null;
    }

    public List<Integer> getRecentRecipes() {
        return null;
    }

    public List<Integer> getPopularRecipes() {
        return null;
    }

<<<<<<< HEAD
    /**
     * @return Retorna um objeto Cookbook inteiro para ser exibido.
     */
    public Cookbook getCookbook() {
        CookbookDAO cookbookDAO = new CookbookDAO(this);
        UnsyncDAO unsyncDAO = new UnsyncDAO(this);
        RecipeDAO recipeDAO = new RecipeDAO(this);
        //Carrega os IDs das receitas no cookbook.
        List<Integer> cookbookRecipes = cookbookDAO.readAll("ASC");
        //Carrega as receitas que ainda não foram sincronizadas.
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
     *
     * @param recipe Receita a ser adicionada no cookbook.
     * @return Retorna true se a receita foi adicionada e false se ela não foi.
     */
    public Boolean addToCookbook(Recipe recipe) {
        CookbookDAO cookbookDAO = new CookbookDAO(this);
        return cookbookDAO.insert(recipe.getId());
    }

    /**
     * @param recipe Receita a ser registrada no Banco de Dados no Servidor
     * @return Retorna true se a receita for registrada no banco de dados no servidor e retorna false
     * se a receita não for registrada no banco de dados no servidor,
     * mas for registrada no Banco de dados local.
     */
    public Boolean registerRecipe(Recipe recipe) {
        //Instancia uma nova receita. A receita é criada sem ID e um ID global só é atribuído a ela
        //quando ela é adicionada ao servidor.
        Recipe newRecipe = recipe;
        RecipeDAO recipeDAO = new RecipeDAO(this);
        UnsyncDAO unsyncDAO = new UnsyncDAO(this);
        CookbookDAO cookbookDAO = new CookbookDAO(this);
        Boolean inserted;

        //Tenta adicionar a receita no servidor e pega dele o ID que ele deu para ela.
        int id = accessor.registerRecipe(recipe);

        //Se conseguiu adicioar (Se o ID não é negativo).
        if (id >= 0) {
            //Atribui o ID a receita.
            newRecipe.setId(id);
            //Adiciona ela na tabela local de receitas.
            recipeDAO.insert(newRecipe);
            cookbookDAO.insert(newRecipe.getId());
            return true;
        } else { //Se não conseguiu adicionar.
            //Atribui um ID aleatório e não repetido para a receita.
            do {
                newRecipe.setId((int) Math.random() % 1000000);
            } while(unsyncDAO.read(newRecipe.getId()) != null);

            //Insere na tabela de receitas não sincronizadas.
            unsyncDAO.insert(newRecipe);
            return false;
        }
=======
    public Boolean registerRecipe(Recipe recipe) {
        return false;
>>>>>>> 229ff594554c2008ce8c6cbf3d094595e0641231
    }

    public Boolean classifyRecipe(int id, float taste, float difficult) {
        return false;
    }

    public Boolean syncAll() {
<<<<<<< HEAD
        UnsyncDAO unsyncDAO = new UnsyncDAO(this);
        List<Recipe> unsyncs = new ArrayList<Recipe>();
        unsyncs = unsyncDAO.readAll("ASC");
        Boolean allSynced = true; //Assume-se inicialmente que todas a receitas serão sincronizadas.

        //Para todas as receitas na tabela de receitas não sincrinizadas.
        for (Recipe recipe : unsyncs) {
            //Tenta registrar a receita.
            if (registerRecipe(recipe)) {
                //Se conseguir, como a receita já foi adicionada na tabela local de receitas,
                //apenas exclui ela da tabela de receitas não sincronizadas.
                unsyncDAO.delete(recipe);
            } else {
                //Se não conseguir não pode-se mais dizer que todas as receitas foram sincronizadas.
                allSynced = false;
            }
        }

        return allSynced;
    }
=======
        return false;
    }*/
>>>>>>> 229ff594554c2008ce8c6cbf3d094595e0641231
}
