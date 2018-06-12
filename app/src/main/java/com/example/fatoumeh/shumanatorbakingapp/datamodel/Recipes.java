package com.example.fatoumeh.shumanatorbakingapp.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fatoumeh on 05/06/2018.
 */

public class Recipes implements Parcelable {

    private Integer recipeId;
    private String recipeName;
    private ArrayList<HashMap<String, String>> recipeIngredientsList;
    private ArrayList<HashMap<String, String>> recipeStepsList;
    private Integer recipeServings;
    private String recipeImage;

    public Recipes(Integer recipeId, String recipeName, ArrayList<HashMap<String, String>> recipeIngredientsList,
                   ArrayList<HashMap<String, String>> recipeStepsList, Integer recipeServings, String recipeImage) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.recipeIngredientsList = recipeIngredientsList;
        this.recipeStepsList = recipeStepsList;
        this.recipeServings = recipeServings;
        this.recipeImage = recipeImage;
    }

    public Recipes(Parcel recipeParcel) {
        this.recipeId = recipeParcel.readInt();
        this.recipeName = recipeParcel.readString();
        this.recipeIngredientsList = recipeParcel.readArrayList(ArrayList.class.getClassLoader());
        this.recipeStepsList = recipeParcel.readArrayList(ArrayList.class.getClassLoader());
        this.recipeServings = recipeParcel.readInt();
        this.recipeImage = recipeParcel.readString();
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public ArrayList<HashMap<String, String>> getRecipeIngredientsList() {
        return recipeIngredientsList;
    }

    public void setRecipeIngredientsList(ArrayList<HashMap<String, String>> recipeIngredientsList) {
        this.recipeIngredientsList = recipeIngredientsList;
    }

    public ArrayList<HashMap<String, String>> getRecipeStepsList() {
        return recipeStepsList;
    }

    public void setRecipeStepsList(ArrayList<HashMap<String, String>> recipeStepsList) {
        this.recipeStepsList = recipeStepsList;
    }

    public Integer getRecipeServings() {
        return recipeServings;
    }

    public void setRecipeServings(Integer recipeServings) {
        this.recipeServings = recipeServings;
    }

    public String getRecipeImage() {
        return recipeImage;
    }

    public void setRecipeImage(String recipeImage) {
        this.recipeImage = recipeImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.recipeId);
        parcel.writeString(this.recipeName);
        parcel.writeList(this.recipeIngredientsList);
        parcel.writeList(this.recipeStepsList);
        parcel.writeInt(this.recipeServings);
        parcel.writeInt(this.recipeId);
    }

    public static final Parcelable.Creator CREATOR=new Parcelable.Creator() {

        @Override
        public Recipes createFromParcel(Parcel parcel) {
            return new Recipes(parcel);
        }

        @Override
        public Object[] newArray(int size) {
            return new Recipes[size];
        }
    };

}
