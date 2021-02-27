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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Model.User;
import com.example.connection.R;

import java.util.regex.Pattern;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private ConstraintLayout themeSettings, changePasswordSettings, forgottenPasswordSettings;
    private SharedPreferences sharedPreferences;
    private String newTheme;
    private ConnectionController connectionController;
    private Database database;
    private ChatController chatController;
    private int theme = R.style.AppTheme;
    private TextView themeOptionDescription, wallpaperOptionDescription;
    private ImageView editProfileButton;
    private int bgColor = R.color.mediumWhite;
    private int PICK_IMAGE = 1, CAPTURE_IMAGE = 1337;
    private ImageView profilePic, profilePics;
    private String previousProfilePic = "";
    private boolean isBg = false;
    private ConstraintLayout wallpaperSettings;
    private MapFragment map;
    private ChatFragment chat;
    private SettingsFragment settingsFragment;
    private Connection connection;
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

    public SettingsFragment newInstance(Connection connection,ConnectionController connectionController, Database database, ChatController chatController, MapFragment map, ChatFragment chat) {
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

        themeSettings = view.findViewById(R.id.themeSettings);
        themeSettings.setOnClickListener(this);
        themeOptionDescription = view.findViewById(R.id.themeOptionDescription);
        themeOptionDescription.setText(Connection.lightOrDark);

        changePasswordSettings = view.findViewById(R.id.changePasswordSettings);
        changePasswordSettings.setOnClickListener(this);

        wallpaperSettings = view.findViewById(R.id.wallpaperSettings);
        wallpaperSettings.setOnClickListener(this);

        profilePic = view.findViewById(R.id.profilePic);

        wallpaperOptionDescription = view.findViewById(R.id.wallpaperOptionDescription);

        setProfilePic();

        Cursor c = database.getBacgroundImage();
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
                editProfile(dialogBuilder);
                break;
            case R.id.themeSettings:
                manageTheme(dialogBuilder);
                break;
            case R.id.changePasswordSettings:
                changePassword(dialogBuilder);
                break;
            case R.id.wallpaperSettings:
                chooseBackgroundImage();
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

    private void editProfile(AlertDialog.Builder dialogBuilder) {
        //AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        dialogBuilder.setView(R.layout.dialog_edit_profile);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final TextView cancelTextView, applyTextView, gallery, takePhoto,textViewGender;
        final EditText editTextUsername, editTextMail, editTextName, editTextSurname, editTextPhoneNumber, editTextCity, editTextCountry;

        editTextCity = alertDialog.findViewById(R.id.editTextCity);
        editTextUsername = alertDialog.findViewById(R.id.editTextUsername);
        editTextMail = alertDialog.findViewById(R.id.editTextMail);
        editTextName = alertDialog.findViewById(R.id.editTextName);
        editTextSurname = alertDialog.findViewById(R.id.editTextSurname);
        editTextPhoneNumber = alertDialog.findViewById(R.id.editTextPhonenumber);
        textViewGender = alertDialog.findViewById(R.id.textViewGender);
        editTextCountry = alertDialog.findViewById(R.id.editTextCountry);

        User user = new User(database.getMyInformation()[0], database.getMyInformation()[1], database.getMyInformation()[2], database.getMyInformation()[3], database.getMyInformation()[4], database.getMyInformation()[5], database.getMyInformation()[6], database.getMyInformation()[7], database.getMyInformation()[8], database.getMyInformation()[9], database.getMyInformation()[10]);

        editTextCity.setText(user.getCity());
        editTextUsername.setText(user.getUsername());
        editTextMail.setText(user.getMail());
        editTextName.setText(user.getName());
        editTextSurname.setText(user.getSurname());
        editTextPhoneNumber.setText(user.getNumber());
        textViewGender.setText(user.getGender());
        editTextCountry.setText(user.getCountry());

        takePhoto = alertDialog.findViewById(R.id.textView13);
        gallery = alertDialog.findViewById(R.id.textView14);
        cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
        applyTextView = alertDialog.findViewById(R.id.applyTextView);
        profilePics = alertDialog.findViewById(R.id.profilePic);
        if (!previousProfilePic.equals("")) {
            Bitmap bitmap = BitmapFactory.decodeFile(previousProfilePic);
            Drawable draw = new BitmapDrawable(getResources(), bitmap);
            profilePics.setImageTintList(null);
            profilePics.setImageDrawable(draw);
        }
        textViewGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.dialog_gender);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                TextView male = alertDialog.findViewById(R.id.male), female = alertDialog.findViewById(R.id.female), other = alertDialog.findViewById(R.id.other);
                male.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textViewGender.setText("Maschio");
                        alertDialog.dismiss();
                    }
                });
                female.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textViewGender.setText("Femmina");
                        alertDialog.dismiss();
                    }
                });
                other.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textViewGender.setText("Altro");
                        alertDialog.dismiss();
                    }
                });
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.setProfilePic("0",previousProfilePic);
                alertDialog.dismiss();
            }
        });

        applyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfilePic();
                boolean bool = true;
                if (!regexName.matcher(editTextCity.getText().toString()).matches()) {
                    editTextCity.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background_wrong));
                    bool = false;
                } else {
                    editTextCity.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background));
                }
                if (!regexName.matcher(editTextUsername.getText().toString()).matches()) {
                    editTextUsername.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background_wrong));
                    bool = false;
                } else {
                    editTextUsername.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background));
                }
                if (!regexName.matcher(editTextName.getText().toString()).matches()) {
                    editTextName.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background_wrong));
                    bool = false;
                } else {
                    editTextName.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background));
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(editTextMail.getText().toString()).matches()) {
                    editTextMail.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background_wrong));
                    bool = false;
                } else {
                    editTextMail.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background));
                }
                if (!regexName.matcher(editTextSurname.getText().toString()).matches()) {
                    editTextSurname.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background_wrong));
                    bool = false;
                } else {
                    editTextSurname.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background));
                }
                if (!regexPhoneNumber.matcher(editTextPhoneNumber.getText().toString()).matches()) {
                    editTextPhoneNumber.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background_wrong));
                    bool = false;
                } else {
                    editTextPhoneNumber.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background));
                }
                if (!regexName.matcher(editTextCountry.getText().toString()).matches()) {
                    editTextCountry.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background_wrong));
                    bool = false;
                } else {
                    editTextCountry.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background));
                }
                if (bool) {
                    database.setCity("0", editTextCity.getText().toString());
                    database.setUsername("0", editTextUsername.getText().toString());
                    database.setName("0", editTextName.getText().toString());
                    database.setMail("0", editTextMail.getText().toString());
                    database.setSurname("0", editTextSurname.getText().toString());
                    database.setNumber("0", editTextPhoneNumber.getText().toString());
                    database.setGender("0", textViewGender.getText().toString());
                    database.setCountry("0", editTextCountry.getText().toString());
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void changePassword(AlertDialog.Builder dialogBuilder) {
        //AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        dialogBuilder.setView(R.layout.dialog_change_password);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final EditText oldPassword, newPassword, newPassword2;
        oldPassword = alertDialog.findViewById(R.id.editTextOldPassword);
        newPassword = alertDialog.findViewById(R.id.editTextNewPassword);
        newPassword2 = alertDialog.findViewById(R.id.editTextNewPassword2);


        final TextView forgotPasswordButton, cancelTextView, confirmTextview;
        forgotPasswordButton = alertDialog.findViewById(R.id.forgotPasswordButton);
        cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
        confirmTextview = alertDialog.findViewById(R.id.confirmTextView);

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.dialog_forgotten_password);
                final AlertDialog alertDialog2 = dialogBuilder.create();
                alertDialog2.show();

                final TextView cancelTextView, confirmTextview;
                final EditText emailEditText;
                cancelTextView = alertDialog2.findViewById(R.id.cancelTextView);
                confirmTextview = alertDialog2.findViewById(R.id.confirmTextView);
                emailEditText = alertDialog2.findViewById(R.id.editTextEmail);

                cancelTextView.setOnClickListener(new View.OnClickListener() {
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
                            emailEditText.setBackgroundResource(R.drawable.input_data_background);
                            alertDialog2.dismiss();
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                            dialogBuilder.setView(R.layout.dialog_verify_code_alert);
                            final AlertDialog alertDialog3 = dialogBuilder.create();
                            alertDialog3.show();
                            //SEND REQUEST TO MAKE VERIFICATION FROM THE SERVER AND TAKE BACK THE CODE
                        } else {
                            emailEditText.setBackgroundResource(R.drawable.input_data_background_wrong);
                        }
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

    private void manageTheme(AlertDialog.Builder dialogBuilder) {
        //AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        dialogBuilder.setView(R.layout.dialog_app_theme);
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
            bgColor = R.color.black4;
            darkButton.setBackgroundResource(R.drawable.set_current_theme_background_borderline);
        } else if (currentTheme.equals("auto")) {
            getCurrentSystemTheme();
            newTheme = "auto";
            Connection.lightOrDark = "Follow System";
            if (theme == R.style.DarkTheme) {
                bgColor = R.color.black4;
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

            window.setStatusBarColor(getContext().getColor(R.color.colorPrimary));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.getDecorView().setSystemUiVisibility(0);
            //window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            window.setStatusBarColor(getContext().getColor(R.color.darkColorPrimary));
        }
        HomeFragment homeFragment = new HomeFragment();
        settingsFragment = newInstance(connection,connectionController,database,chatController,map,chat);
        Fragment fragment = homeFragment.newInstance(connection,connectionController,database, chatController, map, chat,settingsFragment);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, fragment);
        transaction.commit();
    }

    private void captureImage() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    private void chooseImage() {
        isBg = false;
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

    }

    private void chooseBackgroundImage() {
        isBg = true;
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
                if (!isBg) {
                    database.setProfilePic("0",imagePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    Drawable draw = new BitmapDrawable(getResources(), bitmap);
                    profilePics.setImageTintList(null);
                    profilePics.setImageDrawable(draw);
                } else {
                    database.setBacgroundImage(imagePath);
                    String string[] = imagePath.split("/");
                    wallpaperOptionDescription.setText(string[string.length - 1]);
                }
                cursor.close();
            }
        } else if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getContext(), photo);
                // CALL THIS METHOD TO GET THE ACTUAL PATH
                String imagePath = getRealPathFromURI(tempUri);
                database.setProfilePic("0",imagePath);
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
        Cursor c = database.getProfilePic();
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
