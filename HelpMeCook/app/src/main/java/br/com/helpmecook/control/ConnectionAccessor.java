package br.com.helpmecook.control;

import java.util.List;

/**
 * Created by Felipe on 30/04/2015.
 */
public class ConnectionAccessor {
    public Recipe getRecipeById() {
        return null;
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

    public Boolean registerRecipe(Recipe recipe) {
        return false;
    }

    public Boolean classifyRecipe(int id, float taste, float difficult) {
        return false;
    }
}
