package br.com.helpmecook.model;

import android.graphics.Bitmap;

/**
 * Created by ck0119 on 30/04/15.
 */

public class AbstractRecipe {
    protected long id;
    protected String name;
    protected float taste;
    protected float difficulty;
    protected Bitmap picture;

    public AbstractRecipe() {
        this.id = -1;
        this.name = null;
        this.taste = 0;
        this.difficulty = 0;
        this.picture = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
