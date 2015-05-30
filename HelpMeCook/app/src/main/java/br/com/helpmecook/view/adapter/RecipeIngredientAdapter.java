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
import br.com.helpmecook.model.Recipe;

/**
 * Created by Felipe on 30/05/2015.
 */
public class RecipeIngredientAdapter extends BaseAdapter {
    private Context context;
    private List<Ingredient> ingredients;
    private List<String> ingredientsQnt;

    public RecipeIngredientAdapter(Context context, List<Ingredient> ingredients, List<String> ingredientsQnt) {
        this.context = context;
        this.ingredients = ingredients;
        this.ingredientsQnt = ingredientsQnt;
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
            convertView = mInflater.inflate(R.layout.item_recipe_ingredient, null);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.imgIcon);
        TextView qntd = (TextView) convertView.findViewById(R.id.txtQuant);
        TextView name = (TextView) convertView.findViewById(R.id.txtTitle);

        icon.setImageDrawable(context.getResources().getDrawable(ingredients.get(position).getIconPath()));
        name.setText(ingredients.get(position).getName());
        qntd.setText(ingredientsQnt.get(position));

        return convertView;
    }
}
