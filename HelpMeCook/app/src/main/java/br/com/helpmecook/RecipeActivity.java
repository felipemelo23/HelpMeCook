package br.com.helpmecook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import br.com.helpmecook.control.RecipeManager;
import br.com.helpmecook.model.Recipe;


public class RecipeActivity extends Activity {

    public static final String RECIPE_ID = "recipeID";

    ImageView banner;
    ImageButton addCookBook;
    RatingBar rbTaste, rbDifficulty;
    ListView lvIngredient;
    TextView recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        banner = (ImageView) findViewById(R.id.banner);
        addCookBook = (ImageButton) findViewById(R.id.add_cookbook);
        rbTaste = (RatingBar) findViewById(R.id.rb_taste);
        rbDifficulty = (RatingBar) findViewById(R.id.rb_difficulty);
        lvIngredient = (ListView) findViewById(R.id.lv_ingredient_recipe);
        recipe = (TextView) findViewById(R.id.tv_recipe);

        Intent intent = getIntent();
        int recipeId = intent.getExtras().getInt(RECIPE_ID);
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
