package com.example.fatoumeh.shumanatorbakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;

/**
 * Created by fatoumeh on 08/06/2018.
 */

public class RecipeStepsFragment extends Fragment {
    private String detailedStep;
    public RecipeStepsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            detailedStep=savedInstanceState.getString(getString(R.string.json_description));
        }

        View rootView=inflater.inflate(R.layout.fragment_steps, container,false);
        final TextView tvStep=ButterKnife.findById(rootView, R.id.tv_detailed_steps);

        if (!TextUtils.isEmpty(detailedStep)) {
            tvStep.setText(detailedStep);
        }
        return rootView;

    }

    public void setDetailedStep(String step) {
        detailedStep=step;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(getString(R.string.json_description), detailedStep);
    }
}
