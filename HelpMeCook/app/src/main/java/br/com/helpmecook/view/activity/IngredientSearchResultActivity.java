package br.com.helpmecook.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.view.adapter.RecipesListAdapter;

public class IngredientSearchResultActivity extends ActionBarActivity {

    public static final int RESULT_RECIPE = 1;

    private ListView resultRecipes;
    private ListView resultRecipesPlus;
    private List<AbstractRecipe> results;
    private List<AbstractRecipe> plus;
    private List<Long> wanted;
    private List<Long> unwanted;

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_search_result);

        Intent intent = getIntent();

        results = new ArrayList<>();
        plus = new ArrayList<>();

        wanted = (List<Long>) intent.getSerializableExtra(IngredientSelectionActivity.WANTED_INGREDIENTS);
        unwanted = (List<Long>) intent.getSerializableExtra(IngredientSelectionActivity.UNWANTED_INGREDIENTS);

        if (Manager.isOnline(IngredientSearchResultActivity.this)) {
            new IngredientSearchTask().execute();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(IngredientSearchResultActivity.this);
            builder.setMessage(getString(R.string.no_connection));
            builder.setNeutralButton("Ok", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void showResults(){
        if (results != null && plus != null) {
            RecipesListAdapter resultsAdapter = new RecipesListAdapter(getApplicationContext(), results);
            RecipesListAdapter plusAdapter = new RecipesListAdapter(getApplicationContext(), plus);

            if (results.size() == 0 && plus.size() == 0){
                Toast.makeText(getApplicationContext(),getString(R.string.no_results), Toast.LENGTH_LONG).show();
            }

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

            setListViewHeightBasedOnChildren(resultRecipes);
            setListViewHeightBasedOnChildren(resultRecipesPlus);

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(IngredientSearchResultActivity.this);
            builder.setMessage(getString(R.string.cant_search));
            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    public void showRecipe(long id) {
        Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
        intent.putExtra(RecipeActivity.RECIPE_ID, id);

        startActivityForResult(intent, RESULT_RECIPE);
    }

    private class IngredientSearchTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(IngredientSearchResultActivity.this);
            pDialog.setMessage(getString(R.string.searching));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            List<Ingredient> wantedIng = Manager.getRecipeIngredients(wanted,getApplicationContext());
            List<Ingredient> unwantedIng = Manager.getRecipeIngredients(unwanted,getApplicationContext());
            Pair<List<AbstractRecipe>,List<AbstractRecipe>> lists = Manager.getResultByIngredientLists(wantedIng, unwantedIng);
            results = lists.first;
            plus = lists.second;

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            pDialog.dismiss();
            showResults();
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
