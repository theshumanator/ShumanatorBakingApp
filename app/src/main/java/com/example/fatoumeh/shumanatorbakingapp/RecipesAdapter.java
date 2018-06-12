package com.example.fatoumeh.shumanatorbakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fatoumeh.shumanatorbakingapp.datamodel.Recipes;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fatoumeh on 05/06/2018.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipesAdapterViewHolder> {

    private ArrayList<Recipes> recipeArrayList;
    private final RecipesAdapterOnClickHandler recipesAdapterOnClickHandler;
    private Context context;

    public RecipesAdapter(RecipesAdapterOnClickHandler recipesAdapterOnClickHandler) {
        this.recipesAdapterOnClickHandler = recipesAdapterOnClickHandler;
    }

    public interface RecipesAdapterOnClickHandler {
        void onClick(Recipes recipeItem);
    }

    @Override
    public RecipesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        int layoutIdForGridItem=R.layout.recipe_item_view;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View inflatedGridView=layoutInflater.inflate(layoutIdForGridItem, parent, false);
        return new RecipesAdapterViewHolder(inflatedGridView);
    }

    @Override
    public void onBindViewHolder(RecipesAdapterViewHolder holder, int position) {
        Recipes recipe = recipeArrayList.get(position);
        String recipeName=recipe.getRecipeName();
        String recipeServings=String.valueOf(recipe.getRecipeServings());
        if (TextUtils.isEmpty(recipeName)) {
            return;
        } else {
            holder.tvRecipeName.setText(recipeName);
            if (!TextUtils.isEmpty(recipeServings)) {
                String formattedServings = context.getResources().getQuantityString(R.plurals.recipe_serving_count,
                        Integer.parseInt(recipeServings), Integer.parseInt(recipeServings));
                holder.tvRecipeServing.setText(formattedServings);
            }
        }
    }


    @Override
    public int getItemCount() {
        if (recipeArrayList==null) {
            return 0;
        } else {
            return recipeArrayList.size();
        }
    }

    public void setRecipeData(ArrayList<Recipes> recipeData) {
        recipeArrayList=recipeData;
        notifyDataSetChanged();
    }

    public class RecipesAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        @BindView(R.id.tv_recipe_name) TextView tvRecipeName;
        @BindView(R.id.tv_recipe_serving) TextView tvRecipeServing;

        public RecipesAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            Recipes recipeItem=recipeArrayList.get(position);
            recipesAdapterOnClickHandler.onClick(recipeItem);
        }
    }

}
