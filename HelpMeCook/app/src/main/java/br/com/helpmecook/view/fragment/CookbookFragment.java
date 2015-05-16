package br.com.helpmecook.view.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.Cookbook;
import br.com.helpmecook.view.activity.RecipeActivity;
import br.com.helpmecook.view.adapter.RecipesListAdapter;

/**
 * Created by Thais on 16/05/2015.
 */
@SuppressLint("ValidFragment")
public class CookbookFragment extends Fragment{
    private ListView lv_recipes_cookbook;
    private Context context;
    private Cookbook cookbook;

    public static final String RECIPE_ID = "recipeID";

    public CookbookFragment(Context context) {
        this.context = context;
        loadCookbook();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_cookbook,
                container, false);

        lv_recipes_cookbook = (ListView) fragmentView.findViewById(R.id.lv_cookbook);
        lv_recipes_cookbook.setAdapter(new RecipesListAdapter(context, cookbook.getRecipeList()));

        lv_recipes_cookbook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showRecipe(cookbook.getRecipeAt(position).getId());
            }
        });

        return fragmentView;
    }

    public void loadCookbook() {
        this.cookbook = Manager.getCookbook(context);
    }

    public void showRecipe(long id) {
        Intent intent = new Intent(context, RecipeActivity.class);
        intent.putExtra(RECIPE_ID, id);

        startActivity(intent);
    }
}