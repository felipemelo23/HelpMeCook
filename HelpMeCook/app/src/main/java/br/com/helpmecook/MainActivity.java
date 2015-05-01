package br.com.helpmecook;

<<<<<<< HEAD
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import br.com.helpmecook.adapter.NavDrawerListAdapter;
import br.com.helpmecook.model.NavDrawerItem;

public class MainActivity extends ActionBarActivity {

    // SharedPreferences da posicao selecionada na navigation drawer
    public static final String PREFS_POSICAO = "posicao";
    // posicao selecionada na navigation drawer
    private int posicao;
    // layout da navigation drawer
    private DrawerLayout drawerLayout;
    // lista com os itens da navigation drawer
    // responsavel por abrir e fechar a navigation drawer
    private ActionBarDrawerToggle abDrawerToggle;
    private ListView drawerMenuList;
    // titulo da navigation drawer
    private CharSequence drawerTitle;
    // titulo da aplicacao
    private CharSequence mTitle;
    // titulos dos itens da navigation drawer
    private String[] navMenuTitles;
    // itens da navigation drawer
    private ArrayList<NavDrawerItem> navDrawerItems;
    // adapter da navigation drawer
    private NavDrawerListAdapter adapter;
=======
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import br.com.helpmecook.view.fragment.PlaceholderFragment;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private String[] navMenuTitles;
>>>>>>> 229ff594554c2008ce8c6cbf3d094595e0641231

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Carregar a posicao selecionada na navigation drawer
        final SharedPreferences settings = getSharedPreferences(PREFS_POSICAO, 0);
        posicao = settings.getInt(PREFS_POSICAO, posicao);

        mTitle = drawerTitle = getTitle();

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_itens);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerMenuList = (ListView) findViewById(R.id.navigation_drawer);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        //Adicionando os itens na nav drawer
        //Busca por igrediente
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], R.drawable.magnify));
        //Meu CookBook
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], R.drawable.file_document_box));
        //Criar receita
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], R.drawable.pencil));
        //Localizar Restaurante
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], R.drawable.silverware_variant));
        //Localizar Supermercado
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], R.drawable.cart));

        drawerMenuList.setOnItemClickListener(new SlideMenuClickListener());

        // Selecionando o adapter da navigation drawer
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        drawerMenuList.setAdapter(adapter);

        abDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(abDrawerToggle);

        if (savedInstanceState == null) {
            displayView(0);
        }

        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

<<<<<<< HEAD
    // Alterna entre os itens da nav drawer
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            displayView(position);
        }
    }

    /**
     * Mostra o fragment selecionado (checar se esta tudo certo)
     * */
    private void displayView(int position) {
        // Atualiza a view principal, com o fragmento selecionado
        Fragment fragment = null;

        // Atualiza a posicao
        final SharedPreferences settings = getSharedPreferences(PREFS_POSICAO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("posicao", position);
        editor.commit();

        switch (position) {
            case 0:
                //fragment = new
                break;
            default:
=======
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
>>>>>>> 229ff594554c2008ce8c6cbf3d094595e0641231
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();

            // Atualiza o titulo e fecha a navigation drawer
            drawerMenuList.setItemChecked(position, true);
            drawerMenuList.setSelection(position);
            setTitle(navMenuTitles[position]);
            drawerLayout.closeDrawer(drawerMenuList);
        } else {
            // Erro na criação do fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        final SharedPreferences settings = getSharedPreferences(PREFS_POSICAO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(PREFS_POSICAO, posicao);
        editor.commit();

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        abDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        abDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Atualiza a posicao para sempre abrir na lista de alunos
        final SharedPreferences settings = getSharedPreferences(PREFS_POSICAO, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(PREFS_POSICAO, posicao);
        editor.commit();
    }

/*
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
<<<<<<< HEAD
    }*/

}
=======
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

    /**
     * A placeholder fragment containing a simple view.
     */
}
>>>>>>> 229ff594554c2008ce8c6cbf3d094595e0641231
