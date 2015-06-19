package br.com.helpmecook.view.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import org.apache.http.conn.HttpHostConnectException;

import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.AbstractRecipe;

import br.com.helpmecook.view.activity.RecipeActivity;
import br.com.helpmecook.view.adapter.RecipeCardAdapter;

public class HomeFragment extends Fragment {

    private static final String FIRST_TIME = "first_time_pops";
    public static int POPULAR_PARAM;

    private Context context;
    private LayoutInflater inflater;
    private ViewGroup container;
    private List<AbstractRecipe> popularRecipes;
    private View fragmentView;

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        fragmentView = inflater.inflate(R.layout.fragment_home,
                container, false);

        context = getActivity();
        SharedPreferences settings = getActivity().getSharedPreferences(FIRST_TIME, 0);

        if (Manager.isOnline(getActivity())) {
            new MostPopularTask().execute();
        } else if (!(settings.getBoolean(FIRST_TIME, true))) {
            popularRecipes = Manager.getLocalPopularRecipes(getActivity());
            loadPopularAndRecents();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.no_connection));
            builder.setNeutralButton("Ok", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return fragmentView;
    }

    public void showRecipe(long id) {
        Intent intent = new Intent(context, RecipeActivity.class);
        intent.putExtra(RecipeActivity.RECIPE_ID, id);

        startActivity(intent);
    }

    private void loadPopularAndRecents() {
        GridView gvRecents = (GridView) fragmentView.findViewById(R.id.gv_recents);

        final RecipeCardAdapter adapterRecents = new RecipeCardAdapter(context, Manager.getRecentRecipes(context));
        gvRecents.setAdapter(adapterRecents);

        Log.i("HomeFragment", "Receitas recentes " + adapterRecents.getCount() + "");

        gvRecents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showRecipe(adapterRecents.getItem(position).getId());
            }
        });

        if (popularRecipes != null) {
            GridView gvPop = (GridView) fragmentView.findViewById(R.id.gv_pop);

            final RecipeCardAdapter adapterPop = new RecipeCardAdapter(context, popularRecipes);
            gvPop.setAdapter(adapterPop);

            gvPop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showRecipe(adapterPop.getItem(position).getId());
                }
            });

            setListViewHeightBasedOnChildren(gvPop);
        }

        setListViewHeightBasedOnChildren(gvRecents);
    }

    public static void setListViewHeightBasedOnChildren(GridView gridView) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            if (i%gridView.getNumColumns()==0){
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();

        params.height = totalHeight + (gridView.getHorizontalSpacing() * (listAdapter.getCount()/gridView.getNumColumns() - 1));
        Log.i("Tamanho Grid Home", " " + params.height);
        gridView.setLayoutParams(params);
    }

    private class MostPopularTask extends AsyncTask {
        SharedPreferences settings = getActivity().getSharedPreferences(FIRST_TIME, 0);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!(settings.getBoolean(FIRST_TIME, false))) {
                popularRecipes = Manager.getLocalPopularRecipes(getActivity());
            }
            loadPopularAndRecents();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                popularRecipes = Manager.getPopularRecipes(POPULAR_PARAM,context);
                settings.edit().putBoolean(FIRST_TIME, false).commit();
            } catch (HttpHostConnectException e) {
                e.printStackTrace();
                popularRecipes = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            loadPopularAndRecents();
        }
    }
}
