package br.com.helpmecook.view.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;

import br.com.helpmecook.R;

/**
 * Created by Felipe on 30/05/2015.
 */
@SuppressLint("ValidFragment")
public class QuantityIngredientDialog extends DialogFragment {
    DialogInterface.OnClickListener listener;

    public QuantityIngredientDialog(DialogInterface.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_quantity_ingredient, null));

        builder.setPositiveButton(getString(R.string.ok), listener);

        return builder.create();
    }
}
