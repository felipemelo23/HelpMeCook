package br.com.helpmecook.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.model.Recipe;
import br.com.helpmecook.view.adapter.IngredientsAdapter;

public class RecipeRegisterActivity extends ActionBarActivity {

    private static final int REGISTER_RECIPE = 1;
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
    private ImageView ivRecipePicture;
    private EditText etName;
    private ListView lvIngredients;
    private EditText etPrepTime;
    private EditText etPortionNum;
    private EditText etDescription;
    private Bitmap picture;
    private long ingredients[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_register);

        Log.i("Ciclo de Vida", "OnCreate");

        ivRecipePicture = (ImageView) findViewById(R.id.iv_register_recipe);
        etName = (EditText) findViewById(R.id.et_recipe_register_name);
        lvIngredients = (ListView) findViewById(R.id.lv_recipe_register_ingredients);
        etPrepTime = (EditText) findViewById(R.id.et_recipe_register_preparation_time);
        etPortionNum = (EditText) findViewById(R.id.et_recipe_register_portions_number);
        etDescription = (EditText) findViewById(R.id.et_recipe_register_description);
        picture = BitmapFactory.decodeResource(ivRecipePicture.getResources(), R.drawable.plate);
        TextView tvIngredients = (TextView) findViewById(R.id.tv_ingredients);



        //Abre a intent para selecionar a foto da receita
        //por enquanto só abre o gerenciador de arquivos, não a camera
        ivRecipePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PICTURE);
            }
        });

        tvIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectIngredientsIntent = new Intent(getApplicationContext(), IngredientSelectionActivity.class);
                selectIngredientsIntent.putExtra(CURRENT_INGREDIENTS, ingredients);
                selectIngredientsIntent.putExtra(IngredientSelectionActivity.REQUEST_CODE, REGISTER_RECIPE);
                startActivityForResult(selectIngredientsIntent, SELECT_INGREDIENT);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("Ciclo de Vida", "OnSave");
        // Tem que fazer essa funcao(ainda tem que fazer, ou o comentário é que não foi apagado?)
        outState.putString(RECIPE_NAME_KEY, etName.getText().toString());
        outState.putLongArray(INGREDIENTS_KEY, ingredients);
        outState.putString(PREPARE_TIME_KEY, etPrepTime.getText().toString());
        outState.putString(PORTION_KEY, etPortionNum.getText().toString());
        outState.putString(DESCRIPTION_KEY, etDescription.getText().toString());

        outState.putParcelable(PICTURE_KEY,picture);



        /*System.out.println(picture);
        int bytes = picture.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        picture.copyPixelsToBuffer(buffer);
        byte[] array = buffer.array();

        Log.i("Debug", "Tamanho do Buffer: " + array.length);

        outState.putByteArray(PICTURE_KEY, array);*/
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("Ciclo de Vida", "OnRestore");
        // e essa
        super.onRestoreInstanceState(savedInstanceState);
        etName.setText(savedInstanceState.getString(RECIPE_NAME_KEY));
        ingredients = savedInstanceState.getLongArray(INGREDIENTS_KEY);
        etPrepTime.setText(savedInstanceState.getString(PREPARE_TIME_KEY));
        etPortionNum.setText(savedInstanceState.getString(PORTION_KEY));
        etDescription.setText(savedInstanceState.getString(DESCRIPTION_KEY));

        /*byte[] array = savedInstanceState.getByteArray(PICTURE_KEY);
        ByteBuffer buffer = ByteBuffer.wrap(array);
        Log.i("Debug", "Tamanho do Buffer: " + array.length);

        picture.copyPixelsFromBuffer(buffer);*/
        picture = savedInstanceState.getParcelable(PICTURE_KEY);
    }

    public void registerRecipe() {
        recipe = new Recipe();
        if ((etName.getText().toString().equals("")) || (etDescription.getText().toString().equals("")) || (ingredients == null)){ // + lista de ingredientes
            Toast.makeText(RecipeRegisterActivity.this, getResources().getString(R.string.blank_fields), Toast.LENGTH_LONG).show();
        } else {
            recipe.setName(etName.getText().toString());
            recipe.setText(etDescription.getText().toString());

            if (picture.equals(null)){
                recipe.setPicture(BitmapFactory.decodeResource((RecipeRegisterActivity.this).getResources(),R.drawable.plate));
            } else {
                recipe.setPicture(picture);
            }
            if (!(etPrepTime.getText().toString().equals(""))) {
                recipe.setEstimatedTime(Integer.parseInt(etPrepTime.getText().toString()));
            }
            if (!(etPortionNum.getText().toString().equals(""))) {
                recipe.setPortionNum(etPortionNum.getText().toString());
            }

            System.out.println(Manager.registerRecipe(recipe, getApplicationContext()));
            Toast.makeText(getApplicationContext(), getString(R.string.recipe_registered),Toast.LENGTH_LONG).show();
            //System.out.println(Manager.getRecipeById(Long.MAX_VALUE, this).getName());
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
    protected void onResume() {
        super.onResume();
        Log.i("Ciclo de Vida", "OnResume");

        if (ingredients != null){
            Log.i("Debug", "Ingredientes não é nulo");
            Log.i("Debug", "O tamanho de ingredientes é: " + ingredients.length);
            List<Long> ingId = new ArrayList<Long>();
            for (int i = 0; i < ingredients.length; i++) {
                Log.i("Debug", "id: " + ingredients[i]);
                ingId.add(ingredients[i]);
            }
            List<Ingredient> i = Manager.getRecipeIngredients(ingId,getApplicationContext());
            for (Ingredient ing : i) {
                Log.i("Debug", "nome: " + ing.getName());
            }
            IngredientsAdapter adapter = new IngredientsAdapter(getApplicationContext(),i);
            System.out.println("oi");
            lvIngredients.setAdapter(adapter);
            setListViewHeightBasedOnChildren(lvIngredients);
            adapter.notifyDataSetChanged();
            System.out.println("xau");
        }

        lvIngredients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent selectIngredientsIntent = new Intent(getApplicationContext(), IngredientSelectionActivity.class);
                selectIngredientsIntent.putExtra(CURRENT_INGREDIENTS, ingredients);
                selectIngredientsIntent.putExtra(IngredientSelectionActivity.REQUEST_CODE, REGISTER_RECIPE);
                startActivityForResult(selectIngredientsIntent, SELECT_INGREDIENT);
            }
        });

        ivRecipePicture.setImageBitmap(picture);
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
    protected void onRestart() {
        super.onRestart();
        Log.i("Ciclo de Vida", "OnRestart");
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

                    ingredients = intent.getLongArrayExtra(IngredientSelectionActivity.WANTED_INGREDIENTS);
                    Log.i("Ciclo de Vida","OnActivityResult");
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "De onde voce veio, meu amigo?", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
