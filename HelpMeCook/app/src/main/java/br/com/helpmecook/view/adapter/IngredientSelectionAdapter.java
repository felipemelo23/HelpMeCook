package br.com.helpmecook.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.helpmecook.R;
import br.com.helpmecook.view.activity.IngredientSelectionList;

/**
 * Created by mariana on 16/05/15.
 */
public class IngredientSelectionAdapter extends ArrayAdapter<IngredientSelectionList>{
    Context context;
    int layoutResourceId;
    IngredientSelectionList data[] = null;

    public IngredientSelectionAdapter(Context context, int layoutResourceId, IngredientSelectionList[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ListHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ListHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);


            row.setTag(holder);
        }
        else
        {
            holder = (ListHolder)row.getTag();
        }

        IngredientSelectionList ingredientselectionlist = data[position];
        holder.imgIcon.setImageResource(ingredientselectionlist.icon);
        holder.txtTitle.setText(ingredientselectionlist.title);


        return row;
    }

    static class ListHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }

}
