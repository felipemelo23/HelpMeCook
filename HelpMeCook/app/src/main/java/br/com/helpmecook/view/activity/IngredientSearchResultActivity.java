package br.com.helpmecook.view.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.view.adapter.RecipesListAdapter;


public class IngredientSearchResultActivity extends ActionBarActivity {
    private ListView resultRecipes;
    private List<AbstractRecipe> results;
    private List<AbstractRecipe> plus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_search_result);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ingredient_search_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showResults(List<Ingredient> wanted, List<Ingredient> unwanted){
        //getResultByIngredientLists(wanted, unwanted);
        //getPlusByIngredientLists(wanted, unwanted);

        RecipesListAdapter adapter = new RecipesListAdapter(getApplicationContext(), results);
        RecipesListAdapter adapter2 = new RecipesListAdapter(getApplicationContext(), plus);


    }
    public void showRecipe(long id) {
        Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
        intent.putExtra(RecipeActivity.RECIPE_ID, id);

        startActivity(intent);
    }
}
