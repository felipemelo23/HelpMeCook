package br.com.helpmecook.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.Recipe;
import br.com.helpmecook.view.adapter.RecipeIngredientsAdapter;


public class RecipeActivity extends Activity {
    Recipe recipe;
    Manager manager;

    public static final String RECIPE_ID = "recipeID";

    ImageView banner;
    ImageButton addCookBook;
    RatingBar rbTaste, rbDifficulty;
    ListView lvIngredient;
    TextView recipeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id  = extras.getInt(RECIPE_ID);
            recipe = manager.getRecipeById(id);
        } else {
            //O que fazemos se o id não for enviado/recebido??
        }

        banner = (ImageView) findViewById(R.id.banner);
        // por enquanto as receitas nao tem imagens

        addCookBook = (ImageButton) findViewById(R.id.add_cookbook);

        rbTaste = (RatingBar) findViewById(R.id.rb_taste);
        rbTaste.setRating(recipe.getTaste());

        rbDifficulty = (RatingBar) findViewById(R.id.rb_difficulty);
        rbDifficulty.setRating(recipe.getDifficulty());

        lvIngredient = (ListView) findViewById(R.id.lv_ingredient_recipe);
        lvIngredient.setAdapter(new RecipeIngredientsAdapter(this, manager.get));

        recipeText = (TextView) findViewById(R.id.tv_recipe);
        recipeText.setText(recipe.getText());

       // Recipe recipe = RecipeManager.getRecipeById(recipeId);
       // banner.setImageBitmap(recipe.getPicture());
       // recipeName.setText(recipe.getName());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe, menu);
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
}
