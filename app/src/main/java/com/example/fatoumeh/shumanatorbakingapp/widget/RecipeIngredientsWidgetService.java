package com.example.fatoumeh.shumanatorbakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.os.Parcelable;

import com.example.fatoumeh.shumanatorbakingapp.R;
import com.example.fatoumeh.shumanatorbakingapp.RecipeStepsIngredientsActivity;
import com.example.fatoumeh.shumanatorbakingapp.datamodel.Recipes;


public class RecipeIngredientsWidgetService extends IntentService {

    private static final String ACTION_GET_RECIPE = "com.example.fatoumeh.shumanatorbakingapp.widget.action.get_recipe";
    private static final String ACTION_GET_INGREDIENTS= "com.example.fatoumeh.shumanatorbakingapp.widget.action.get_ingredients";

    /*
        instead of using com.example.fatoumeh.shumanatorbakingapp.widget.extra.recipe_parcel
         I'm reusing the recipe parcel used in other places
    */
    private static final String RECIPE_PARCEL="RecipeParcel";

    public RecipeIngredientsWidgetService() {
        super("RecipeIngredientsWidgetService");
    }

    public static void startActionGetIngredients(Context context, Recipes recipeItem) {
        Intent intent = new Intent(context, RecipeIngredientsWidgetService.class);
        intent.setAction(ACTION_GET_INGREDIENTS);
        intent.putExtra(RECIPE_PARCEL, recipeItem);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_RECIPE.equals(action)) {
                Parcelable recipeParcelable=intent.getParcelableExtra(RECIPE_PARCEL);
                final Recipes recipeItem = (Recipes) recipeParcelable;
                handleActionGetRecipe(recipeItem);
            } else if (ACTION_GET_INGREDIENTS.equals(action)) {
                Parcelable recipeParcelable=intent.getParcelableExtra(RECIPE_PARCEL);
                final Recipes recipeItem = (Recipes) recipeParcelable;
                handleActionGetIngredients(recipeItem);
            }
        }
    }


    private void handleActionGetRecipe(Recipes recipeItem) {
        Intent recipeDetailsIntent = new Intent(this, RecipeStepsIngredientsActivity.class);
        recipeDetailsIntent.putExtra(RECIPE_PARCEL, recipeItem);
        startActivity(recipeDetailsIntent);
    }

    private void handleActionGetIngredients(Recipes recipeItem) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_widget_ingredients);
        RecipeWidgetProvider.updateIngredientList(this, appWidgetManager, recipeItem, appWidgetIds);
    }
}
