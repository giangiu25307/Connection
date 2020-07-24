package com.example.connection.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.R;

public class SettingsFragment extends Fragment {

    private ConstraintLayout themeLayout;
    private SharedPreferences sharedPreferences;
    private String newTheme;
    private ConnectionController connectionController;
    private Database database;
    private ChatController chatController;
    private int theme = R.style.AppTheme;
    private TextView themeOptionDescription;
    private int bgColor=R.color.mediumWhite;

    public SettingsFragment() {

    }

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public SettingsFragment newInstance(ConnectionController connectionController, Database database, ChatController chatController) {
        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setChatController(chatController);
        settingsFragment.setConnectionController(connectionController);
        settingsFragment.setDatabase(database);
        return settingsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") final View view = inflater.inflate(R.layout.settings_fragment, null);

        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        themeLayout = view.findViewById(R.id.themeLayout);
        themeOptionDescription= view.findViewById(R.id.themeOptionDescription);
        themeOptionDescription.setText(Connection.lightOrDark);
        themeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.select_theme_alert_layout);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                final CardView lightButton, darkButton, followSystemButton;
                final TextView cancelTextView, applyTextView;

                lightButton = alertDialog.findViewById(R.id.lightButton);
                darkButton = alertDialog.findViewById(R.id.darkButton);
                followSystemButton = alertDialog.findViewById(R.id.followSystemButton);
                cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
                applyTextView = alertDialog.findViewById(R.id.applyTextView);

                String currentTheme = sharedPreferences.getString("appTheme", "light");
                if(currentTheme.equals("dark")){
                    theme=R.style.DarkTheme;
                    newTheme = "dark";
                    Connection.lightOrDark="Dark";
                    bgColor=R.color.lightLightBlack;
                    darkButton.setBackgroundResource(R.drawable.set_current_theme_background);
                }else if(currentTheme.equals("auto")){
                    getCurrentSystemTheme();
                    newTheme = "auto";
                    Connection.lightOrDark="Follow System";
                    if(theme==R.style.DarkTheme){
                        bgColor=R.color.lightLightBlack;
                    }else{
                        bgColor=R.color.mediumWhite;
                    }
                    followSystemButton.setCardBackgroundColor(R.drawable.set_current_theme_background);
                }else{
                    theme=R.style.AppTheme;
                    newTheme = "light";
                    Connection.lightOrDark="Light";
                    lightButton.setBackgroundResource(R.drawable.set_current_theme_background);
                }
                /*
                if (currentTheme.equals("light")) {
                    lightButton.setChecked(true);
                } else if (currentTheme.equals("dark")) {
                    darkButton.setChecked(true);
                } else {
                    followSystemButton.setChecked(true);
                }
                */

                lightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        theme=R.style.AppTheme;
                        newTheme = "light";
                        Connection.lightOrDark="Light";
                        lightButton.setCardBackgroundColor(getContext().getColor(R.color.transparent));
                        lightButton.setBackgroundResource(R.drawable.set_current_theme_background);
                        darkButton.setCardBackgroundColor(getContext().getColor(bgColor));
                        darkButton.setBackgroundResource(0);
                        followSystemButton.setCardBackgroundColor(getContext().getColor(bgColor));
                        followSystemButton.setBackgroundResource(0);
                    }
                });

                darkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        theme=R.style.DarkTheme;
                        newTheme = "dark";
                        Connection.lightOrDark="Dark";
                        lightButton.setCardBackgroundColor(getContext().getColor(bgColor));
                        lightButton.setBackgroundResource(0);
                        darkButton.setCardBackgroundColor(getContext().getColor(R.color.transparent));
                        darkButton.setBackgroundResource(R.drawable.set_current_theme_background);
                        followSystemButton.setCardBackgroundColor(getContext().getColor(bgColor));
                        followSystemButton.setBackgroundResource(0);
                    }
                });

                followSystemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCurrentSystemTheme();
                        newTheme = "auto";
                        Connection.lightOrDark="Follow System";
                        lightButton.setCardBackgroundColor(getContext().getColor(bgColor));
                        lightButton.setBackgroundResource(0);
                        darkButton.setCardBackgroundColor(getContext().getColor(bgColor));
                        darkButton.setBackgroundResource(0);
                        followSystemButton.setCardBackgroundColor(getContext().getColor(R.color.transparent));
                        followSystemButton.setBackgroundResource(R.drawable.set_current_theme_background);
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
                        changeTheme(newTheme, alertDialog);
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

    private void getCurrentSystemTheme(){
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                theme = R.style.AppTheme;
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                theme = R.style.DarkTheme;
                break;
            default:
                break;
        }
    }

    private void changeTheme(String theme, AlertDialog alertDialog) {

        sharedPreferences.edit().putString("appTheme", theme).apply();
        alertDialog.dismiss();
        //getActivity().recreate();
        getActivity().setTheme(this.theme);
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if(this.theme == R.style.AppTheme){

            window.setStatusBarColor(getContext().getColor(R.color.mediumWhite));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }else{
            window.getDecorView().setSystemUiVisibility(0);
            //window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            window.setStatusBarColor(getContext().getColor(R.color.mediumBlack));
        }
        HomeFragment homeFragment = new HomeFragment();
        Fragment fragment = homeFragment.newInstance(connectionController, database, chatController);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, fragment);
        transaction.commit();
    }

}
