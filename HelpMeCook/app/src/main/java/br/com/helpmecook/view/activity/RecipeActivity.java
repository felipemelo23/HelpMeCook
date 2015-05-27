package br.com.helpmecook.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.Recipe;
import br.com.helpmecook.view.adapter.IngredientsAdapter;


public class RecipeActivity extends ActionBarActivity {
    private long recipeId;
    private Recipe recipe;

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
            recipeId  = extras.getLong(RECIPE_ID);
        } else {
            //O que fazemos se o id não for enviado/recebido??
        }

        loadRecipe(recipeId);
    }

    public void loadRecipe(long id) {
        recipe = Manager.getRecipeById(id, this);

        setTitle(recipe.getName());

        banner = (ImageView) findViewById(R.id.banner);
        banner.setImageBitmap(recipe.getPicture());

        addCookBook = (ImageButton) findViewById(R.id.add_cookbook);
        // Se a receita já estiver no cookbook, o botão de adicionar vira de remover
        if (Manager.isOnCookbook(recipe, RecipeActivity.this)) {
            addCookBook.setImageDrawable(getResources().getDrawable(R.drawable.cookbook_minus));
        }
        addCookBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Manager.isOnCookbook(recipe, RecipeActivity.this)) {
                    removeFromCookbook();
                } else {
                    addToCookbook();
                }
            }
        });

        rbTaste = (RatingBar) findViewById(R.id.rb_taste);
        rbTaste.setRating(recipe.getTaste());
        rbTaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classifyTaste();
            }
        });

        rbDifficulty = (RatingBar) findViewById(R.id.rb_difficulty);
        rbDifficulty.setRating(recipe.getDifficulty());
        rbDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classifyDifficulty();
            }
        });

        lvIngredient = (ListView) findViewById(R.id.lv_ingredient_recipe);
        lvIngredient.setAdapter(new IngredientsAdapter(this, Manager.getRecipeIngredients(recipe.getIngredientList(), this)));
        setListViewHeightBasedOnChildren(lvIngredient);


        recipeText = (TextView) findViewById(R.id.tv_recipe);
        recipeText.setText(recipe.getText());
    }

    public boolean addToCookbook() {
        boolean result = Manager.addToCookbook(recipe, RecipeActivity.this);
        if (result) {
            addCookBook.setImageDrawable(getResources().getDrawable(R.drawable.cookbook_minus));
        }
        return result;
    }

    public boolean removeFromCookbook() {
        boolean result = Manager.removeFromCookbook(recipe, RecipeActivity.this);
        if (result) {
            addCookBook.setImageDrawable(RecipeActivity.this.getResources().getDrawable(R.drawable.cookbook_plus));
        }
        return result;
    }

    public void classifyTaste() {
        //final boolean result;

        AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);

        View view = getLayoutInflater().inflate(R.layout.dialog_classify, null);
        builder.setView(view);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rb_classify);


        builder.setTitle(RecipeActivity.this.getResources().getString(R.string.classify_taste));
        builder.setNegativeButton(RecipeActivity.this.getResources().getString(R.string.cancel),
                new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                //result = false;
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                //result = Manager.classifyTaste(recipeId, ratingBar.getRating());
                Manager.classifyTaste(recipeId, ratingBar.getRating());
                dialog.dismiss();
            }
        });

       builder.create();
        //return result;
    }

    public void classifyDifficulty() {
        //final boolean result;

        AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);

        View view = getLayoutInflater().inflate(R.layout.dialog_classify, null);
        builder.setView(view);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rb_classify);


        builder.setTitle(RecipeActivity.this.getResources().getString(R.string.classify_difficulty));
        builder.setNegativeButton(RecipeActivity.this.getResources().getString(R.string.cancel),
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        //result = false;
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
               // result = Manager.classifyDifficulty(recipeId, ratingBar.getRating());
                Manager.classifyDifficulty(recipeId, ratingBar.getRating());
                dialog.dismiss();
            }
        });

        builder.create();
        //return result;
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
