package br.com.helpmecook;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import br.com.helpmecook.adapter.NavDrawerListAdapter;
import br.com.helpmecook.model.NavDrawerItem;

public class MainActivity extends FragmentActivity {

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

        Log.i("GREatStudents", "Aplicação Encerrada");
    }

    /*
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
    }*/

}
