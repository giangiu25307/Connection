package com.example.connection.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.connection.BuildConfig;
import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Database.Database;
import com.example.connection.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private ConstraintLayout themeSettings, changePasswordSettings, forgottenPasswordSettings, information, bugReport;
    private SharedPreferences sharedPreferences;
    private String newTheme;
    private ConnectionController connectionController;
    private Database database;
    private ChatController chatController;
    private int theme = R.style.AppTheme;
    private TextView themeOptionDescription, wallpaperOptionDescription;
    private ImageButton editProfileButton, logoutButton;
    private int PICK_IMAGE = 1, CAPTURE_IMAGE = 1337;
    private ImageView profilePic, profilePics;
    private String previousProfilePic = "";
    private ConstraintLayout wallpaperSettings;
    private MapFragment map;
    private ChatFragment chat;
    private SettingsFragment settingsFragment;
    private Connection connection;
    private boolean isOldPasswordShown, isNewPasswordShown, isNewPassword2Shown;
    private static final Pattern regexPassword = Pattern.compile("^" +
            "(?=.*[0-9])" + //at least 1 digit
            "(?=.*[a-z])" + //at least 1 lower case
            "(?=.*[A-Z])" + //at least 1 upper case
            "(?=.*[!?&%$#])" + //at least 1 special character of !?&%$#
            "(?=\\S+$)" + //no white spaces
            ".{8,}" + //at least length of 8 character
            "$");

    private static final Pattern regexName = Pattern.compile("^" +
            "(?=.*[a-z])" + //at least 1 lower case
            ".{2,26}" +
            "$");

    private static final Pattern regexPhoneNumber = Pattern.compile("^" +
            "[0-9]*" +
            ".{9,11}" +
            "$");

    public SettingsFragment() {
    }

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setMap(MapFragment map) {
        this.map = map;
    }

    public void setChat(ChatFragment chat) {
        this.chat = chat;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public SettingsFragment newInstance(Connection connection, ConnectionController connectionController, Database database, ChatController chatController, MapFragment map, ChatFragment chat) {
        settingsFragment = new SettingsFragment();
        settingsFragment.setConnection(connection);
        settingsFragment.setChatController(chatController);
        settingsFragment.setConnectionController(connectionController);
        settingsFragment.setDatabase(database);
        settingsFragment.setMap(map);
        settingsFragment.setChat(chat);
        return settingsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") final View view = inflater.inflate(R.layout.lyt_settings, null);

        setHasOptionsMenu(false);

        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        editProfileButton = view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(this);

        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);

        themeSettings = view.findViewById(R.id.themeSettings);
        themeSettings.setOnClickListener(this);
        //themeOptionDescription = view.findViewById(R.id.themeOptionDescription);
        //themeOptionDescription.setText(Connection.lightOrDark);

        changePasswordSettings = view.findViewById(R.id.changePasswordSettings);
        changePasswordSettings.setOnClickListener(this);

        wallpaperSettings = view.findViewById(R.id.wallpaperSettings);
        wallpaperSettings.setOnClickListener(this);

        information = view.findViewById(R.id.information);
        information.setOnClickListener(this);

        bugReport = view.findViewById(R.id.bugReport);
        bugReport.setOnClickListener(this);

        profilePic = view.findViewById(R.id.profilePic);

        //wallpaperOptionDescription = view.findViewById(R.id.wallpaperOptionDescription);

        setProfilePic();

        Cursor c = database.getBackgroundImage();
        if (c != null && c.getCount() > 0) {
            c.moveToLast();
            String imagePath = c.getString(0);
            String string[] = imagePath.split("/");
            wallpaperOptionDescription.setText(string[string.length - 1]);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        switch (v.getId()) {
            case R.id.editProfileButton:
                //editProfile(dialogBuilder);
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.logoutButton:
                openLogoutDialog(dialogBuilder);
                break;
            case R.id.wallpaperSettings:
                chooseBackgroundImage();
                break;
            case R.id.themeSettings:
                manageTheme(dialogBuilder);
                break;
            case R.id.changePasswordSettings:
                changePassword(dialogBuilder);
                break;
            case R.id.information:
                openInformationDialog(dialogBuilder);
                break;
            case R.id.bugReport:
                openBugReportDialog(dialogBuilder);
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

    private void changePassword(AlertDialog.Builder dialogBuilder) {
        //AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        dialogBuilder.setView(R.layout.dialog_change_password);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final EditText oldPassword, newPassword, newPassword2;
        final ImageButton showHideOldPassword, showHideNewPassword, showHideNewPassword2;
        oldPassword = alertDialog.findViewById(R.id.editTextOldPassword);
        newPassword = alertDialog.findViewById(R.id.editTextNewPassword);
        newPassword2 = alertDialog.findViewById(R.id.editTextNewPassword2);

        showHideOldPassword = alertDialog.findViewById(R.id.showHideOldPassword);
        showHideNewPassword = alertDialog.findViewById(R.id.showHideNewPassword);
        showHideNewPassword2 = alertDialog.findViewById(R.id.showHideNewPassword2);

        showHideOldPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHidePassword(isOldPasswordShown, oldPassword, showHideOldPassword);
                isOldPasswordShown = !isOldPasswordShown;
            }
        });

        showHideNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHidePassword(isNewPasswordShown, newPassword, showHideNewPassword);
                isNewPasswordShown = !isNewPasswordShown;
            }
        });

        showHideNewPassword2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHidePassword(isNewPassword2Shown, newPassword2, showHideNewPassword2);
                isNewPassword2Shown = !isNewPassword2Shown;
            }
        });


        final TextView forgotPasswordTextView;
        final Button cancelButton, confirmButton;
        forgotPasswordTextView = alertDialog.findViewById(R.id.forgotPasswordButton);
        cancelButton = alertDialog.findViewById(R.id.cancelTextView);
        confirmButton = alertDialog.findViewById(R.id.confirmTextView);

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.dialog_forgotten_password);
                final AlertDialog alertDialog2 = dialogBuilder.create();
                alertDialog2.show();

                final Button cancelButton, confirmTextview;
                final EditText emailEditText;
                cancelButton = alertDialog2.findViewById(R.id.cancelTextView);
                confirmTextview = alertDialog2.findViewById(R.id.confirmTextView);
                emailEditText = alertDialog2.findViewById(R.id.editTextEmail);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog2.dismiss();
                    }
                });

                confirmTextview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String data = database.getMyEmail();
                        if (emailEditText.getText().toString().equals(data)) {
                            emailEditText.setBackgroundResource(R.drawable.bg_input_data);
                            alertDialog2.dismiss();
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                            dialogBuilder.setView(R.layout.dialog_verify_code);
                            final AlertDialog alertDialog3 = dialogBuilder.create();
                            alertDialog3.show();
                            //SEND REQUEST TO MAKE VERIFICATION FROM THE SERVER AND TAKE BACK THE CODE
                        } else {
                            emailEditText.setBackgroundResource(R.drawable.bg_input_data_wrong);
                        }
                    }
                });

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                boolean oldPasswordBoolean = false, newPasswordBoolean = false;
                String data = database.getMyEmail();
                if (data.equals(oldPassword.getText().toString())){
                    oldPassword.setBackgroundResource(R.drawable.input_data_background);
                    oldPasswordBoolean = true;
                }else{
                    oldPasswordBoolean = false;
                    oldPassword.setBackgroundResource(R.drawable.input_data_background_wrong);
                }

                if(newPassword.getText().toString().equals(newPassword2.getText().toString()) && regexPassword.matcher(newPassword.getText().toString()).matches()) {
                    newPasswordBoolean = true;
                    newPassword.setBackgroundResource(R.drawable.input_data_background);
                    newPassword2.setBackgroundResource(R.drawable.input_data_background);
                }
                else {
                    newPasswordBoolean = false;
                    newPassword.setBackgroundResource(R.drawable.input_data_background_wrong);
                    newPassword2.setBackgroundResource(R.drawable.input_data_background_wrong);
                }
                if(oldPasswordBoolean && newPasswordBoolean){
                    database.setMyPassword(newPassword.getText().toString());
                    alertDialog.dismiss();
                }
                */
            }
        });
    }

    private void showHidePassword(boolean isShown, EditText password, ImageButton showHidePasswordButton) {
        if (!isShown) {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            showHidePasswordButton.setImageResource(R.drawable.ic_hide_password);
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            showHidePasswordButton.setImageResource(R.drawable.ic_show_password);
        }
        password.setSelection(password.getText().length());
    }

    private void manageTheme(AlertDialog.Builder dialogBuilder) {
        //AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        dialogBuilder.setView(R.layout.dialog_app_theme);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final Button lightButton, darkButton, followSystemButton, cancelButton, applyButton;

        lightButton = alertDialog.findViewById(R.id.lightButton);
        darkButton = alertDialog.findViewById(R.id.darkButton);
        followSystemButton = alertDialog.findViewById(R.id.followSystemButton);
        cancelButton = alertDialog.findViewById(R.id.cancelTextView);
        applyButton = alertDialog.findViewById(R.id.applyTextView);

        String currentTheme = sharedPreferences.getString("appTheme", "light");
        if (currentTheme.equals("dark")) {
            theme = R.style.DarkTheme;
            newTheme = "dark";
            Connection.lightOrDark = "Dark";
            darkButton.setBackgroundResource(R.drawable.bg_theme_card_borderline);
        } else if (currentTheme.equals("auto")) {
            getCurrentSystemTheme();
            newTheme = "auto";
            Connection.lightOrDark = "Follow System";
            followSystemButton.setBackgroundResource(R.drawable.bg_theme_card_borderline);
        } else {
            theme = R.style.AppTheme;
            newTheme = "light";
            Connection.lightOrDark = "Light";
            lightButton.setBackgroundResource(R.drawable.bg_theme_card_borderline);
        }

        lightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theme = R.style.AppTheme;
                newTheme = "light";
                Connection.lightOrDark = "Light";
                lightButton.setBackgroundResource(R.drawable.bg_theme_card_borderline);
                darkButton.setBackgroundResource(R.drawable.bg_theme_card_borderless);
                followSystemButton.setBackgroundResource(R.drawable.bg_theme_card_borderless);
            }
        });

        darkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theme = R.style.DarkTheme;
                newTheme = "dark";
                Connection.lightOrDark = "Dark";
                lightButton.setBackgroundResource(R.drawable.bg_theme_card_borderless);
                darkButton.setBackgroundResource(R.drawable.bg_theme_card_borderline);
                followSystemButton.setBackgroundResource(R.drawable.bg_theme_card_borderless);
            }
        });

        followSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentSystemTheme();
                newTheme = "auto";
                Connection.lightOrDark = "Follow System";
                lightButton.setBackgroundResource(R.drawable.bg_theme_card_borderless);
                darkButton.setBackgroundResource(R.drawable.bg_theme_card_borderless);
                followSystemButton.setBackgroundResource(R.drawable.bg_theme_card_borderline);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
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

            window.setStatusBarColor(getContext().getColor(R.color.colorPrimary));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.getDecorView().setSystemUiVisibility(0);
            //window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            window.setStatusBarColor(getContext().getColor(R.color.darkColorPrimary));
        }
        HomeFragment homeFragment = new HomeFragment();
        settingsFragment = newInstance(connection, connectionController, database, chatController, map, chat);
        Fragment fragment = homeFragment.newInstance(connection, connectionController, database, chatController, map, chat, settingsFragment);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, fragment);
        transaction.commit();
    }

    private void openLogoutDialog(AlertDialog.Builder dialogBuilder){
        dialogBuilder.setView(R.layout.dialog_logout);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        final Button cancel, logout;
        cancel = alertDialog.findViewById(R.id.cancelButton);
        logout = alertDialog.findViewById(R.id.logoutButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Implementare logica per effettuare il logout
            }
        });

    }

    private void openInformationDialog(AlertDialog.Builder dialogBuilder) {
        dialogBuilder.setView(R.layout.dialog_information);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        final LinearLayout rate, share;
        final Button close;
        rate = alertDialog.findViewById(R.id.rate);
        share = alertDialog.findViewById(R.id.share);
        close = alertDialog.findViewById(R.id.close);

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Connection");
                String shareMessage = "Start using Connection\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share Connection"));
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


    }

    private void openBugReportDialog(AlertDialog.Builder dialogBuilder) {
        dialogBuilder.setView(R.layout.dialog_bug_report);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        final Button cancelButton, sendButton;
        cancelButton = alertDialog.findViewById(R.id.cancelTextView);
        sendButton = alertDialog.findViewById(R.id.sendTextView);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sendBugReport(view.findViewById(R.id.editTextBugReport)))alertDialog.dismiss();
            }
        });


    }

    private boolean sendBugReport(TextView textView){
        OkHttpClient client= new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
                .build();
        // create your json here
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", ConnectionController.myUser.getIdUser());
            jsonObject.put("BugReport", textView.getText().toString());
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonObject.toString(),mediaType);

        Request request = new Request.Builder()
                .url("https://isconnection.herokuapp.com/bugReport")
                .post(body)
                .build();


        Call call = client.newCall(request);
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                Toast.makeText(getContext(),"Thanks for reporting", Toast.LENGTH_LONG);
            }else{
                Toast.makeText(getContext(),"Something went wrong", Toast.LENGTH_LONG);
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    private void captureImage() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    private void chooseBackgroundImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                // Let's read picked image data - its URI
                Uri pickedImage = data.getData();
                // Let's read picked image path using content resolver
                String[] filePath = {MediaStore.MediaColumns.DATA};
                Cursor cursor = getContext().getContentResolver().query(pickedImage, filePath, null, null, null);
                if (cursor == null) return;
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                database.setBackgroundImage(imagePath);
                String string[] = imagePath.split("/");
                wallpaperOptionDescription.setText(string[string.length - 1]);
                cursor.close();
            }
        } else if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getContext(), photo);
                // CALL THIS METHOD TO GET THE ACTUAL PATH
                String imagePath = getRealPathFromURI(tempUri);
                database.setProfilePic("0", imagePath);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Drawable draw = new BitmapDrawable(getResources(), bitmap);
                profilePic.setImageTintList(null);
                profilePic.setImageDrawable(draw);
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void setProfilePic() {
        Cursor c = database.getProfilePic(ConnectionController.myUser.getIdUser());
        if (c == null || c.getCount() == 0) {
            profilePic.setImageTintList(ColorStateList.valueOf(android.R.attr.iconTint));
            return;
        }
        c.moveToFirst();
        previousProfilePic = c.getString(0);
        Bitmap bitmap = BitmapFactory.decodeFile(c.getString(0));
        Drawable draw = new BitmapDrawable(getResources(), bitmap);
        profilePic.setImageTintList(null);
        profilePic.setImageDrawable(draw);
        c.close();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContext().getContentResolver() != null) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

}
