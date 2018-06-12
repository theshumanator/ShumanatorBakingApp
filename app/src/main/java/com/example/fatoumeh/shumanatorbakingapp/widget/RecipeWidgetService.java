package com.example.fatoumeh.shumanatorbakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;


/**
 * Created by fatoumeh on 09/06/2018.
 */

public class RecipeWidgetService  extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new IngredientsListRemoteViewsFactory(this.getApplicationContext(), intent));
    }
}