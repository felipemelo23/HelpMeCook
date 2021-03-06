package br.com.helpmecook.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import br.com.helpmecook.R;
import br.com.helpmecook.model.AbstractRecipe;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thais on 15/05/2015.
 */
public class RecipesListAdapter extends BaseAdapter{
    Context context;
    List<AbstractRecipe> recipes;

    public RecipesListAdapter(Context context, List<AbstractRecipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public AbstractRecipe getItem(int position) {
        return recipes.get(position);
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
            convertView = mInflater.inflate(R.layout.item_recipe, null);
        }

        CircleImageView icon = (CircleImageView) convertView.findViewById(R.id.iv_recipe_detail);
        TextView name = (TextView) convertView.findViewById(R.id.tv_recipe_name_detail);
        RatingBar rbTaste = (RatingBar) convertView.findViewById(R.id.rb_taste_detail);
        RatingBar rbDifficulty = (RatingBar) convertView.findViewById(R.id.rb_difficulty_detail);
        CircleImageView exBackground = (CircleImageView) convertView.findViewById(R.id.iv_ex);
        TextView tvExtras = (TextView) convertView.findViewById(R.id.tv_ex);
        tvExtras.bringToFront();

        icon.setImageBitmap(recipes.get(position).getPicture());
        name.setText(recipes.get(position).getName());
        rbTaste.setRating(recipes.get(position).getTaste());
        rbDifficulty.setRating(recipes.get(position).getDifficulty());

        if (recipes.get(position).getExtraIngNum() > 0){
            if (recipes.get(position).getExtraIngNum() > 9){
                tvExtras.setText("+..");
            }else{
                tvExtras.setText("+" + recipes.get(position).getExtraIngNum());
            }
            exBackground.setVisibility(View.VISIBLE);
            tvExtras.setVisibility(View.VISIBLE);
        }

        tvExtras.bringToFront();

        return convertView;
    }
}
