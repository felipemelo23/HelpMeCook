package br.com.helpmecook.model;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Mariana on 30/04/15.
 */
public class Recipe extends AbstractRecipe {
    private List<Integer> ingredientList;
    private String text;
    private int estimatedTime;
    private String portionNum;
    private Calendar lastAcess;
    private int sync = 0;

    public List<Integer> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Integer> ingredientList) {
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

    public Calendar getLastAcess() {
        return lastAcess;
    }

    public void updateLastAcess(Calendar lastAcess) {
        this.lastAcess = lastAcess;
    }

    public int getIngredientNum (){
        return ingredientList.size();
    }

    public void addIngredient (int idIngredient){
        ingredientList.add(idIngredient);
    }

    public int isSync(){ return sync; }

    public void setSync(int isSync) { sync = isSync;  }

}