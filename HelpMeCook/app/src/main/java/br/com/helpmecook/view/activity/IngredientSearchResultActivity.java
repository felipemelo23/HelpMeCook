package br.com.helpmecook.view.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.view.adapter.RecipesListAdapter;


public class IngredientSearchResultActivity extends ActionBarActivity {
    private ListView resultRecipes;
    private ListView resultRecipesPlus;
    private List<AbstractRecipe> results;
    private List<AbstractRecipe> plus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_search_result);

        Intent intent = getIntent();

        List<Long> wanted = (List<Long>) intent.getSerializableExtra(IngredientSelectionActivity.WANTED_INGREDIENTS);
        List<Long> unwanted = (List<Long>) intent.getSerializableExtra(IngredientSelectionActivity.UNWANTED_INGREDIENTS);

        /*for (int i = 0; i < wantedId.length; i++) {
            wanted.add(wantedId[i]);
        }
        for (int i = 0; i < unwantedId.length; i++) {
            unwanted.add(unwantedId[i]);
        }*/

        showResults(Manager.getRecipeIngredients(wanted,getApplicationContext()),
                    Manager.getRecipeIngredients(unwanted,getApplicationContext()));
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
        results = Manager.getResultByIngredientLists(wanted, unwanted);
        plus = Manager.getPlusByIngredientLists(wanted, unwanted);

        if (results != null && plus != null) {
            RecipesListAdapter resultsAdapter = new RecipesListAdapter(getApplicationContext(), results);
            RecipesListAdapter plusAdapter = new RecipesListAdapter(getApplicationContext(), plus);

            resultRecipes = (ListView) findViewById(R.id.lvRecipes);
            resultRecipes.setAdapter(resultsAdapter);

            resultRecipesPlus = (ListView) findViewById(R.id.lvRecipesPlus);
            resultRecipesPlus.setAdapter(plusAdapter);


            resultRecipes.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            showRecipe(results.get(position).getId());
                        }
                    }
            );

            resultRecipesPlus.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            showRecipe(plus.get(position).getId());
                        }
                    }
            );
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.cant_search), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    public void showRecipe(long id) {
        Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
        intent.putExtra(RecipeActivity.RECIPE_ID, id);

        startActivity(intent);
    }
}
