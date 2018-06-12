package com.example.fatoumeh.shumanatorbakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fatoumeh.shumanatorbakingapp.datamodel.Recipes;
import com.example.fatoumeh.shumanatorbakingapp.utils.QueryUtils;
import com.example.fatoumeh.shumanatorbakingapp.widget.RecipeIngredientsWidgetService;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectARecipeActivity extends AppCompatActivity implements RecipesAdapter.RecipesAdapterOnClickHandler {

    private static final String LOG_TAG = SelectARecipeActivity.class.getSimpleName();
    private int scrollPos;
    private String scrollPosKey;
    private boolean sharedPrefEditted;
    private GridLayoutManager gridLayoutManager;
    private RecipesAdapter recipesAdapter;
    @BindView(R.id.rv_recipe_list)
    RecyclerView rvRecipes;
    @BindView(R.id.tv_error)
    TextView tvError;
    @BindView(R.id.pb_progress)
    ProgressBar pbProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_a_recipe);
        ButterKnife.bind(this);
        setTitle(getString(R.string.select_a_recipe_label));

        int spanCount = getGridViewCount();
        gridLayoutManager = new GridLayoutManager(this, spanCount);
        recipesAdapter = new RecipesAdapter(this);
        rvRecipes.setLayoutManager(gridLayoutManager);
        rvRecipes.setAdapter(recipesAdapter);

        scrollPosKey = getString(R.string.scroll_position);
        scrollPos = 0;
        if (savedInstanceState != null) {
            scrollPos = savedInstanceState.getInt(scrollPosKey, 0);
        }

        if (getSharedPreferences(getString(R.string.recipe_pref), Context.MODE_PRIVATE)
                .contains(getString(R.string.widget_recipe))) {
            sharedPrefEditted = true;
        }

        if (QueryUtils.isConnected(this)) {
            tvError.setVisibility(View.GONE);
            fetchRecipes();
        } else {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(getString(R.string.connection_error));
        }

    }

    private void fetchRecipes() {
        recipesAdapter.setRecipeData(null);
        new FetchRecipesTask().execute();
    }

    private int getGridViewCount() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / 600);
    }

    private void updateWidget(Recipes recipeItem) {
        String recipePref = getString(R.string.recipe_pref);
        String widgetRecipe = getString(R.string.widget_recipe);
        SharedPreferences widgetPrefs = getSharedPreferences(recipePref, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String recipeJson = gson.toJson(recipeItem);
        widgetPrefs.edit().clear().commit();
        widgetPrefs.edit().putString(widgetRecipe, recipeJson).commit();
        if (widgetPrefs.contains(widgetRecipe)) {
            sharedPrefEditted = true;
        }

        RecipeIngredientsWidgetService.startActionGetIngredients(this, recipeItem);
    }

    @Override
    public void onClick(Recipes recipeItem) {
        updateWidget(recipeItem);
        Intent recipeDetailsIntent = new Intent(this, RecipeStepsIngredientsActivity.class);
        recipeDetailsIntent.putExtra(getString(R.string.recipe_parcel), recipeItem);
        startActivity(recipeDetailsIntent);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        scrollPos = gridLayoutManager.findFirstVisibleItemPosition();
        outState.putInt(scrollPosKey, scrollPos);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (scrollPos != RecyclerView.NO_POSITION) {
            gridLayoutManager.scrollToPosition(scrollPos);
        }
        if (getSharedPreferences(getString(R.string.recipe_pref),
                Context.MODE_PRIVATE).contains(getString(R.string.widget_recipe))) {
            sharedPrefEditted = true;
        }

    }


    public class FetchRecipesTask extends AsyncTask<Void, Void, ArrayList<Recipes>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Recipes> doInBackground(Void... voids) {
            ArrayList<Recipes> recipeList = new ArrayList<>();
            recipeList = QueryUtils.fetchAllRecipes(getApplicationContext());
            return recipeList;
        }

        @Override
        protected void onPostExecute(ArrayList<Recipes> recipes) {
            pbProgress.setVisibility(View.GONE);
            if (recipes != null) {
                tvError.setVisibility(View.GONE);
                rvRecipes.setVisibility(View.VISIBLE);
                recipesAdapter.setRecipeData(recipes);

                //set it to the first recipe if the key doesnt exist
                if (!getSharedPreferences(getString(R.string.recipe_pref), Context.MODE_PRIVATE)
                        .contains(getString(R.string.widget_recipe))) {
                    updateWidget(recipes.get(0));
                }

                if (scrollPos != RecyclerView.NO_POSITION) {
                    gridLayoutManager.scrollToPosition(scrollPos);
                }
            } else {
                tvError.setText(getString(R.string.no_recipes));
                tvError.setVisibility(View.VISIBLE);
                rvRecipes.setVisibility(View.GONE);
                super.onPostExecute(recipes);

            }
        }
    }
}
