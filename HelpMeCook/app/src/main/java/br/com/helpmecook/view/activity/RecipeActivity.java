package br.com.helpmecook.view.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.Recipe;
import br.com.helpmecook.view.adapter.IngredientsAdapter;
import br.com.helpmecook.view.dialog.ImageDialog;

public class RecipeActivity extends ActionBarActivity {

    public static final String RECIPE_ID = "recipeID";
    public static final String REQUEST_CODE = "Request_code";

    private long recipeId;
    private Recipe recipe;
    private int origin;
    private ImageView banner;
    private ImageButton addCookBook;
    private RatingBar rbTaste, rbDifficulty;
    private ListView lvIngredient;
    private TextView recipeText;
    private TextView recipePrepTime;
    private TextView recipePortionNumber;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recipeId = extras.getLong(RECIPE_ID);
        } else {
            AlertDialog dialog = createDialog(getString(R.string.error_open_recipe));
            dialog.show();
        }

        if (Manager.isOnline(RecipeActivity.this)) {
            GetRecipeTask grt = new GetRecipeTask();
            grt.execute();
            try {
                grt.get(9999, TimeUnit.MILLISECONDS);//requisito não funcional, tudo com internet em menos de 10s
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            recipe = Manager.getRecipeOnLocalDB(recipeId, RecipeActivity.this);
            if (recipe == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);
                builder.setMessage(getString(R.string.no_connection));
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                loadRecipe();
            }
        }
    }

    private AlertDialog createDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);
        builder.setMessage(message);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private void launchImageDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        View imageDialogView = factory.inflate(R.layout.dialog_image, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);
        final AlertDialog dialog = builder.create();

        ImageView fullBannerImage = (ImageView) imageDialogView.findViewById(R.id.fullBannerImage);
        fullBannerImage.setImageBitmap(recipe.getPicture());


        fullBannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void loadRecipe() {
        if (recipe != null) {
            setTitle(recipe.getName());

            banner = (ImageView) findViewById(R.id.banner);
            banner.setImageBitmap(recipe.getPicture());

            banner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageDialog dialog = new ImageDialog(RecipeActivity.this, recipe.getPicture());
                    dialog.show();
                    launchImageDialog();
                }
            });

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
            rbTaste.setClickable(true);
            rbTaste.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        classifyTaste();
                        return true;
                    }
                    return false;
                }
            });

            rbDifficulty = (RatingBar) findViewById(R.id.rb_difficulty);
            rbDifficulty.setRating(recipe.getDifficulty());
            rbDifficulty.setClickable(true);
            rbDifficulty.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        classifyDifficulty();
                        return true;
                    }
                    return false;
                }
            });

            lvIngredient = (ListView) findViewById(R.id.lv_ingredient_recipe);
            lvIngredient.setEnabled(false);
            lvIngredient.setAdapter(new IngredientsAdapter(this, Manager.getRecipeIngredients(recipe.getIngredientList(), this), recipe.getUnits()));
            setListViewHeightBasedOnChildren(lvIngredient);

            recipeText = (TextView) findViewById(R.id.tv_recipe);
            recipeText.setText(recipe.getText());
            Log.i("NAME LOSS", "RecipeAct name: " + recipe.getText());

            recipePrepTime = (TextView) findViewById(R.id.tv_prep_time);
            if (recipe.getEstimatedTime() == 1) {
                recipePrepTime.setText(recipe.getEstimatedTime() + " minuto");
            } else if (recipe.getEstimatedTime() > 0) {
                recipePrepTime.setText(recipe.getEstimatedTime() + " minutos");
            } else {
                ((ImageView) findViewById(R.id.iv_prep_time)).setVisibility(View.GONE);
                recipePrepTime.setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.ll_prep_time)).setVisibility(View.GONE);
            }

            recipePortionNumber = (TextView) findViewById(R.id.tv_portion_number);
            if (recipe.getPortionNum() != null && !recipe.getPortionNum().isEmpty() && Integer.parseInt(recipe.getPortionNum()) == 1) {
                recipePortionNumber.setText(recipe.getPortionNum() + " porção");
            } else if (recipe.getPortionNum() != null && !recipe.getPortionNum().isEmpty()) {
                recipePortionNumber.setText(recipe.getPortionNum() + " porções");
            } else {
                ((ImageView) findViewById(R.id.iv_portion_num)).setVisibility(View.GONE);
                recipePortionNumber.setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.ll_portion_num)).setVisibility(View.GONE);
            }
        } else {
            AlertDialog dialog = createDialog(getString(R.string.error_open_recipe));
            dialog.show();
        }
    }

    public boolean addToCookbook() {
        boolean result = Manager.addToCookbook(recipe, RecipeActivity.this);
        Log.i("RecipeActivity", "Add Cookbook Pressed");
        if (result) {
            Log.i("RecipeActivity", "Added to Cookbook");
            Toast.makeText(getApplicationContext(), "Receita adicionada no Cookbook", Toast.LENGTH_SHORT).show();
            addCookBook.setImageDrawable(getResources().getDrawable(R.drawable.cookbook_minus));
        }
        return result;
    }

    public boolean removeFromCookbook() {
        boolean result = Manager.removeFromCookbook(recipe, RecipeActivity.this);
        Log.i("RecipeActivity", "Remove Cookbook Pressed");
        if (result) {
            Log.i("RecipeActivity", "Removed from Cookbook");
            Toast.makeText(getApplicationContext(), "Receita removida do Cookbook", Toast.LENGTH_SHORT).show();
            addCookBook.setImageDrawable(RecipeActivity.this.getResources().getDrawable(R.drawable.cookbook_plus));
        }
        return result;
    }

    public void classifyTaste() {

        AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);

        View view = getLayoutInflater().inflate(R.layout.dialog_classify, null);
        builder.setView(view);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rb_classify);

        builder.setMessage(RecipeActivity.this.getResources().getString(R.string.classify_taste));
        builder.setNegativeButton(RecipeActivity.this.getResources().getString(R.string.cancel),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                recipe.setTaste(ratingBar.getRating());
                dialog.dismiss();

                if (Manager.isOnline(RecipeActivity.this)) {
                    new ClassifyRecipeTaste().execute();
                } else {
                    AlertDialog dialog2 = createDialog(getString(R.string.no_connection));
                    dialog2.show();
                }

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void classifyDifficulty() {

        AlertDialog.Builder builder = new AlertDialog.Builder(RecipeActivity.this);

        View view = getLayoutInflater().inflate(R.layout.dialog_classify, null);
        builder.setView(view);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rb_classify);

        builder.setMessage(RecipeActivity.this.getResources().getString(R.string.classify_difficulty));
        builder.setNegativeButton(RecipeActivity.this.getResources().getString(R.string.cancel),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                recipe.setDifficulty(ratingBar.getRating());
                dialog.dismiss();

                if (Manager.isOnline(RecipeActivity.this)) {
                    new ClassifyRecipeDifficulty().execute();
                } else {
                    AlertDialog dialog2 = createDialog(getString(R.string.no_connection));
                    dialog2.show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getIntent().getExtras().getInt(REQUEST_CODE) == 1) {
                startActivity(new Intent(RecipeActivity.this, MainActivity.class));
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetRecipeTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RecipeActivity.this);
            pDialog.setMessage(getString(R.string.loading_recipe));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            recipe = Manager.getRecipeById(recipeId, getApplicationContext());
            Log.i("RecipeLists", recipe.getIngredientNum()+"");
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            pDialog.dismiss();
            loadRecipe();
        }
    }

    private class ClassifyRecipeTaste extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RecipeActivity.this);
            pDialog.setMessage(getString(R.string.classifing_recipe));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Manager.classifyTaste(recipeId, recipe.getTaste());
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            pDialog.dismiss();
        }
    }

    private class ClassifyRecipeDifficulty extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RecipeActivity.this);
            pDialog.setMessage(getString(R.string.classifing_recipe));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Manager.classifyDifficulty(recipeId, recipe.getDifficulty());
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            pDialog.dismiss();
        }
    }
}
