package com.example.fatoumeh.shumanatorbakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.fatoumeh.shumanatorbakingapp.R;
import com.example.fatoumeh.shumanatorbakingapp.datamodel.Recipes;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fatoumeh on 09/06/2018.
 */

public class IngredientsListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<HashMap<String, String>> ingredientsList;
    private Context context;
    private int appWidgetId;
    private Recipes recipeItem;

    public IngredientsListRemoteViewsFactory(Context context, Intent intent) {
        this.context=context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        String recipeParcel=context.getString(R.string.recipe_parcel);
        Bundle bundle=intent.getBundleExtra(recipeParcel);
        bundle.setClassLoader(Recipes.class.getClassLoader());
        recipeItem=bundle.getParcelable(recipeParcel);

    }

    private void getIngredientList() {
        ingredientsList=recipeItem.getRecipeIngredientsList();
    }

    @Override
    public void onCreate() {
        getIngredientList();
    }

    @Override
    public void onDataSetChanged() {
        String recipePref=context.getString(R.string.recipe_pref);
        String widgetRecipe=context.getString(R.string.widget_recipe);
        SharedPreferences widgetRecipePref= context.getSharedPreferences(recipePref, Context.MODE_PRIVATE );
        if (widgetRecipePref.contains(widgetRecipe)) {
            Gson gson = new Gson();
            String recipeJson = widgetRecipePref.getString(widgetRecipe, "");
            recipeItem = gson.fromJson(recipeJson, Recipes.class);
            getIngredientList();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (ingredientsList==null) {
            return 0;
        } else {
            return ingredientsList.size();
        }
    }

    @Override
    public RemoteViews getViewAt(int viewPos) {

        RemoteViews row=new RemoteViews(context.getPackageName(), R.layout.recipe_widget_ingredient_item);
        String ingredient=ingredientsList.get(viewPos).get(context.getString(R.string.json_ingredient));
        String measure=ingredientsList.get(viewPos).get(context.getString(R.string.json_measure));
        String quantity=ingredientsList.get(viewPos).get(context.getString(R.string.json_quantity));
        String overallIngredientSt=quantity+" " + measure + " " + ingredient;
        row.setTextViewText(R.id.tv_widget_ingredient, overallIngredientSt);

        Bundle extras = new Bundle();
        extras.putInt(RecipeWidgetProvider.EXTRA_ITEM, viewPos);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        fillInIntent.putExtra(context.getString(R.string.recipe_parcel), recipeItem);
        row.setOnClickFillInIntent(R.id.tv_widget_ingredient, fillInIntent);

        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int itemPos) {
        return itemPos;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
