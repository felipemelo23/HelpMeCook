package br.com.helpmecook.view.activity;


import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import br.com.helpmecook.R;
import br.com.helpmecook.view.fragment.CookbookFragment;
import br.com.helpmecook.view.fragment.HomeFragment;
import br.com.helpmecook.view.fragment.NavigationDrawerFragment;
import br.com.helpmecook.view.fragment.PlaceholderFragment;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private String[] navMenuTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new HomeFragment(MainActivity.this);
                break;
            case 1:
                //Busca por ingredient
                break;
            case 2:
                fragment = new CookbookFragment(MainActivity.this);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}