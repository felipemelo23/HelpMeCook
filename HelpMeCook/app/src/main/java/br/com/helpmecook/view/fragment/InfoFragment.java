package br.com.helpmecook.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.helpmecook.R;

/**
 * Created by Thais on 27/05/2015.
 */
public class InfoFragment extends Fragment{

    public InfoFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_info,
                container, false);
        return fragmentView;
    }


}
