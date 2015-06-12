package br.com.helpmecook.model;

/**
 * Created by Andre on 30/04/2015.
 */
public class Ingredient {

    private long id;
    private String name;
    private int iconPath;
    private  String quantity;

    public long getId(){ return this.id; }

    public void setId(long id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getIconPath(){
        return this.iconPath;
    }

    public void setIconPath(int iconPath){
        this.iconPath = iconPath;
    };

    public void setQuantity(String quantity){ this.quantity = quantity; }

    public String getQuantity(){ return this.quantity; }

    @Override
    public String toString() {
        return this.name;
    }
}
