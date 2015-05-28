package br.com.helpmecook.connection;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Kandarpa on 27/05/2015.
 */
public class SyncRating extends UrlConnection {
    public SyncRating(ArrayList recipeIds){
        StringBuffer idBuffer = new StringBuffer();
        for (int i = 0; i < recipeIds.size(); i++){
            idBuffer.append(recipeIds.get(i));
            if (i < (recipeIds.size()-1)){
                idBuffer.append(",");
            }
        }
        this.UrlStringWS.append("SyncRating.php?id=" + idBuffer);
    }
}