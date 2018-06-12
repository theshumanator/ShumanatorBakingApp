package com.example.fatoumeh.shumanatorbakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fatoumeh on 07/06/2018.
 */

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.RecipeStepsAdapterViewHolder>  {

    private ArrayList<HashMap<String, String>> recipeSteps;
    private Context context;
    private final RecipestepsAdapterOnClickHandler recipestepsAdapterOnClickHandler;

    public RecipeStepsAdapter(Context context, RecipestepsAdapterOnClickHandler recipestepsAdapterOnClickHandler) {
        this.context=context;
        this.recipestepsAdapterOnClickHandler=recipestepsAdapterOnClickHandler;
    }

    @Override
    public RecipeStepsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForStepItem=R.layout.recipe_step_item_view;
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View inflatedView=layoutInflater.inflate(layoutIdForStepItem, parent, false);
        return new RecipeStepsAdapterViewHolder(inflatedView);

    }

    @Override
    public void onBindViewHolder(RecipeStepsAdapterViewHolder holder, int position) {
        HashMap<String, String> recipeStep = recipeSteps.get(position);
        String shortDescription=recipeStep.get(context.getString(R.string.json_shortDescription));

        int stepNumber=Integer.parseInt(recipeStep.get(context.getString(R.string.json_id)));
        String stepLabel=context.getString(R.string.step_label) + " ";
        //if it's step 0, we just remove the Step label
        if (stepNumber==0) {
            stepLabel="";
        }

        holder.tvRecipeStepLabel.setText(stepLabel);

        if (TextUtils.isEmpty(shortDescription)) {
            shortDescription=recipeStep.get(context.getString(R.string.json_description));
        }
        holder.tvRecipeStep.setText(shortDescription);
    }

    @Override
    public int getItemCount() {
        if (recipeSteps==null) {
            return 0;
        } else {
            return recipeSteps.size();
        }
    }

    public void setRecipeSteps(ArrayList<HashMap<String, String>> recipeStepsList) {
        recipeSteps=recipeStepsList;
        notifyDataSetChanged();
    }

    public interface RecipestepsAdapterOnClickHandler {
        void onClick(HashMap<String, String> recipeStep);
    }

    public class RecipeStepsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_recipe_step_label) TextView tvRecipeStepLabel;
        @BindView(R.id.tv_recipe_step) TextView tvRecipeStep;
        public RecipeStepsAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        //get the position of the adapter where click has happened and pass to the adapterclickhandler
        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            HashMap<String, String> recipeStep = recipeSteps.get(position);
            recipestepsAdapterOnClickHandler.onClick(recipeStep);
        }
    }

}
