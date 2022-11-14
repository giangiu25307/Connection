package com.ConnectionProject.connection.View;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ConnectionProject.connection.R;

public class SplashScreenFragment extends Fragment {

    private TextView splashScreenTitle;
    private Typeface lightType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.lyt_splash_screen, null);

        /*
        lightType = Typeface.createFromAsset(getContext().getAssets(), "fonts/nunito_light.ttf");
        splashScreenTitle = view.findViewById(R.id.splashscreen_title);
        splashScreenTitle.setTypeface(lightType);
        */

        return view;
    }

}
