package br.com.helpmecook.connection;

import java.util.ArrayList;

import br.com.helpmecook.model.Ingredient;

/**
 * Created by Kandarpa on 27/05/2015.
 */
public class IngredientSearch extends UrlConnection {


    // set your json string url here
    public IngredientSearch(ArrayList<Ingredient> ingIn, ArrayList<Ingredient> ingOut){
        StringBuffer inBuffer = new StringBuffer();
        for (int i = 0; i<ingIn.size(); i++){
            inBuffer.append(ingIn.get(i).getName());
            if (i < (ingIn.size()-1)){
                inBuffer.append(",");
            }
        }
        this.UrlStringWS.append("IngredientSearch.php?in=" + inBuffer);

        StringBuffer outBuffer = new StringBuffer();
        for (int i = 0; i<ingIn.size(); i++){
            outBuffer.append(ingIn.get(i).getName());
            if (i < (ingIn.size()-1)){
                outBuffer.append(",");
            }
        }
        this.UrlStringWS.append("&out=" + outBuffer);
    }
}