package com.example.fatoumeh.shumanatorbakingapp;

import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.fatoumeh.shumanatorbakingapp.datamodel.Recipes;

import java.util.HashMap;

import butterknife.ButterKnife;

public class PlayRecipeVideoActivity extends AppCompatActivity {

    private int stepId;
    private Recipes selectedRecipe;
    private Button nextButton, prevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_recipe_video);
        nextButton= ButterKnife.findById(this, R.id.bt_next);
        prevButton=ButterKnife.findById(this, R.id.bt_previous);

        String recipeParcel=getString(R.string.recipe_parcel);
        String recipeStepId=getString(R.string.recipe_steps_id);

        if (!getIntent().hasExtra(recipeParcel) || !getIntent().hasExtra(recipeStepId)) {
            Toast.makeText(this, getString(R.string.no_recipe_details), Toast.LENGTH_LONG).show();
            return;
        }

        Parcelable recipeParcelable=getIntent().getParcelableExtra(recipeParcel);
        selectedRecipe = (Recipes) recipeParcelable;
        setTitle(selectedRecipe.getRecipeName());

        //we need this id to navigate to next step
        stepId=Integer.parseInt(getIntent().getStringExtra(recipeStepId));

        if (stepId<0) {
            prevButton.setEnabled(false);
        } else {
            prevButton.setEnabled(true);
        }

        //if we dont do below, the recipe steps will start from the beginning every time we rotate
        if (savedInstanceState==null) {
            navigateToStep(stepId);
        } else {
            stepId=savedInstanceState.getInt(getString(R.string.step_id));
            String recipeShortDescription=selectedRecipe.getRecipeStepsList().get(stepId).get(getString(R.string.json_shortDescription));
            String recipeLongDescription=selectedRecipe.getRecipeStepsList().get(stepId).get(getString(R.string.json_description));
            FragmentManager fragmentManager=getSupportFragmentManager();

            if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT) {
                RecipeStepsFragment recipeStepsFragment=new RecipeStepsFragment();
                if (!TextUtils.isEmpty(recipeLongDescription)) {
                    recipeStepsFragment.setDetailedStep(recipeLongDescription);
                } else {
                    recipeStepsFragment.setDetailedStep(recipeShortDescription);
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.step_instruction_container, recipeStepsFragment)
                        .commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getString(R.string.step_id), stepId);
    }

    private void navigateToStep(int navigateToStepId) {
        HashMap<String, String> recipeForStepId=selectedRecipe.getRecipeStepsList().get(navigateToStepId);
        goToStep(recipeForStepId);
    }

    private void goToStep(HashMap<String, String> recipeStep) {
        stepId=Integer.parseInt(recipeStep.get(getString(R.string.json_id)));
        String recipeShortDescription=recipeStep.get(getString(R.string.json_shortDescription));
        String recipeLongDescription=recipeStep.get(getString(R.string.json_description));
        String videoURL=recipeStep.get(getString(R.string.json_videoURL));
        String thumbnailURL=recipeStep.get(getString(R.string.json_thumbnailURL));

        FragmentManager fragmentManager=getSupportFragmentManager();

        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT) {
            RecipeStepsFragment recipeStepsFragment=new RecipeStepsFragment();
            if (!TextUtils.isEmpty(recipeLongDescription)) {
                recipeStepsFragment.setDetailedStep(recipeLongDescription);
            } else {
                recipeStepsFragment.setDetailedStep(recipeShortDescription);
            }

            fragmentManager.beginTransaction()
                    .replace(R.id.step_instruction_container, recipeStepsFragment)
                    .commit();
        }

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
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
