package br.com.helpmecook.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.view.adapter.RecipesListAdapter;


public class NameSearchResultsActivity extends ActionBarActivity {
    public static final String SEARCH_NAME = "searchName";
    private String searchName;
    private ListView resultRecipes;
    private List<AbstractRecipe> results;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_search_results);

        final EditText etSearchName = (EditText) findViewById(R.id.et_name_search);

        Intent intent = getIntent();

        etSearchName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    EditText etSearchName = (EditText) findViewById(R.id.et_name_search);
                    searchName = etSearchName.getText().toString();
                    if (searchName != null && !searchName.trim().equals("")) {
                        new NameSearchTask().execute();
                        View view = getCurrentFocus();
                        if (view != null) {
                            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Insira um valor para pesquisar", Toast.LENGTH_LONG).show();
                    }
                    handled = true;
                }
                return handled;
            }
        });

        if (intent.hasExtra(SEARCH_NAME)) {
            searchName = intent.getExtras().getString(SEARCH_NAME);
            etSearchName.setText(searchName);
            executeNameSearch();
        }

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
        if (searchName != null && !searchName.trim().equals("")) {
            if (Manager.isOnline(NameSearchResultsActivity.this)) {
                new NameSearchTask().execute();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(NameSearchResultsActivity.this);
                builder.setMessage(getString(R.string.no_connection));
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }else{
            Toast.makeText(getApplicationContext(),getString(R.string.empty_search), Toast.LENGTH_LONG).show();
        }
    }

    private void executeNameSearch() {
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

    private class NameSearchTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NameSearchResultsActivity.this);
            pDialog.setMessage(getString(R.string.searching));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            results = Manager.getResultByRecipeName(searchName);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            pDialog.dismiss();
            executeNameSearch();
        }
    }

}