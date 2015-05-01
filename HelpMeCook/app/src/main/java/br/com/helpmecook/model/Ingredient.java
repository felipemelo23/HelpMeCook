package br.com.helpmecook.model;

/**
 * Created by Chibi on 30/04/2015.
 */
public class Ingredient {

    private int id;
    private String name;
    private String iconPath;

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getIconPath(){
        return this.iconPath;
    }

    public void setIconPath(String iconPath){
        this.iconPath = iconPath;
    };

}
