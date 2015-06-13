package br.com.helpmecook.view.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.FragmentManager;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.MapFragment;

import br.com.helpmecook.R;
import br.com.helpmecook.control.Manager;
import br.com.helpmecook.view.fragment.CookbookFragment;
import br.com.helpmecook.view.fragment.HomeFragment;
import br.com.helpmecook.view.fragment.InfoFragment;

import br.com.helpmecook.view.fragment.MapFragment2;
import br.com.helpmecook.view.fragment.NavigationDrawerFragment;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final int MAIN = 0;
    private static final String FIRST_TIME = "first_time";
    private static final String POSITION_NAV_DRAWER = "position";

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Main - Ciclo de Vida", "onCreate");
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(FIRST_TIME, 0);

        if (settings.getBoolean(FIRST_TIME, true)) {
            new AsyncTaskLoadIngredients().execute();
            settings.edit().putBoolean(FIRST_TIME, false).commit();
        }

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment fragment = null;
        FragmentActivity fragmentActivity = null;

        switch (position) {
            case 0:
                mTitle = getResources().getString(R.string.app_name);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(POSITION_NAV_DRAWER, 0);
                editor.commit();
                fragment = new HomeFragment();
                break;
            case 1:
                Intent intent = new Intent(getApplicationContext(), IngredientSelectionActivity.class);
                intent.putExtra(IngredientSelectionActivity.REQUEST_CODE, MAIN);
                startActivity(intent);
                break;
            case 2:
                mTitle = getResources().getString(R.string.title_activity_cookbook);

                SharedPreferences preferences2 = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor2 = preferences2.edit();
                editor2.putInt(POSITION_NAV_DRAWER, 2);
                editor2.commit();

                SharedPreferences settings = getSharedPreferences(POSITION_NAV_DRAWER, 0);
                Log.i("Go to Cookbook", "position: " + settings.getInt(POSITION_NAV_DRAWER, 0));

                fragment = new CookbookFragment();
                break;
            case 3:
                startActivity(new Intent(MainActivity.this, RecipeRegisterActivity.class));
                break;
            case 4:
                mTitle = getResources().getString(R.string.title_activity_map);

                SharedPreferences preferences3 = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor3 = preferences3.edit();
                editor3.putInt(POSITION_NAV_DRAWER, 4);
                editor3.commit();

                fragment = new MapFragment2();
                break;
            case 5:
                mTitle = getResources().getString(R.string.title_activity_info);

                SharedPreferences preferences4 = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor4 = preferences4.edit();
                editor4.putInt(POSITION_NAV_DRAWER, 5);
                editor4.commit();

                fragment = new InfoFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();

            // Atualiza o titulo e fecha a navigation drawer
        } else {
            // Erro na criação do fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
//        if (fragmentActivity != null) {
//            FragmentManager fragmentManager = getFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.container, fragment).commit();
//
//            // Atualiza o titulo e fecha a navigation drawer
//        } else {
//            // Erro na criação do fragment
//            Log.e("MainActivity", "Error in creating fragment");
//        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        //return super.onCreateOptionsMenu(menu);
        return false;
    }

    class AsyncTaskLoadIngredients extends AsyncTask<Void, Integer, String> {

        protected void onPreExecute(){
            Log.d("Asyntask","On preExceute...");
        }

        protected String doInBackground(Void...arg0) {
            Log.d("Asyntask","On doInBackground...");

            Manager.insertAllIngredients(getApplicationContext());

            return "You are at PostExecute";
        }

        protected void onProgressUpdate(Integer...a){
            Log.d("Asyntask","You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            Log.d("Asyntask",result);
        }
    }

    @Override
    protected void onResume() {
        SharedPreferences settings = getSharedPreferences(POSITION_NAV_DRAWER, 0);
        Log.i("Main - Ciclo de Vida", "onResume" + settings.getInt(POSITION_NAV_DRAWER, 0));
        super.onResume();
        //atualizar os fragments
        onNavigationDrawerItemSelected(settings.getInt(POSITION_NAV_DRAWER, 0));
    }

    @Override
    protected void onStop() {
        SharedPreferences settings = getSharedPreferences(POSITION_NAV_DRAWER, 0);
        Log.i("Main - Ciclo de Vida", "onStop" + " position:" + settings.getInt(POSITION_NAV_DRAWER, 0));
        super.onStop();
    }
}