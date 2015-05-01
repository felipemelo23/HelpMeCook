package br.com.helpmecook.control;

import java.util.List;

import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Recipe;

/**
 * Created by Felipe on 30/04/2015.
 */
public class ConnectionAccessor {
    public Recipe getRecipeById(int id) {
        return null;
    }

    public List<AbstractRecipe> getAbstractRecipes(List<Integer> ids) {
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

    public int registerRecipe(Recipe recipe) {
        return -1;
    }

    public Boolean classifyRecipe(int id, float taste, float difficult) {
        return false;
    }
}
