package br.com.helpmecook.view.activity;

/**
 * Created by mariana on 16/05/15.
 */
public class IngredientSelectionList {

    public int icon;
    public String title;
    public IngredientSelectionList(){
        super();
    }

    public IngredientSelectionList(int icon, String title) {
        super();
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
