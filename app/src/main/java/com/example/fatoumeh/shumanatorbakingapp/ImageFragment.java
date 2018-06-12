package com.example.fatoumeh.shumanatorbakingapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;

/**
 * Created by fatoumeh on 08/06/2018.
 */

public class ImageFragment extends Fragment {
    private int defaultImg;
    private String thumbnailUrl;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_img, container, false);
        imageView=ButterKnife.findById(rootView, R.id.img_no_video);

        if (savedInstanceState!=null) {
            if (savedInstanceState.getString(getString(R.string.json_thumbnailURL))!=null) {
                thumbnailUrl=savedInstanceState.getString(getString(R.string.json_thumbnailURL));
            } else {
                defaultImg=savedInstanceState.getInt(getString(R.string.default_img));
            }
        }

        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Picasso.with(getContext())
                    .load(Uri.parse(thumbnailUrl))
                    .error(R.mipmap.ic_launcher)
                    .into(imageView);
        } else {
            Picasso.with(getContext())
                    .load(defaultImg)
                    .error(R.mipmap.ic_launcher)
                    .into(imageView);
            imageView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        }


        return rootView;
    }

    public void setThumbnailImg(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setDefaultImg(int defaultImg) {
        this.defaultImg = defaultImg;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            outState.putString(getString(R.string.json_thumbnailURL), thumbnailUrl);
        } else {
            outState.putInt(getString(R.string.default_img), defaultImg);
        }
    }
}
