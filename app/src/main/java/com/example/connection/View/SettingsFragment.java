package com.example.connection.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.R;

import java.nio.file.Paths;
import java.util.Objects;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private ConstraintLayout themeSettings, changePasswordSettings, forgottenPasswordSettings;
    private SharedPreferences sharedPreferences;
    private String newTheme;
    private ConnectionController connectionController;
    private Database database;
    private ChatController chatController;
    private int theme = R.style.AppTheme;
    private TextView themeOptionDescription;
    private Button editProfileButton;
    private int bgColor = R.color.mediumWhite;
    private int PICK_IMAGE = 1;
    private ImageView profilePic;
    private ConstraintLayout constraintLayout;

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

        editProfileButton = view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(this);

        themeSettings = view.findViewById(R.id.themeSettings);
        themeSettings.setOnClickListener(this);
        themeOptionDescription = view.findViewById(R.id.themeOptionDescription);
        themeOptionDescription.setText(Connection.lightOrDark);

        changePasswordSettings = view.findViewById(R.id.changePasswordSettings);
        changePasswordSettings.setOnClickListener(this);

        profilePic = view.findViewById(R.id.profilePic);
        constraintLayout=view.findViewById(R.id.constraintLayout);
        //setProfilePic();
        return view;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        switch (v.getId()) {
            case R.id.editProfileButton:
                editProfile(dialogBuilder);
                break;
            case R.id.themeSettings:
                manageTheme(dialogBuilder);
                break;
            case R.id.changePasswordSettings:
                changePassword(dialogBuilder);
                break;
            default:
                break;
        }
    }


    private void getCurrentSystemTheme() {
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

    private void editProfile(AlertDialog.Builder dialogBuilder){
        //AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        dialogBuilder.setView(R.layout.edit_profile_alert_dialog);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final TextView cancelTextView, applyTextView;
        final ImageView profilePic;
        cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
        applyTextView = alertDialog.findViewById(R.id.applyTextView);
        profilePic = alertDialog.findViewById(R.id.profilePic);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
                chooseImage();
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

            }
        });
    }

    private void changePassword(AlertDialog.Builder dialogBuilder){
        //AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        dialogBuilder.setView(R.layout.change_password_alert_dialog);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final TextView forgotPasswordButton, cancelTextView, confirmTextview;
        forgotPasswordButton = alertDialog.findViewById(R.id.forgotPasswordButton);
        cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
        confirmTextview = alertDialog.findViewById(R.id.confirmTextView);

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.forgotten_password_alert_dialog);
                final AlertDialog alertDialog2 = dialogBuilder.create();
                alertDialog2.show();

                final TextView cancelTextView, confirmTextview;
                cancelTextView = alertDialog2.findViewById(R.id.cancelTextView);
                confirmTextview = alertDialog2.findViewById(R.id.confirmTextView);

                cancelTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog2.dismiss();
                    }
                });

                confirmTextview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
        });

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        confirmTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void manageTheme(AlertDialog.Builder dialogBuilder) {
        //AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        dialogBuilder.setView(R.layout.select_theme_alert_dialog);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final LinearLayout lightButton, darkButton, followSystemButton;
        final TextView cancelTextView, applyTextView;

        lightButton = alertDialog.findViewById(R.id.lightButton);
        darkButton = alertDialog.findViewById(R.id.darkButton);
        followSystemButton = alertDialog.findViewById(R.id.followSystemButton);
        cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
        applyTextView = alertDialog.findViewById(R.id.applyTextView);


        String currentTheme = sharedPreferences.getString("appTheme", "light");
        if (currentTheme.equals("dark")) {
            theme = R.style.DarkTheme;
            newTheme = "dark";
            Connection.lightOrDark = "Dark";
            bgColor = R.color.lightLightBlack;
            darkButton.setBackgroundResource(R.drawable.set_current_theme_background_borderline);
        } else if (currentTheme.equals("auto")) {
            getCurrentSystemTheme();
            newTheme = "auto";
            Connection.lightOrDark = "Follow System";
            if (theme == R.style.DarkTheme) {
                bgColor = R.color.lightLightBlack;
            } else {
                bgColor = R.color.mediumWhite;
            }
            followSystemButton.setBackgroundResource(R.drawable.set_current_theme_background_borderline);
        } else {
            theme = R.style.AppTheme;
            newTheme = "light";
            Connection.lightOrDark = "Light";
            lightButton.setBackgroundResource(R.drawable.set_current_theme_background_borderline);
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
                theme = R.style.AppTheme;
                newTheme = "light";
                Connection.lightOrDark = "Light";
                lightButton.setBackgroundResource(R.drawable.set_current_theme_background_borderline);
                darkButton.setBackgroundResource(R.drawable.set_current_theme_background_borderless);
                followSystemButton.setBackgroundResource(R.drawable.set_current_theme_background_borderless);
            }
        });

        darkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theme = R.style.DarkTheme;
                newTheme = "dark";
                Connection.lightOrDark = "Dark";
                lightButton.setBackgroundResource(R.drawable.set_current_theme_background_borderless);
                darkButton.setBackgroundResource(R.drawable.set_current_theme_background_borderline);
                followSystemButton.setBackgroundResource(R.drawable.set_current_theme_background_borderless);
            }
        });

        followSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentSystemTheme();
                newTheme = "auto";
                Connection.lightOrDark = "Follow System";
                lightButton.setBackgroundResource(R.drawable.set_current_theme_background_borderless);
                darkButton.setBackgroundResource(R.drawable.set_current_theme_background_borderless);
                followSystemButton.setBackgroundResource(R.drawable.set_current_theme_background_borderline);
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

    private void changeTheme(String theme, AlertDialog alertDialog) {

        sharedPreferences.edit().putString("appTheme", theme).apply();
        alertDialog.dismiss();
        //getActivity().recreate();
        getActivity().setTheme(this.theme);
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (this.theme == R.style.AppTheme) {

            window.setStatusBarColor(getContext().getColor(R.color.mediumWhite));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
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

    private void chooseImage(){
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            if(resultCode == Activity.RESULT_OK){
                // Let's read picked image data - its URI
                Uri pickedImage = data.getData();
                // Let's read picked image path using content resolver
                String[] filePath = { MediaStore.MediaColumns.DATA };
                Cursor cursor = getContext().getContentResolver().query(pickedImage, filePath, null, null, null);
                if(cursor==null)return;
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                database.setProfilePic(imagePath);
                cursor.close();
                setProfilePic();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void setProfilePic(){
        Cursor c=database.getProfilePic();
        if(c==null)return;
        c.moveToFirst();
        Bitmap bitmap = BitmapFactory.decodeFile(c.getString(0));
        Drawable draw = new BitmapDrawable(getResources(), bitmap);
        profilePic.setImageTintList(null);
        profilePic.setImageDrawable(draw);
        c.close();
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE);
        }
    }

}
