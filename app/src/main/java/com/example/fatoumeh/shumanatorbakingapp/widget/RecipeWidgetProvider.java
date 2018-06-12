package com.example.fatoumeh.shumanatorbakingapp.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.example.fatoumeh.shumanatorbakingapp.R;
import com.example.fatoumeh.shumanatorbakingapp.RecipeStepsIngredientsActivity;
import com.example.fatoumeh.shumanatorbakingapp.datamodel.Recipes;
import com.example.fatoumeh.shumanatorbakingapp.SelectARecipeActivity;
import com.google.gson.Gson;


public class RecipeWidgetProvider extends AppWidgetProvider {

    static int OVERALL_CLICK=100;
    static int ROW_CLICK=200;

    static String EXTRA_ITEM="recipeWidgetItem";
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                Recipes recipeItem, int appWidgetId) {

        RemoteViews remoteViews=getIngredientsRemoteView(context, recipeItem, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    private static RemoteViews getIngredientsRemoteView(Context context, Recipes recipeItem,
                                                        int appWidgetId) {

        Intent recipeDetailIntent;
        //if the recipe is null then just open the main activity with all recipes
        if (recipeItem==null) {
            recipeDetailIntent=new Intent(context, SelectARecipeActivity.class);
        } else {
            recipeDetailIntent=new Intent(context, RecipeStepsIngredientsActivity.class);
            recipeDetailIntent.putExtra(context.getString(R.string.recipe_parcel), recipeItem);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, OVERALL_CLICK, recipeDetailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);



        CharSequence widgetText;
        if (recipeItem==null) {
            widgetText=context.getString(R.string.widget_error);
        } else {
            widgetText=  recipeItem.getRecipeName();
        }
        remoteViews.setTextViewText(R.id.tv_widget_text, widgetText);

        Intent populateListIntent=new Intent(context, RecipeWidgetService.class);
        populateListIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        String parcelTag=context.getString(R.string.recipe_parcel);
        Bundle bundle=new Bundle();
        bundle.putParcelable(parcelTag, recipeItem);
        populateListIntent.putExtra(parcelTag, bundle);
        remoteViews.setRemoteAdapter(appWidgetId, R.id.lv_widget_ingredients, populateListIntent);


        /*
            onclickpendingintent for the overall linear layout so when user clicks on recipe
            name the details come over
        */
        remoteViews.setOnClickPendingIntent(R.id.ll_widget_list, pendingIntent);

        //using the fillin intent and template with a diff req code
        Intent recipeIntentTemplate = new Intent(context, RecipeStepsIngredientsActivity.class);
        PendingIntent pendingIntentTemplate=PendingIntent.getActivity(context, ROW_CLICK, recipeIntentTemplate,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.lv_widget_ingredients, pendingIntentTemplate);

        return remoteViews;
    }

    //I am using shared prefs to save/retrieve the current recipe
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        String recipePref=context.getString(R.string.recipe_pref);
        String widgetRecipe=context.getString(R.string.widget_recipe);
        Recipes recipeItem=null;
        SharedPreferences widgetRecipePref= context.getSharedPreferences(recipePref, Context.MODE_PRIVATE );
        if (widgetRecipePref.contains(widgetRecipe)) {
            Gson gson = new Gson();
            String recipeJson = widgetRecipePref.getString(widgetRecipe, "");
            recipeItem = gson.fromJson(recipeJson, Recipes.class);
        }
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RecipeIngredientsWidgetService.startActionGetIngredients(context, recipeItem);
        }
    }

    public static void updateIngredientList(Context context, AppWidgetManager appWidgetManager,
                                            Recipes recipeItem, int[] appWidgetIds) {
        for (int appWidgetId: appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipeItem, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

