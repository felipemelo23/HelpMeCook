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
    private List<String> units;

    public IngredientsAdapter(Context context, List<Ingredient> ingredients, List<String> units) {
        this.context = context;
        this.ingredients = ingredients;
        this.units = units;
    }

    @Override
    public int getCount() {
        return ingredients.size();
    }

    @Override
    public Ingredient getItem(int position) {
        Ingredient ingredient = ingredients.get(position);

        if (units.size() == ingredients.size()) {
            ingredient.setQuantity(units.get(position));
        } else {
            ingredient.setQuantity("0 unid.");
        }

        return ingredient;
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
            convertView = mInflater.inflate(R.layout.item_recipe_ingredient, null);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.imgIcon);
        TextView name = (TextView) convertView.findViewById(R.id.txtTitle);
        TextView qnt = (TextView) convertView.findViewById(R.id.txtQuant);

        icon.setImageDrawable(context.getResources().getDrawable(getItem(position).getIconPath()));
        name.setText(getItem(position).getName());
        qnt.setText(getItem(position).getQuantity());

        return convertView;
    }

}
