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

/**
 * Created by Thais on 14/05/2015.
 */
public class IngredientsAdapter extends BaseAdapter {
    private Context context;
    private List<Ingredient> ingredients;

    public IngredientsAdapter(Context context, List<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    @Override
    public int getCount() {
        return ingredients.size();
    }

    @Override
    public Ingredient getItem(int position) {
        return ingredients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_ingredients, null);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.icon_ingredient);
        TextView name = (TextView) convertView.findViewById(R.id.name_ingredient);

        icon.setImageDrawable(context.getResources().getDrawable(ingredients.get(position).getIconPath()));
        name.setText(ingredients.get(position).getName());

        return null;
    }

}
