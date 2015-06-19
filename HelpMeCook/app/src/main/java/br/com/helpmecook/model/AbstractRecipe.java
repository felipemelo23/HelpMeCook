package br.com.helpmecook.model;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by ck0119 on 30/04/15.
 */

public class AbstractRecipe {
    protected long id;
    protected String name;
    protected float taste;
    protected float difficulty;
    protected Bitmap picture;
    protected int extraIngNum = -1;

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
        int threshold = 816;
        if (picture != null) {
            int size = Math.max(picture.getWidth(),picture.getHeight());

            Log.i("PicWidthBefore", picture.getWidth()+"");
            Log.i("PicHeightBefore", picture.getHeight()+"");

            if (size > threshold) {
                int width, height;
                if (picture.getWidth() > picture.getHeight()) {
                    width = threshold;
                    height = (threshold*picture.getHeight()/picture.getWidth());
                    if (height < 816) height = 816;
                } else {
                    width = (threshold*picture.getWidth()/picture.getHeight());
                    height = threshold;
                    if (width < 816) width = 816;
                }
                picture = Bitmap.createScaledBitmap(picture, width, height, true);
            }

            Log.i("PicWidthAfter", picture.getWidth()+"");
            Log.i("PicHeightAfter", picture.getHeight()+"");
        }

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

    public int getExtraIngNum() {
        return extraIngNum;
    }
}
