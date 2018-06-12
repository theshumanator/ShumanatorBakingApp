package com.example.fatoumeh.shumanatorbakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;

/**
 * Created by fatoumeh on 08/06/2018.
 */

public class MasterListFragment extends Fragment implements RecipeStepsAdapter.RecipestepsAdapterOnClickHandler  {


    private Context context;
    RecipeStepClickListener recipeStepClickListener;

    public MasterListFragment() {
    }

    public interface RecipeStepClickListener {
        void onRecipeStepClick(HashMap<String, String> recipeStep);
    }

    //checking that the activity has implemented the listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
        try {
            recipeStepClickListener = (RecipeStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RecipeStepClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {



        ArrayList<HashMap<String, String>> ingredients= ((RecipeStepsIngredientsActivity)getActivity()).getIngredientsList();
        ArrayList<HashMap<String, String>> steps= ((RecipeStepsIngredientsActivity)getActivity()).getStepsList();

        final View rootView=inflater.inflate(R.layout.fragment_master_list, container, false);

        ExpandableListView fragmentIngredientsListView=ButterKnife.findById(rootView, R.id.fragment_evl_ingredients);
        RecyclerView fragmentRecipeStepsRecyclerView=ButterKnife.findById(rootView, R.id.fragment_rv_recipe_steps);

        ExpandableIngredientsListAdapter ingredientsListAdapter =new ExpandableIngredientsListAdapter(context, ingredients);
        fragmentIngredientsListView.setAdapter(ingredientsListAdapter);

        RecipeStepsAdapter recipeStepsAdapter=new RecipeStepsAdapter(context, this);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(context, 1);
        fragmentRecipeStepsRecyclerView.setLayoutManager(gridLayoutManager);
        fragmentRecipeStepsRecyclerView.setAdapter(recipeStepsAdapter);
        recipeStepsAdapter.setRecipeSteps(steps);

        return rootView;
    }

    @Override
    public void onClick(HashMap<String, String> recipeStep) {
        recipeStepClickListener.onRecipeStepClick(recipeStep);
    }
}
