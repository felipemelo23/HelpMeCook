package br.com.helpmecook.model;

import android.graphics.Bitmap;

/**
 * Created by ck0119 on 30/04/15.
 */

public class AbstractRecipe {
    private int id;
    private String name;
    private float taste;
    private float difficulty;
    private Bitmap picture;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public float getTaste() {
        return taste;
    }

    public void setTaste(float taste) {
        this.taste = taste;
    }

    public float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }
}
