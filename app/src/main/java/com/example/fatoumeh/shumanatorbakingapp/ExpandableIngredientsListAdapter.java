package com.example.fatoumeh.shumanatorbakingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fatoumeh on 07/06/2018.
 */

public class ExpandableIngredientsListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> ingredientsList;
    private String ingredientsHeader;

    @BindView(R.id.tv_ingredient_quantity) TextView tvIngredientQuantity;
    @BindView(R.id.tv_ingredient_measure) TextView tvIngredientMeasure;
    @BindView(R.id.tv_ingredient_name) TextView tvIngredientName;

    public ExpandableIngredientsListAdapter(Context context,
                                            ArrayList<HashMap<String, String>> ingredientsList) {
        this.context = context;
        this.ingredientsList = ingredientsList;
        this.ingredientsHeader=context.getString(R.string.recipe_ingredients);
    }

    //we have only one header i.e ingredients header
    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int i) {
        return ingredientsList.size();
    }

    @Override
    public Object getGroup(int i) {
        return ingredientsHeader;
    }

    @Override
    public Object getChild(int groupPos, int childPos) {
        return ingredientsList.get(childPos);
    }

    @Override
    public long getGroupId(int groupPos) {
        return groupPos;
    }

    @Override
    public long getChildId(int groupPos, int childPos) {
        return childPos;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View inflatedGroupView,
                             ViewGroup parent) {
        if (inflatedGroupView == null) {
            LayoutInflater layoutInflater=LayoutInflater.from(context);
            inflatedGroupView = layoutInflater.inflate(R.layout.ingredients_list_group, null);
        }
        TextView tvIngredientHeader=ButterKnife.findById(inflatedGroupView, R.id.tv_ingredient_header);
        tvIngredientHeader.setText(ingredientsHeader);
        return inflatedGroupView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View inflatedChildView, ViewGroup parent) {
        final HashMap<String, String> ingredientItem = (HashMap<String, String>) getChild(groupPosition, childPosition);

        if (inflatedChildView == null) {
            LayoutInflater layoutInflater=LayoutInflater.from(context);
            inflatedChildView = layoutInflater.inflate(R.layout.ingredient_list_item, null);
        }
        ButterKnife.bind(this, inflatedChildView);
        tvIngredientQuantity.setText(ingredientItem.get(context.getString(R.string.json_quantity)));
        tvIngredientMeasure.setText(ingredientItem.get(context.getString(R.string.json_measure)));
        tvIngredientName.setText(ingredientItem.get(context.getString(R.string.json_ingredient)));
        return inflatedChildView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
