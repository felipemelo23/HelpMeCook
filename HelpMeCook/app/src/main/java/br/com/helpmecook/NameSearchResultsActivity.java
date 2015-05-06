package br.com.helpmecook;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import br.com.helpmecook.model.Recipe;


public class NameSearchResultsActivity extends ActionBarActivity {
    private ListView lvRecipes;
    private List<Recipe> listRecipe;
    private ArrayAdapter<Recipe> adapter;
    private int adapterLayout = android.R.layout.simple_list_item_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_search_results);
        lvRecipes = (ListView) findViewById(R.id.lvRecipes);
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


    private void loadList() {
        recipeDAO dao = new recipeDAO(this);
        this.listRecipe = dao.listar();
        dao.close();

        this.adapter = new ArrayAdapter<Recipe>(this, adapterLayout, listRecipe);
        this.lvRecipes.setAdapter(adapter);
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
        this.loadList();
    }
}