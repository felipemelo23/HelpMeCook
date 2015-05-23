package br.com.helpmecook.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.model.Recipe;
import br.com.helpmecook.view.adapter.IngredientsAdapter;

public class RecipeRegisterActivity extends ActionBarActivity {

    private static final String REGISTER_RECIPE = "RegisterRecipe";
    private static final int SELECT_INGREDIENT = 2;
    private static final int SELECT_PICTURE = 1;
    private static final String CURRENT_INGREDIENTS = "CURRENT_INGREDIENTS";
    private static final String RECIPE_NAME_KEY = "NAME";
    private static final String INGREDIENTS_KEY = "INGREDIENTS";
    private static final String PREPARE_TIME_KEY = "PREPARE_TIME";
    private static final String PORTION_KEY = "PORTION_KEY";
    private static final String DESCRIPTION_KEY = "DESCRIPTION";
    private static final String PICTURE_KEY = "PICTURE";
    private Recipe recipe;
    private ImageButton ibRecipePicture;
    private EditText etName;
    private ListView lvIngredients;
    private EditText etPrepTime;
    private EditText etPortionNum;
    private EditText etDescription;
    private Bitmap picture;
    private int ingredients[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_register);

        ingredients = new int[0];
        ibRecipePicture = (ImageButton) findViewById(R.id.ib_register_recipe);
        etName = (EditText) findViewById(R.id.et_recipe_register_name);
        lvIngredients = (ListView) findViewById(R.id.lv_recipe_register_ingredients);
        etPrepTime = (EditText) findViewById(R.id.et_recipe_register_preparation_time);
        etPortionNum = (EditText) findViewById(R.id.et_recipe_register_portions_number);
        etDescription = (EditText) findViewById(R.id.et_recipe_register_description);

        //Abre a intent para selecionar a foto da receita
        //por enquanto só abre o gerenciador de arquivos, não a camera
        ibRecipePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PICTURE);
            }
        });

        lvIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent selectIngredientsIntent = new Intent(getApplicationContext(), IngredientSelectionActivity.class);
                selectIngredientsIntent.putExtra(CURRENT_INGREDIENTS, ingredients);
                selectIngredientsIntent.putExtra(IngredientSelectionActivity.REQUEST_CODE, REGISTER_RECIPE);
                startActivityForResult(selectIngredientsIntent, SELECT_INGREDIENT);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Tem que fazer essa funcao
        outState.putString(RECIPE_NAME_KEY, etName.getText().toString());
        outState.putIntArray(INGREDIENTS_KEY, ingredients);
        outState.putString(PREPARE_TIME_KEY, etPrepTime.getText().toString());
        outState.putString(PORTION_KEY, etPortionNum.getText().toString());
        outState.putString(DESCRIPTION_KEY, etDescription.getText().toString());

        int bytes = picture.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        picture.copyPixelsToBuffer(buffer);
        byte[] array = buffer.array();

        outState.putByteArray(PICTURE_KEY, array);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // e essa
        super.onRestoreInstanceState(savedInstanceState);
        etName.setText(savedInstanceState.getString(RECIPE_NAME_KEY));
        ingredients = savedInstanceState.getIntArray(INGREDIENTS_KEY);
        etPrepTime.setText(savedInstanceState.getString(PREPARE_TIME_KEY));
        etPortionNum.setText(savedInstanceState.getString(PORTION_KEY));
        etDescription.setText(savedInstanceState.getString(DESCRIPTION_KEY));

        byte[] array = savedInstanceState.getByteArray(PICTURE_KEY);
        ByteBuffer buffer = ByteBuffer.wrap(array);
        picture.copyPixelsFromBuffer(buffer);
    }

    public void registerRecipe() {
        recipe = new Recipe();
        if ((etName.getText().toString().equals(null)) || (etDescription.getText().toString().equals(null))){ // + lista de ingredientes
            Toast.makeText(RecipeRegisterActivity.this, getResources().getString(R.string.blank_fields), Toast.LENGTH_LONG).show();
        } else {
            recipe.setName(etName.getText().toString());
            recipe.setText(etDescription.getText().toString());

            if (picture.equals(null)){
                recipe.setPicture(BitmapFactory.decodeResource((RecipeRegisterActivity.this).getResources(),R.drawable.plate));
            } else {
                recipe.setPicture(picture);
            }
            if (!(etPrepTime.getText().toString().equals(null))) {
                recipe.setEstimatedTime(Integer.parseInt(etPrepTime.getText().toString()));
            }
            if (!(etPortionNum.getText().toString().equals(null))) {
                recipe.setPortionNum(etPortionNum.getText().toString());
            }

            Manager.registerRecipe(recipe, RecipeRegisterActivity.this);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_recipe_register) {
            registerRecipe();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode != RESULT_OK) {
            picture = null;
            // o que faz se a foto escolhida nao for recebida?? Só um Toast avisando?
        } else {
            switch (requestCode) {
                case SELECT_PICTURE:
                    Cursor cursor = getContentResolver().query(intent.getData(), null,
                            null, null, null);
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(ImageColumns.DATA);
                    String fileSrc = cursor.getString(idx);
                    picture = BitmapFactory.decodeFile(fileSrc);
                    break;
                case SELECT_INGREDIENT:
                    ingredients = intent.getExtras().getIntArray(INGREDIENTS_KEY);
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "De onde voc� veio, meu amigo?", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
