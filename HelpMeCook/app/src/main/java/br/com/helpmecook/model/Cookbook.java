package br.com.helpmecook.model;

import android.content.res.Resources;
import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Chibi on 30/04/2015.
 */
public class Cookbook {
    public List<Recipe> recipes;

    public List<Recipe> getRecipeList(){
        return this.recipes;
    }
    public void setRecipeList(List<Recipe> recipes){
        this.recipes = recipes;
    }

    public Boolean addRecipe(Recipe recipe){
        this.recipes.add(recipe);
        return true;
    }
    public Recipe getRecipeAt(int index){
        return this.recipes.get(index);
    }

    public Boolean removeRecipe(int index){
        this.recipes.remove(index);
        return false;
    }

    public Recipe getRecipeById(int id){
        Recipe recipe;
        recipe = new Recipe();
        for(int i = 0; i < this.recipes.size(); i++){
            recipe = this.recipes.get(i);
            if (recipe.getId() == id) return recipe;
        }
        return null;
    }
    public int getRecipesNum(){
        return this.recipes.size();}

}
