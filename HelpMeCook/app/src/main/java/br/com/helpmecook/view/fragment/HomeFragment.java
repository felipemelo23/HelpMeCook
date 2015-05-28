package br.com.helpmecook.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.view.activity.RecipeActivity;
import br.com.helpmecook.view.adapter.RecipeCardAdapter;

public class HomeFragment extends Fragment {
    private Context context;
    private GridView gvRecents;
    private GridView gvPop;

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home,
                container, false);

        context = getActivity();

        gvRecents = (GridView) fragmentView.findViewById(R.id.gv_recents);
        //gvRecents.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 100));
        gvPop = (GridView) fragmentView.findViewById(R.id.gv_pop);

        if (Manager.getRecentRecipes(context) == null) {
            Log.i("HomeFragment", "esta nulo");
        } else {
            Log.i("HomeFragment", "nao esta nulo");
        }

        List<AbstractRecipe> ar = Manager.getRecentRecipes(context);

        final RecipeCardAdapter adapterRecents = new RecipeCardAdapter(context, Manager.getRecentRecipes(context));
        gvRecents.setAdapter(adapterRecents);
        Log.i("HomeFragment", adapterRecents.getCount() + "");
        final RecipeCardAdapter adapterPop = new RecipeCardAdapter(context, Manager.getPopularRecipes());
        gvPop.setAdapter(adapterPop);

        gvRecents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showRecipe(adapterRecents.getItem(position).getId());
            }
        });
        gvPop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showRecipe(adapterPop.getItem(position).getId());
            }
        });

        return fragmentView;
    }

    public void showRecipe(long id) {
        Intent intent = new Intent(context, RecipeActivity.class);
        intent.putExtra(RecipeActivity.RECIPE_ID, id);

        startActivity(intent);
    }
}
