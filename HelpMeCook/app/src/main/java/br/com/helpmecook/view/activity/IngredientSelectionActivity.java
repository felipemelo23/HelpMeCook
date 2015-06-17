package br.com.helpmecook.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.view.adapter.IngredientSelectionAdapter;


/**
 * Created by mariana on 08/05/15.
 */
public class IngredientSelectionActivity extends ActionBarActivity {

    private static final int MAX_INGREDIENTS = 300;
    private ListView lvList;
    IngredientSelectionAdapter ingredientSelectionAdapter;
    private ArrayList<Long> wantedIngredients;
    private ArrayList<Long> unwantedIngredients;
    private List<Ingredient> allIngredients;
    public int[] clicked;
    public static final String REQUEST_CODE = "Request_code";
    int origin;
    public static final String WANTED_INGREDIENTS = "Wanted_ingredients";
    public static final String UNWANTED_INGREDIENTS = "Unwanted_ingredients";

    EditText search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_selection_activity);

        Intent intent = getIntent();
        origin = intent.getExtras().getInt(REQUEST_CODE);

        allIngredients = Manager.getIngredients(getApplicationContext());

        if (allIngredients == null) {
            allIngredients = new ArrayList<Ingredient>();
        }

        clicked = new int[MAX_INGREDIENTS];
        for(int i=0; i<allIngredients.size(); i++){
            clicked[(int)allIngredients.get(i).getId()] = 0;
        }

        if (origin == RecipeRegisterActivity.REGISTER_RECIPE) {
            long ingredients[] = intent.getLongArrayExtra(RecipeRegisterActivity.CURRENT_INGREDIENTS);
            if (ingredients != null && ingredients.length > 0) {
                for (int i = 0; i<ingredients.length; i++){
                    clicked[(int) ingredients[i]] = 1;
                    for (int j = 0; j <  allIngredients.size(); j++) {
                        if (allIngredients.get(j).getId() == ingredients[i]){
                            allIngredients.get(j).setIconPath(R.drawable.checkbox_yellow);
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
                Ingredient ing = (Ingredient) lvList.getItemAtPosition(position);
                if (clicked[(int)ing.getId()] == 0) {
                    for (int j = 0; j <  allIngredients.size(); j++) {
                        if (allIngredients.get(j).getId() == ing.getId()){
                            allIngredients.get(j).setIconPath(R.drawable.checkbox_yellow);
                        }
                    }
                    clicked[(int) ing.getId()] = 1;
                } else if ((clicked[(int)ing.getId()] == 1) && (origin == MainActivity.MAIN)) {
                    for (int j = 0; j <  allIngredients.size(); j++) {
                        if (allIngredients.get(j).getId() == ing.getId()){
                            allIngredients.get(j).setIconPath(R.drawable.close_circle);
                        }
                    }
                    clicked[(int) ing.getId()] = 2;
                } else {
                    for (int j = 0; j <  allIngredients.size(); j++) {
                        if (allIngredients.get(j).getId() == ing.getId()){
                            allIngredients.get(j).setIconPath(R.drawable.checkbox_blank_circle);
                        }
                    }
                    clicked[(int) ing.getId()] = 0;
                }
                ingredientSelectionAdapter.notifyDataSetChanged();
            }
        });

        search = (EditText) findViewById(R.id.inputSearch);
        //search.setVisibility(View.GONE);
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                (IngredientSelectionActivity.this).ingredientSelectionAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) { }
        });
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

        for (int i=0; i<clicked.length; i++){
            if (clicked[i]==1){
                wantedIngredients.add((long)i);
            }
            if (clicked[i]==2){
                unwantedIngredients.add((long)i);
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
                    if (Manager.isOnline(IngredientSelectionActivity.this)) {
                        executeSearch();
                    } else {
                        AlertDialog dialog = createDialog(getString(R.string.no_connection));
                        dialog.show();
                    }
                }
                else{
                    wantedIngredients = new ArrayList<Long>(allIngredients.size());
                    Intent intent = getIntent();

                    for (int i=0; i<clicked.length; i++) {
                        if (clicked[i] == 1) {
                            wantedIngredients.add((long) i);
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
                if (Manager.isOnline(IngredientSelectionActivity.this)) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(IngredientSelectionActivity.this);
                    alert.setTitle("Sugira um ingrediente");

                    final EditText input = new EditText(IngredientSelectionActivity.this);
                    alert.setView(input);

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            String srt = input.getEditableText().toString();
                            //!!!!tem que armazenar essa string no banco de dados!! !!
                        }
                    });
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                } else {
                    AlertDialog dialog = createDialog(getString(R.string.no_connection));
                    dialog.show();
                }
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog createDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(IngredientSelectionActivity.this);
        builder.setMessage(message);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
