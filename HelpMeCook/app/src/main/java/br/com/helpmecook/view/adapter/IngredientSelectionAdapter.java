package br.com.helpmecook.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.model.Ingredient;
import br.com.helpmecook.view.activity.IngredientSelectionList;

/**
 * Created by mariana on 16/05/15.
 */
public class IngredientSelectionAdapter extends BaseAdapter {
    Context context;
    int layoutResourceId;
    List<Ingredient> data = null;

    public IngredientSelectionAdapter(Context context, int layoutResourceId, List<Ingredient> data) {
        super();
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
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

        Ingredient ingredient = data.get(position);
        holder.imgIcon.setImageResource(ingredient.getIconPath());
        holder.txtTitle.setText(ingredient.getName());


        return row;
    }

    static class ListHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }

}
