package com.example.fatoumeh.shumanatorbakingapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.fatoumeh.shumanatorbakingapp.R;
import com.example.fatoumeh.shumanatorbakingapp.datamodel.Recipes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by fatoumeh on 05/06/2018.
 */

public class QueryUtils {

    private QueryUtils() {}

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final String RECIPE_URL="https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static int READ_TIMEOUT;
    private static int CONNECT_TIMEOUT;

    private static String JSON_ID;
    private static String JSON_NAME;
    private static String JSON_INGREDIENTS;
    private static String JSON_QUANTITY;
    private static String JSON_MEASURE;
    private static String JSON_INGREDIENT;
    private static String JSON_STEPS;
    private static String JSON_SHORT_DESCRIPTION;
    private static String JSON_DESCRIPTION;
    private static String JSON_VIDEO;
    private static String JSON_THUMBNAIL;
    private static String JSON_SERVINGS;
    private static String JSON_IMAGE;

    private static String HTTP_ERROR;
    private static String JSON_ERROR;
    private static String JSON_RETRIEVE_ERROR;

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        return isConnected;
    }

    public static ArrayList<Recipes> fetchAllRecipes(Context context) {
        setupStrings(context);
        URL url=getURL();
        String jsonResponse=null;
        try {
            jsonResponse=makeHttpQuery(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Recipes> recipes=new ArrayList<>();
        recipes=fetchRecipesFromJson(jsonResponse);
        return recipes;
    }

    private static void setupStrings(Context context) {
        READ_TIMEOUT=Integer.parseInt(context.getString(R.string.read_timeout));
        CONNECT_TIMEOUT=Integer.parseInt(context.getString(R.string.connect_timeout));
        HTTP_ERROR=context.getString(R.string.http_error);
        JSON_ERROR=context.getString(R.string.json_error);
        JSON_RETRIEVE_ERROR=context.getString(R.string.json_retrieve_error);

        JSON_ID=context.getString(R.string.json_id);
        JSON_NAME=context.getString(R.string.json_name);
        JSON_INGREDIENTS=context.getString(R.string.json_ingredients);
        JSON_QUANTITY=context.getString(R.string.json_quantity);
        JSON_MEASURE=context.getString(R.string.json_measure);
        JSON_INGREDIENT=context.getString(R.string.json_ingredient);
        JSON_STEPS=context.getString(R.string.json_steps);
        JSON_SHORT_DESCRIPTION=context.getString(R.string.json_shortDescription);
        JSON_DESCRIPTION=context.getString(R.string.json_description);
        JSON_VIDEO=context.getString(R.string.json_videoURL);
        JSON_THUMBNAIL=context.getString(R.string.json_thumbnailURL);
        JSON_SERVINGS=context.getString(R.string.json_servings);
        JSON_IMAGE=context.getString(R.string.json_image);
    }

    private static ArrayList<Recipes> fetchRecipesFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        ArrayList<Recipes> fetchedRecipes=new ArrayList<>();
        try {

            JSONArray baseJsonResponseArray = new JSONArray(jsonResponse);

            if (baseJsonResponseArray.length()>0) {
                for (int i=0; i<baseJsonResponseArray.length(); i++) {
                    JSONObject recipeDetails=baseJsonResponseArray.getJSONObject(i);

                    Integer id=recipeDetails.getInt(JSON_ID);
                    String name=recipeDetails.getString(JSON_NAME);
                    Integer servings=recipeDetails.getInt(JSON_SERVINGS);
                    String image=recipeDetails.getString(JSON_IMAGE);

                    JSONArray recipeIngredients=recipeDetails.getJSONArray(JSON_INGREDIENTS);
                    ArrayList<HashMap<String, String>> ingredientsList=fetchIngredients(recipeIngredients);

                    JSONArray recipeSteps=recipeDetails.getJSONArray(JSON_STEPS);
                    ArrayList<HashMap<String, String>> stepsList=fetchSteps(recipeSteps);

                    fetchedRecipes.add(new Recipes(id, name, ingredientsList,stepsList,servings,image));
                }
            }

        } catch (JSONException je) {
            Log.e(LOG_TAG,JSON_ERROR);
            je.printStackTrace();
        }
        return fetchedRecipes;
    }

    private static ArrayList<HashMap<String,String>> fetchSteps(JSONArray recipeSteps) {
        ArrayList<HashMap<String,String>> stepsDetails=new ArrayList<>();
        for (int i=0; i<recipeSteps.length(); i++) {
            try {
                HashMap<String, String> recipeStep=new HashMap<>();
                JSONObject ingredientObj=recipeSteps.getJSONObject(i);
                String id= String.valueOf(i);
                String shortdescription= ingredientObj.getString(JSON_SHORT_DESCRIPTION);
                String description= ingredientObj.getString(JSON_DESCRIPTION);
                String video=ingredientObj.getString(JSON_VIDEO);
                String thumbnail=ingredientObj.getString(JSON_THUMBNAIL);
                recipeStep.put(JSON_ID, id);
                recipeStep.put(JSON_SHORT_DESCRIPTION, shortdescription);
                recipeStep.put(JSON_DESCRIPTION, description);
                recipeStep.put(JSON_VIDEO, video);
                recipeStep.put(JSON_THUMBNAIL, thumbnail);
                stepsDetails.add(recipeStep);
            } catch (JSONException e) {
                Log.e(LOG_TAG,JSON_ERROR);
                e.printStackTrace();
            }
        }
        return stepsDetails;
    }

    private static ArrayList<HashMap<String,String>> fetchIngredients(JSONArray recipeIngredients) {
        ArrayList<HashMap<String,String>> ingredientsDetails=new ArrayList<>();
        for (int i=0; i<recipeIngredients.length(); i++) {
            try {
                JSONObject ingredientObj=recipeIngredients.getJSONObject(i);
                String quantity= String.valueOf(ingredientObj.getInt(JSON_QUANTITY));
                String measure=ingredientObj.getString(JSON_MEASURE);
                String ingredient=ingredientObj.getString(JSON_INGREDIENT);
                HashMap<String, String> recipeIngredient=new HashMap<>();
                recipeIngredient.put(JSON_QUANTITY, quantity);
                recipeIngredient.put(JSON_MEASURE, measure);
                recipeIngredient.put(JSON_INGREDIENT, ingredient);
                ingredientsDetails.add(recipeIngredient);
            } catch (JSONException e) {
                Log.e(LOG_TAG,JSON_ERROR);
                e.printStackTrace();
            }
        }
        return ingredientsDetails;
    }

    private static String makeHttpQuery(URL url) throws IOException {
        String jsonResponse="";
        if (url==null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT) ;
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, HTTP_ERROR+ urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, JSON_RETRIEVE_ERROR, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;

    }

    private static String readFromStream(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("\\A");
        boolean hasInput = scanner.hasNext();
        if (hasInput) {
            return scanner.next();
        } else {
            return null;
        }
    }

    private static URL getURL() {
        URL url=null;
        Uri builtUri=Uri.parse(RECIPE_URL);
        try {
            url= new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
