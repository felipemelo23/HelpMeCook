package br.com.helpmecook.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.helpmecook.R;

/**
 * Created by Thais Torres on 16/04/2015.
 */
public class BuscaIngrFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.test_layout,
                container, false);

        return fragmentView;
    }

}
