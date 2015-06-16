package br.com.helpmecook.view.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
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
import android.widget.Toast;

import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.connection.JsonParser;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.model.AbstractRecipe;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.model.Recipe;
import br.com.helpmecook.sqlite.RecipeDAO;
import br.com.helpmecook.view.activity.IngredientSearchResultActivity;
import br.com.helpmecook.view.activity.MainActivity;
import br.com.helpmecook.view.activity.RecipeActivity;
import br.com.helpmecook.view.adapter.RecipeCardAdapter;

public class HomeFragment extends Fragment {
    private Context context;
    private LayoutInflater inflater;
    ViewGroup container;

    private List<AbstractRecipe> popularRecipes;
    private JsonParser jsonParser = new JsonParser();
    private ProgressDialog pDialog;

    public static int POPULAR_PARAM;

    View fragmentView;

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        fragmentView = inflater.inflate(R.layout.fragment_home,
                container, false);

        context = getActivity();

        loadRecents();

        if (Manager.isOnline(getActivity())) {
            new MostPopularTask().execute();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Sem conexão com internet");
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

    private void loadRecents() {

    }

    private void loadPopular() {
        GridView gvRecents = (GridView) fragmentView.findViewById(R.id.gv_recents);

        if (Manager.getRecentRecipes(context) == null) {
            Log.i("HomeFragment", "contexto esta nulo");
        } else {
            Log.i("HomeFragment", "contexto nao esta nulo");
        }

        final RecipeCardAdapter adapterRecents = new RecipeCardAdapter(context, Manager.getRecentRecipes(context));
        gvRecents.setAdapter(adapterRecents);

        Log.i("HomeFragment", "Receitas recentes " + adapterRecents.getCount() + "");

        gvRecents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showRecipe(adapterRecents.getItem(position).getId());
            }
        });

        setListViewHeightBasedOnChildren(gvRecents);

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
        } else {
            Toast.makeText(context,context.getString(R.string.cant_connect), Toast.LENGTH_LONG).show();
        }
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
            //totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();

        //totalHeight = totalHeight/gridView.getNumColumns();

        params.height = totalHeight + (gridView.getHorizontalSpacing() * (listAdapter.getCount()/gridView.getNumColumns() - 1));
        gridView.setLayoutParams(params);
    }

    private class MostPopularTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(context.getString(R.string.waiting_recents_popular_recipes));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                popularRecipes = Manager.getPopularRecipes(POPULAR_PARAM,context);
            } catch (HttpHostConnectException e) {
                e.printStackTrace();
                popularRecipes = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            pDialog.dismiss();
            loadPopular();
        }
    }
}
