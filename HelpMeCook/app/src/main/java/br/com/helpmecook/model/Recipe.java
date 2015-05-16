package br.com.helpmecook.model;

import android.content.SharedPreferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import br.com.helpmecook.view.activity.MainActivity;

/**
 * Created by Mariana on 30/04/15.
 */
public class Recipe extends AbstractRecipe {
    private List<Long> ingredientList;
    private List<Integer> numberOfIng;
    private List<String> units;
    private String text;
    private int estimatedTime;
    private String portionNum;
    private Calendar lastAcess;
    private boolean sync;

    public Recipe() {
        super();
        ingredientList = new ArrayList<Long>();
        numberOfIng = new ArrayList<Integer>();
        units = new ArrayList<String>();
        this.text = null;
        this.estimatedTime = 0;
        this.portionNum = null;
        this.lastAcess = null;
        this.sync = false;
    }

    public List<Long> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Long> ingredientList) {
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

    public void addIngredient (int quantity, String unit, long idIngredient){
        numberOfIng.add(quantity);
        units.add(unit);
        ingredientList.add(idIngredient);
    }

    public boolean isSync(){ return sync; }

    public void setSync(boolean isSync) { sync = isSync;  }

    public List<Integer> getNumberOfIng() {
        return numberOfIng;
    }

    public void setNumberOfIng(List<Integer> numberOfIng) {
        this.numberOfIng = numberOfIng;
    }

    public List<String> getUnits() {
        return units;
    }

    public void setUnits(List<String> units) {
        this.units = units;
    }
}