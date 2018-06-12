package com.example.fatoumeh.shumanatorbakingapp;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.fatoumeh.shumanatorbakingapp.datamodel.Recipes;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;

public class RecipeStepsIngredientsActivity extends AppCompatActivity implements MasterListFragment.RecipeStepClickListener {


    private Recipes selectedRecipe;
    private String recipeName;
    private int stepId;
    private ArrayList<HashMap<String, String>> ingredientsList;
    private ArrayList<HashMap<String, String>> stepsList;
    private boolean showTwoPanes;
    private Button nextButton, prevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String recipeParcel=getString(R.string.recipe_parcel);
        if (!getIntent().hasExtra(recipeParcel)) {
            Toast.makeText(this, getString(R.string.no_recipe_details), Toast.LENGTH_LONG).show();
            return;
        }

        Parcelable recipeParcelable=getIntent().getParcelableExtra(recipeParcel);
        selectedRecipe = (Recipes) recipeParcelable;
        ingredientsList=selectedRecipe.getRecipeIngredientsList();
        stepsList=selectedRecipe.getRecipeStepsList();

        recipeName=selectedRecipe.getRecipeName();

        //setting the step id to -1 since we will never have step -1 in the list
        stepId=-1;

        if (savedInstanceState!=null ) {
            stepId=savedInstanceState.getInt(getString(R.string.step_id));
        }

        //need to call setContentView after the arraylists are populated
        setContentView(R.layout.activity_recipe_steps_ingredients);
        setTitle(recipeName);

        //it must be the large screen
        if (findViewById(R.id.ll_ingredients_steps_large)!=null) {
            showTwoPanes=true;
            nextButton= ButterKnife.findById(this, R.id.bt_next);
            prevButton=ButterKnife.findById(this, R.id.bt_previous);

            if (stepId<0) {
                prevButton.setEnabled(false);
            } else {
                prevButton.setEnabled(true);
            }

        } else {
            showTwoPanes=false;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getString(R.string.step_id), stepId);
    }

    public ArrayList<HashMap<String, String>> getIngredientsList() {
        return ingredientsList;
    }

    public ArrayList<HashMap<String, String>> getStepsList() {
        return stepsList;
    }


    @Override
    public void onRecipeStepClick(HashMap<String, String> recipeStep) {
        if (showTwoPanes) {
            goToStep(recipeStep);
        } else {
            final Intent playVideoIntent =new Intent(this, PlayRecipeVideoActivity.class);
            playVideoIntent.putExtra(getString(R.string.recipe_parcel), selectedRecipe);
            playVideoIntent.putExtra(getString(R.string.recipe_steps_id), recipeStep.get(getString(R.string.json_id)));
            startActivity(playVideoIntent);
        }
    }

    private void goToStep(HashMap<String, String> recipeStep) {
        stepId=Integer.parseInt(recipeStep.get(getString(R.string.json_id)));
        String recipeShortDescription=recipeStep.get(getString(R.string.json_shortDescription));
        String recipeLongDescription=recipeStep.get(getString(R.string.json_description));
        String videoURL=recipeStep.get(getString(R.string.json_videoURL));
        String thumbnailURL=recipeStep.get(getString(R.string.json_thumbnailURL));

        FragmentManager fragmentManager=getSupportFragmentManager();

        RecipeStepsFragment recipeStepsFragment=new RecipeStepsFragment();
        if (!TextUtils.isEmpty(recipeLongDescription)) {
            recipeStepsFragment.setDetailedStep(recipeLongDescription);
        } else {
            recipeStepsFragment.setDetailedStep(recipeShortDescription);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.step_instruction_container, recipeStepsFragment)
                .commit();

        if (!TextUtils.isEmpty(videoURL) || !TextUtils.isEmpty(thumbnailURL)) {

            if (!TextUtils.isEmpty(videoURL)) {
                RecipeStepsVideoFragment recipeStepsVideoFragment=new RecipeStepsVideoFragment();
                recipeStepsVideoFragment.setVideoUrl(videoURL);
                fragmentManager.beginTransaction()
                        .replace(R.id.video_container, recipeStepsVideoFragment)
                        .commit();
            } else {
                /*
                    thumbnailurl won't be empty
                    Pls note: I am assuming thumbnailURL is not a video since it's supposed to
                    be an image url. If android cannot load it, it'll display a default error img
                 */
                ImageFragment imageFragment=new ImageFragment();
                imageFragment.setThumbnailImg(thumbnailURL);
                fragmentManager.beginTransaction()
                        .replace(R.id.video_container, imageFragment)
                        .commit();
                Toast.makeText(this, getString(R.string.no_recipe_video), Toast.LENGTH_SHORT).show();
            }

        } else {
            //both urls must be null
            ImageFragment imageFragment=new ImageFragment();
            imageFragment.setDefaultImg(R.mipmap.ic_launcher);
            fragmentManager.beginTransaction()
                    .replace(R.id.video_container, imageFragment)
                    .commit();
            Toast.makeText(this, getString(R.string.no_recipe_video_img), Toast.LENGTH_SHORT).show();
        }


        if (stepId==0) {
            prevButton.setEnabled(false);
        } else {
            prevButton.setEnabled(true);
        }

        if (stepId==selectedRecipe.getRecipeStepsList().size()-1) {
            nextButton.setEnabled(false);
        } else {
            nextButton.setEnabled(true);
        }

    }

    private void navigateToStep(int navigateToStepId) {
        HashMap<String, String> recipeForStepId=getStepsList().get(navigateToStepId);
        goToStep(recipeForStepId);
    }



    public void nextStep(View view) {
        if (stepId<selectedRecipe.getRecipeStepsList().size()-1) {
            stepId++;
            navigateToStep(stepId);
        }
    }

    public void prevStep(View view) {
        if (stepId>0) {
            stepId--;
            navigateToStep(stepId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
