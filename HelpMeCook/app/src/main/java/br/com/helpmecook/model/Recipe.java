package br.com.helpmecook.model;

/**
 * Created by mariana on 01/05/15.
 */

import java.sql.Time;
import java.util.Date;
import java.util.List;

/**
 * Created by ck0119 on 30/04/15.
 */
public class Recipe extends AbstractRecipe {
    private List<Ingredient> ingredientList;
    private String text;
    private int estimatedTime;
    private String portionNum;
    private Time lastAcess;

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getPortionNum() {
        return portionNum;
    }

    public void setPortionNum(String portionNum) {
        this.portionNum = portionNum;
    }

    public Time getLastAcess() {
        return lastAcess;
    }

    public void updateLastAcess(Time lastAcess) {
        this.lastAcess = lastAcess;
    }

    public int getIngredientNum (){
        return ingredientList.size();
    }

    public void addIngredient (int idIngredient){
        ingredientList.add(idIngredient);
    }
}