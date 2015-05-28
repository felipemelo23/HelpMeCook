package br.com.helpmecook.connection;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Kandarpa on 27/05/2015.
 */
public class Classify extends UrlConnection{
    public Classify(long id){
        this.UrlStringWS.append("Classify.php?id=" + id);
    }

    public void ClassifyAll(float tasteEntry, float difficultyEntry){
        this.UrlStringWS.append("&taste=" +tasteEntry + "&difficulty=" + difficultyEntry);
    }

    public void ClassifyTaste(float tasteEntry){
        this.UrlStringWS.append("&taste=" +tasteEntry);
    }

    public void ClassifyDifficulty(float difficultyEntry){
        this.UrlStringWS.append("&difficulty=" + difficultyEntry);
    }
}