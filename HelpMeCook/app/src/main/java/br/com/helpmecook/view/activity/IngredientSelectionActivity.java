package br.com.helpmecook.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

    private ListView lvList;
    private ArrayList<Long> wantedIngredients;
    private ArrayList<Long> unwantedIngredients;
    private List<Ingredient> allIngredients;
    IngredientSelectionList ingredient_data[];
    public List<Integer> clicked;
    public static final String REQUEST_CODE = "Request_code";
    int origin;
    public static final String WANTED_INGREDIENTS = "Wanted_ingredients";
    public static final String UNWANTED_INGREDIENTS = "Unwanted_ingredients";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_selection_activity);
        Intent intent = getIntent();
        origin = intent.getExtras().getInt(REQUEST_CODE);

        allIngredients = Manager.getIngredients(getApplicationContext());

        ingredient_data = new IngredientSelectionList[]{
                        new IngredientSelectionList(R.drawable.teste, "Sorvete de Morango"),
                        new IngredientSelectionList(R.drawable.teste, "Biscoito de Morango"),
                        new IngredientSelectionList(R.drawable.teste, "Recheadinho de Morango"),

        };

        clicked = new ArrayList<Integer>();

        for(int i=0; i<allIngredients.size(); i++){
            clicked.add(0);
        }


        final IngredientSelectionAdapter ingredientSelectionAdapter = new IngredientSelectionAdapter(this, R.layout.item_ingredient, ingredient_data);


        lvList = (ListView)findViewById(R.id.lvListIngredient);

        lvList.setAdapter(ingredientSelectionAdapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(clicked.get(position)== 0){
                    ingredient_data[position].setIcon(R.drawable.teste2);
                    clicked.set(position, 1);
                }
                else if((clicked.get(position)==1)&&(origin == MainActivity.MAIN)){
                    ingredient_data[position].setIcon(R.drawable.teste3);
                    clicked.set(position, 2);
                }
                else{
                    ingredient_data[position].setIcon(R.drawable.teste);
                    clicked.set(position, 0);
                }
                ingredientSelectionAdapter.notifyDataSetChanged();
            }
        });



    }

    public void buttonClick(View view){
        if (origin == MainActivity.MAIN) {
            executeSearch();
        }
        else{
            wantedIngredients = new long[allIngredients.size()];
            Intent intent = getIntent();

            for (int i=0; i<clicked.size(); i++) {
                if (clicked.get(i) == 1) {
                    wantedIngredients[i] = allIngredients.get(i).getId();
                }
            }
            intent.putExtra(WANTED_INGREDIENTS, wantedIngredients);
            setResult(MainActivity.RESULT_OK, intent);
            finish();
        }
    }

    private void executeSearch(){
        wantedIngredients = new ArrayList<Long>(allIngredients.size());
        unwantedIngredients = new ArrayList<Long>(allIngredients.size()-wantedIngredients.size());
        Intent intent = new Intent(getApplicationContext(),IngredientSearchResultActivity.class);

        for (int i=0; i<clicked.size(); i++){
            if (clicked.get(i)==1){
                wantedIngredients.set(i,allIngredients.get(i).getId());
            }
            if (clicked.get(i)==2){
                unwantedIngredients.set(i,allIngredients.get(i).getId());
            }
        }
        if(wantedIngredients.isEmpty()) {
            for (int i=0; i<clicked.size(); i++){
                if (clicked.get(i) != 2){
                    wantedIngredients.set(i,allIngredients.get(i).getId());
                }
            }
        }

        intent.putExtra(WANTED_INGREDIENTS, wantedIngredients);
        intent.putExtra(UNWANTED_INGREDIENTS, unwantedIngredients);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                            wantedIngredients.set(i,allIngredients.get(i).getId());
                        }
                    }
                    intent.putExtra(WANTED_INGREDIENTS, wantedIngredients);
                    setResult(MainActivity.RESULT_OK, intent);
                    finish();
                }
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
