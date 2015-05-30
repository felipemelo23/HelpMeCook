package br.com.helpmecook.view.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.sqlite.IngredientDAO;
import br.com.helpmecook.view.adapter.IngredientSelectionAdapter;


/**
 * Created by mariana on 08/05/15.
 */
public class IngredientSelectionActivity extends ActionBarActivity {

    private ListView lvList;
    IngredientSelectionAdapter ingredientSelectionAdapter;
    private ArrayList<Long> wantedIngredients;
    private ArrayList<Long> unwantedIngredients;
    private List<Ingredient> allIngredients;
    public List<Integer> clicked;
    public static final String REQUEST_CODE = "Request_code";
    int origin;
    public static final String WANTED_INGREDIENTS = "Wanted_ingredients";
    public static final String UNWANTED_INGREDIENTS = "Unwanted_ingredients";

    EditText search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_selection_activity);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        Log.i("IngredientSelection", actionBar.toString());

        //actionBar.setCustomView(R.layout.actionbar_view);

        //search = (EditText) actionBar.getCustomView().findViewById(R.id.searchfield);

        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

        Intent intent = getIntent();
        origin = intent.getExtras().getInt(REQUEST_CODE);

        allIngredients = Manager.getIngredients(getApplicationContext());

        if (allIngredients == null) {
            allIngredients = new ArrayList<Ingredient>();
        }

        clicked = new ArrayList<Integer>();

        for(int i=0; i<allIngredients.size(); i++){
            clicked.add(0);
        }

        if (origin == RecipeRegisterActivity.REGISTER_RECIPE) {
            long ingredients[] = intent.getLongArrayExtra(RecipeRegisterActivity.CURRENT_INGREDIENTS);
            if (ingredients != null && ingredients.length > 0) {
                for (int i = 0; i < allIngredients.size(); i++) {
                    for (int j = 0; j < ingredients.length; j++) {
                        if (allIngredients.get(i).getId() == ingredients[j]){
                            clicked.set(i,1);
                            allIngredients.get(i).setIconPath(R.drawable.checkbox_yellow);
                        }
                    }
                }
            }
        }

        ingredientSelectionAdapter = new IngredientSelectionAdapter(this, R.layout.item_ingredient, allIngredients);

        lvList = (ListView)findViewById(R.id.lvListIngredient);

        lvList.setAdapter(ingredientSelectionAdapter);

        lvList.setFastScrollEnabled(true);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (clicked.get(position) == 0) {
                    allIngredients.get(position).setIconPath(R.drawable.checkbox_yellow);
                    clicked.set(position, 1);
                } else if ((clicked.get(position) == 1) && (origin == MainActivity.MAIN)) {
                    allIngredients.get(position).setIconPath(R.drawable.close_circle);
                    clicked.set(position, 2);
                } else {
                    allIngredients.get(position).setIconPath(R.drawable.checkbox_blank_circle);
                    clicked.set(position, 0);
                }
                ingredientSelectionAdapter.notifyDataSetChanged();
            }
        });

        /*search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                IngredientSelectionActivity.this.ingredientSelectionAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, getIntent());
        finish();
        super.onBackPressed();
    }

    private void executeSearch(){
        wantedIngredients = new ArrayList<Long>();
        unwantedIngredients = new ArrayList<Long>();
        Intent intent = new Intent(getApplicationContext(),IngredientSearchResultActivity.class);

        for (int i=0; i<clicked.size(); i++){
            if (clicked.get(i)==1){
                wantedIngredients.add(allIngredients.get(i).getId());
            }
            if (clicked.get(i)==2){
                unwantedIngredients.add(allIngredients.get(i).getId());
            }
        }
        if(wantedIngredients.isEmpty()) {
            for (int i=0; i<clicked.size(); i++){
                if (clicked.get(i) != 2){
                    wantedIngredients.add(allIngredients.get(i).getId());
                }
            }
        }

        intent.putExtra(WANTED_INGREDIENTS, wantedIngredients);
        intent.putExtra(UNWANTED_INGREDIENTS, unwantedIngredients);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ingredient_selection_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_ok:
                //abaixo o conteúdo que estava na finada função buttonClick
                if (origin == MainActivity.MAIN) {
                    executeSearch();
                }
                else{
                    wantedIngredients = new ArrayList<Long>(allIngredients.size());
                    Intent intent = getIntent();

                    for (int i=0; i<clicked.size(); i++) {
                        if (clicked.get(i) == 1) {
                            wantedIngredients.add(allIngredients.get(i).getId());
                        }
                    }

                    long wantedId[] = new long[wantedIngredients.size()];
                    for (int i=0; i<wantedId.length;i++) {
                        wantedId[i] = wantedIngredients.get(i);
                    }
                    intent.putExtra(WANTED_INGREDIENTS, wantedId);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return false;
            case R.id.menu_sugestion:
                AlertDialog.Builder alert = new AlertDialog.Builder(IngredientSelectionActivity.this);
                alert.setTitle("Sugira um ingrediente");

                final EditText input = new EditText(IngredientSelectionActivity.this);
                alert.setView(input);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String srt = input.getEditableText().toString();
                        //!!!!tem que armazenar essa string no banco de dados!! !!
                        Toast.makeText(IngredientSelectionActivity.this,srt,Toast.LENGTH_LONG).show();
                    }
                });
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
