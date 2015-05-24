package br.com.helpmecook.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Recipe;
import br.com.helpmecook.view.adapter.RecipesListAdapter;


public class NameSearchResultsActivity extends ActionBarActivity {
    public static final String SEARCH_NAME = "searchName";
    private String searchName;
    private ListView resultRecipes;
    private List<AbstractRecipe> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_search_results);

        Intent intent = getIntent();

        searchName = intent.getExtras().getString(SEARCH_NAME);

        EditText etSearchName = (EditText) findViewById(R.id.et_name_search);
        etSearchName.setText(searchName);

        executeNameSearch();
        //lvRecipes = (ListView) findViewById(R.id.lvRecipes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_name_search_results, menu);
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

    public void actionExecuteSearch(View view) {
        EditText etSearchName = (EditText) findViewById(R.id.et_name_search);
        searchName = etSearchName.getText().toString();
        executeNameSearch();
    }

    private void executeNameSearch() {
        results = Manager.getResultByRecipeName(searchName);

        RecipesListAdapter adapter = new RecipesListAdapter(getApplicationContext(), results);

        resultRecipes = (ListView) findViewById(R.id.lv_name_search_result);
        resultRecipes.setAdapter(adapter);

        resultRecipes.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showRecipe(results.get(position).getId());
                }
            }
        );
    }

    public void showRecipe(long id) {
        Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
        intent.putExtra(RecipeActivity.RECIPE_ID, id);

        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putStringArrayList(ALUNOS_KEY, (ArrayList<Aluno>) listaAlunos);
        super.onSaveInstanceState(outState);
//        Log.i(TAG, "onSaveInstanceState(): " + listRecipe);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //listaAlunos = savedInstanceState.getStringArrayList(ALUNOS_KEY);
//        Log.i(TAG, "onRestoreInstanceState(): " + listRecipe);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        this.loadList();
    }
}