package br.com.helpmecook.view;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import br.com.helpmecook.control.Manager;
import br.com.helpmecook.view.activity.MainActivity;
import br.com.helpmecook.view.fragment.HomeFragment;

/**
 * Created by Felipe on 15/06/2015.
 */
public class MyApp extends Application {

    public MyApp() {
        Log.i("main", "Constructor fired");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HomeFragment.POPULAR_PARAM = Manager.SERVER_POPULAR;
        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.POSITION_NAV_DRAWER, 0).edit();
        editor.putInt(MainActivity.POSITION_NAV_DRAWER, 0).commit();
        Log.i("Application", "onCreate");
    }
}
