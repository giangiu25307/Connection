package com.example.connection.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.R;

public class SettingsFragment extends Fragment{

    private ConstraintLayout themeLayout;
    private SharedPreferences sharedPreferences;
    private String newTheme;

    public SettingsFragment (){

    }

    public SettingsFragment newInstance() {
        SettingsFragment settingsFragment = new SettingsFragment();
        return settingsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.settings_fragment, null);

        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        themeLayout = view.findViewById(R.id.themeLayout);
        themeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.select_theme_alert_layout);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                final RadioButton lightButton, darkButton, followSystemButton;
                final TextView cancelTextView, applyTextView;

                lightButton = alertDialog.findViewById(R.id.lightRadioButton);
                darkButton = alertDialog.findViewById(R.id.darkRadioButton);
                followSystemButton = alertDialog.findViewById(R.id.followSystemRadioButton);
                cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
                applyTextView = alertDialog.findViewById(R.id.applyTextView);

                String currentTheme = sharedPreferences.getString("appTheme", "light");

                if(currentTheme.equals("light")){
                    lightButton.setChecked(true);
                }else if(currentTheme.equals("dark")){
                    darkButton.setChecked(true);
                }else{
                    followSystemButton.setChecked(true);
                }

                lightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newTheme = "light";
                    }
                });

                darkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newTheme = "dark";
                    }
                });

                followSystemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newTheme = "auto";
                    }
                });

                cancelTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                applyTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changetheme(newTheme, alertDialog);
                    }
                });

            }
        });

        return view;
    }

    /*
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lightRadioButton:
                newTheme = "light";
                break;
            case R.id.darkRadioButton:
                newTheme = "dark";
                break;
            case R.id.followSystemRadioButton:
                newTheme = "auto";
                break;
            case R.id.applyTextView:
                changetheme(newTheme);
                break;
            default:
                break;
        }
    }
    */

    private void changetheme(String theme, AlertDialog alertDialog){

        sharedPreferences.edit().putString("appTheme", theme).apply();
        alertDialog.dismiss();
        getActivity().recreate();

    }

}
