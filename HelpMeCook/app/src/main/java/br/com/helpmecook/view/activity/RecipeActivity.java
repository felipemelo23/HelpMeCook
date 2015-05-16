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
import br.com.helpmecook.view.adapter.IngredientsAdapter;


public class RecipeActivity extends Activity {
    long recipeId;
    Recipe recipe;

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
            recipeId  = extras.getInt(RECIPE_ID);
        } else {
            //O que fazemos se o id não for enviado/recebido??
        }

        loadRecipe(recipeId);
    }

    public void loadRecipe(long id) {
        recipe = Manager.getRecipeById(recipeId, this);

        setTitle(recipe.getName());

        banner = (ImageView) findViewById(R.id.banner);
        // por enquanto as receitas nao tem imagens

        addCookBook = (ImageButton) findViewById(R.id.add_cookbook);

        rbTaste = (RatingBar) findViewById(R.id.rb_taste);
        rbTaste.setRating(recipe.getTaste());

        rbDifficulty = (RatingBar) findViewById(R.id.rb_difficulty);
        rbDifficulty.setRating(recipe.getDifficulty());

        lvIngredient = (ListView) findViewById(R.id.lv_ingredient_recipe);
        lvIngredient.setAdapter(new IngredientsAdapter(this, Manager.getRecipeIngredients(recipe.getIngredientList(), this)));

        recipeText = (TextView) findViewById(R.id.tv_recipe);
        recipeText.setText(recipe.getText());
    }

    public boolean addToCookbook() {
        return Manager.addToCookbook(recipe, this);
    }

    public boolean removeFromCookbook() {
        return Manager.removeFromCookbook(recipe, this);
    }

    public boolean classifyTaste(float taste) {
        return Manager.classifyTaste(recipeId, taste);
    }

    public boolean classifyDifficulty(float difficulty) {
        return Manager.classifyDifficulty(recipeId, difficulty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
