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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.Recipe;

public class RecipeRegisterActivity extends ActionBarActivity {

    private static final int SELECT_PICTURE = 1;
    private Recipe recipe;
    private ImageButton ibRecipePicture;
    private EditText etName;
    private ListView lvIngredients;
    private EditText etPrepTime;
    private EditText etPortionNum;
    private EditText etDescription;
    private Bitmap picture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_register);

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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Tem que fazer essa funcao
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // e essa
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
        } else if (requestCode == SELECT_PICTURE) {
            Cursor cursor = getContentResolver().query(intent.getData(), null,
                    null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(ImageColumns.DATA);
            String fileSrc = cursor.getString(idx);
            picture = BitmapFactory.decodeFile(fileSrc);
        }
    }
}
