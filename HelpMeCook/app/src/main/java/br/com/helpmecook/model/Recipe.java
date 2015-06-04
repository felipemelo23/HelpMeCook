package br.com.helpmecook.model;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
    private List<String> units;
    private String text;
    private int estimatedTime;
    private String portionNum;
    private Calendar lastAcess;
    private boolean sync;

    public Recipe() {
        super();
        ingredientList = new ArrayList<Long>();
        units = new ArrayList<String>();
        this.id = -1;
        this.text = null;
        this.estimatedTime = 0;
        this.portionNum = null;
        this.lastAcess = null;
        this.sync = false;
    }

    public List<Long> getIngredientList() {
        System.out.println("teste" + ingredientList.size());
        return ingredientList;
    }

    public void setIngredientList(List<Long> ingredientList) {
        this.ingredientList = ingredientList;
        this.units = new ArrayList<String>();
        for (long l : ingredientList) {
            units.add("0 unid.");
        }
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

    public void addIngredient (String unit, long idIngredient){
        units.add(unit);
        ingredientList.add(idIngredient);
    }

    public boolean isSync(){ return sync; }

    public void setSync(boolean isSync) { sync = isSync;  }

    public List<String> getUnits() {
        return units;
    }

    public void setUnits(List<String> units) {
        this.units = units;
    }

    public String getPictureToString() {
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte[] b=baos.toByteArray();

        String stringPicture = Base64.encodeToString(b, Base64.URL_SAFE);
        Log.i("StringPictureRecipe", stringPicture);

        return stringPicture;
    }

    public void setPictureToString(String stringPicture) {

        try{
            byte[] encodeByte = Base64.decode(stringPicture, Base64.URL_SAFE);
            setPicture(BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length));
        }catch(Exception e){
            e.getMessage();
        }
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof Recipe && this.id == ((Recipe) o).getId()) {
            return true;
        }
        return false;
    }
}