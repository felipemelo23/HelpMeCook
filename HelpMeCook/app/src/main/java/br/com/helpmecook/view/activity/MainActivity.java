package br.com.helpmecook.view.activity;


import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentManager;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.sql.SQLException;

import br.com.helpmecook.R;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.sqlite.IngredientDAO;
import br.com.helpmecook.view.fragment.CookbookFragment;
import br.com.helpmecook.view.fragment.HomeFragment;
import br.com.helpmecook.view.fragment.NavigationDrawerFragment;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final int MAIN = 0;
    private static final String NAV_ITEM_KEY = "navItemPosition";
    private int navItemPosition = 0;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private String[] navMenuTitles;
    private int easteregg = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Ciclo de Vida", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

/*    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("Ciclo de Vida", "onSaveInstanceState");
        outState.putInt(NAV_ITEM_KEY,navItemPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("Ciclo de Vida", "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        onNavigationDrawerItemSelected(savedInstanceState.getInt(NAV_ITEM_KEY));
    }*/

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        navItemPosition = position;
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                Intent intent = new Intent(getApplicationContext(), IngredientSelectionActivity.class);
                intent.putExtra(IngredientSelectionActivity.REQUEST_CODE, MAIN);
                startActivity(intent);
                break;
            case 2:
                fragment = new CookbookFragment();
                break;
            case 3:
                startActivity(new Intent(MainActivity.this, RecipeRegisterActivity.class));
                break;
            case 4:
                //Onde encontrar comida?
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
    }

    public void onSectionAttached(int number) {
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        switch (number) {
            case 1:
                mTitle = navMenuTitles[0];
                break;
            case 2:
                mTitle = navMenuTitles[1];
                break;
            case 3:
                mTitle = navMenuTitles[2];
                break;
            case 4:
                mTitle = navMenuTitles[3];
                break;
            case 5:
                mTitle = navMenuTitles[4];
                break;
        }
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
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            easteregg++;
            if (easteregg > 10) {
                Toast.makeText(getApplicationContext(),getString(R.string.what_are_you_doing),Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}