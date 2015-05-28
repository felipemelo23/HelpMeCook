package br.com.helpmecook.connection;

import java.util.ArrayList;

import br.com.helpmecook.model.Recipe;

/**
 * Created by Kandarpa on 27/05/2015.
 */
public class NameSearch extends UrlConnection{

    // set your json string url here
    public NameSearch(String name){
        this.UrlStringWS.append("NameSearch.php?name=" + name);
    }

}