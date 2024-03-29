package com.ConnectionProject.connection.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ConnectionProject.connection.Controller.ConnectionController;
import com.ConnectionProject.connection.Controller.ImageController;
import com.ConnectionProject.connection.Database.Database;
import com.ConnectionProject.connection.Model.User;
import com.ConnectionProject.connection.R;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Database database;
    private Button gallery, takePhoto, gender;
    private EditText editTextUsername, editTextMail, editTextName, editTextSurname, editTextPhoneNumber, editTextCity, editTextCountry;
    private Button cancelButton, applyButton;
    private CircleImageView profilePic;
    private String previousProfilePic = "";
    private int PICK_IMAGE = 1, CAPTURE_IMAGE = 1337;
    private SharedPreferences sharedPreferences;
    private static final Pattern regexName = Pattern.compile("^" +
            "(?=.*[a-z])" + //at least 1 lower case
            ".{2,26}" +
            "$");

    private static final Pattern regexPhoneNumber = Pattern.compile("^" +
            "[0-9]*" +
            ".{9,11}" +
            "$");
    private String oldFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oldFragment = Connection.fragmentName;
        Connection.fragmentName = "";
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        loadTheme();
        setContentView(R.layout.lyt_edit_profile);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        database = new Database(this);
        editTextCity = findViewById(R.id.cityEditText);
        editTextUsername = findViewById(R.id.usernameEditText);
        editTextMail = findViewById(R.id.mailEditText);
        editTextName = findViewById(R.id.nameEditText);
        editTextSurname = findViewById(R.id.surnameEditText);
        editTextPhoneNumber = findViewById(R.id.phoneNumberEditText);
        gender = findViewById(R.id.genderButton);
        editTextCountry = findViewById(R.id.countryEditText);

        if(database.getMyInformation()!=null) {
            User user = new User(database.getMyInformation()[0], database.getMyInformation()[1], database.getMyInformation()[2], database.getMyInformation()[3], database.getMyInformation()[4], database.getMyInformation()[5], database.getMyInformation()[6], database.getMyInformation()[7], database.getMyInformation()[8], database.getMyInformation()[9], database.getMyInformation()[10]);


            editTextCity.setText(user.getCity());
            editTextUsername.setText(user.getUsername());
            editTextMail.setText(user.getMail());
            editTextName.setText(user.getName());
            editTextSurname.setText(user.getSurname());
            editTextPhoneNumber.setText(user.getNumber());
            gender.setText(user.getGender());
            editTextCountry.setText(user.getCountry());
        }

        takePhoto = findViewById(R.id.textView13);
        gallery = findViewById(R.id.textView14);
        cancelButton = findViewById(R.id.cancelButton);
        applyButton = findViewById(R.id.applyTextView);
        takePhoto.setOnClickListener(this);
        gallery.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        applyButton.setOnClickListener(this);

        profilePic = findViewById(R.id.profilePic);

        if (!previousProfilePic.equals("")) {
            Bitmap bitmap = BitmapFactory.decodeFile(previousProfilePic);
            Drawable draw = new BitmapDrawable(getResources(), bitmap);
            profilePic.setImageTintList(null);
            profilePic.setImageDrawable(draw);
        }
        setProfilePic();

    }

    private void loadTheme() {
        String theme = sharedPreferences.getString("appTheme", "light");
        if (theme.equals("light")) {
            setTheme(R.style.AppTheme);
            setStatusAndNavbarColor(true);
        } else if (theme.equals("dark")) {
            setTheme(R.style.DarkTheme);
            setStatusAndNavbarColor(false);
            System.out.println("Dark");
        } else {
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_NO:
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    setTheme(R.style.AppTheme);
                    setStatusAndNavbarColor(true);
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    setTheme(R.style.DarkTheme);
                    setStatusAndNavbarColor(false);
                    break;
                default:
                    break;
            }
        }

    }

    private void setStatusAndNavbarColor(boolean light) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (light) {
            window.setStatusBarColor(getColor(R.color.colorPrimary));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.setStatusBarColor(getColor(R.color.darkColorPrimary));
        }
        window.setNavigationBarColor(Color.BLACK);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.genderButton:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.dialog_gender);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                Button male = alertDialog.findViewById(R.id.male), female = alertDialog.findViewById(R.id.female), other = alertDialog.findViewById(R.id.other);
                male.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gender.setText("Male");
                        alertDialog.dismiss();
                    }
                });
                female.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gender.setText("Female");
                        alertDialog.dismiss();
                    }
                });
                other.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gender.setText("Other");
                        alertDialog.dismiss();
                    }
                });
                break;
            case R.id.textView14:
                chooseImage();
                break;
            case R.id.textView13:
                captureImage();
                break;
            case R.id.cancelButton:
                database.setProfilePic("0", previousProfilePic);
                finish();
                break;
            case R.id.applyTextView:
                applyChanges();
                break;
        }
    }

    public void applyChanges() {
        setProfilePic();
        boolean bool = true;
        if (!regexName.matcher(editTextCity.getText().toString()).matches()) {
            editTextCity.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data_wrong));
            bool = false;
        } else {
            editTextCity.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data));
        }
        if (!regexName.matcher(editTextUsername.getText().toString()).matches()) {
            editTextUsername.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data_wrong));
            bool = false;
        } else {
            editTextUsername.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data));
        }
        if (!regexName.matcher(editTextName.getText().toString()).matches()) {
            editTextName.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data_wrong));
            bool = false;
        } else {
            editTextName.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data));
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(editTextMail.getText().toString()).matches()) {
            editTextMail.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data_wrong));
            bool = false;
        } else {
            editTextMail.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data));
        }
        if (!regexName.matcher(editTextSurname.getText().toString()).matches()) {
            editTextSurname.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data_wrong));
            bool = false;
        } else {
            editTextSurname.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data));
        }
        if (!regexPhoneNumber.matcher(editTextPhoneNumber.getText().toString()).matches()) {
            editTextPhoneNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data_wrong));
            bool = false;
        } else {
            editTextPhoneNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data));
        }
        if (!regexName.matcher(editTextCountry.getText().toString()).matches()) {
            editTextCountry.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data_wrong));
            bool = false;
        } else {
            editTextCountry.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_input_data));
        }
        if (bool) {
            database.setCity("0", editTextCity.getText().toString());
            database.setUsername("0", editTextUsername.getText().toString());
            database.setName("0", editTextName.getText().toString());
            database.setMail("0", editTextMail.getText().toString());
            database.setSurname("0", editTextSurname.getText().toString());
            database.setNumber("0", editTextPhoneNumber.getText().toString());
            database.setGender("0", gender.getText().toString());
            database.setCountry("0", editTextCountry.getText().toString());
            finish();
        }
    }

    private void captureImage() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    private void chooseImage() {
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
                Cursor cursor = this.getContentResolver().query(pickedImage, filePath, null, null, null);
                if (cursor == null) return;
                cursor.moveToFirst();
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                try {
                    ImageController.copy(new File(imagePath),new File(getApplicationContext().getFilesDir().getAbsolutePath()+"/DIRECT-CONNECTION"+ConnectionController.myUser.getIdUser()+ "." + imagePath.split("\\.")[imagePath.split("\\.").length - 1]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imagePath = getApplicationContext().getFilesDir().getAbsolutePath()+"/DIRECT-CONNECTION"+ConnectionController.myUser.getIdUser()
                        + "." + imagePath.split("\\.")[imagePath.split("\\.").length - 1];
                database.setProfilePic(ConnectionController.myUser.getIdUser(), imagePath);
                profilePic.setImageTintList(null);
                profilePic.setImageDrawable(Drawable.createFromPath(imagePath));
                cursor.close();
            }
        } else if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(this, photo);
                // CALL THIS METHOD TO GET THE ACTUAL PATH
                String imagePath = getRealPathFromURI(tempUri);try {
                    ImageController.copy(new File(imagePath),new File(getApplicationContext().getFilesDir().getAbsolutePath()+"/DIRECT-CONNECTION"+ConnectionController.myUser.getIdUser()+ "." + imagePath.split("\\.")[imagePath.split("\\.").length - 1]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imagePath = getApplicationContext().getFilesDir().getAbsolutePath()+"/DIRECT-CONNECTION"+ConnectionController.myUser.getIdUser()
                        + "." +  imagePath.split("\\.")[imagePath.split("\\.").length - 1];
                database.setProfilePic(ConnectionController.myUser.getIdUser(), imagePath);
                profilePic.setImageTintList(null);
                profilePic.setImageDrawable(Drawable.createFromPath(imagePath));
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void setProfilePic() {
        Cursor c = database.getProfilePic(ConnectionController.myUser.getIdUser());
        ConnectionController.myUser.setProfilePic(c.getString(0));
        if (c == null || c.getCount() == 0) {
            profilePic.setImageTintList(ColorStateList.valueOf(android.R.attr.iconTint));
            return;
        }
        c.moveToFirst();
        previousProfilePic = c.getString(0);
        profilePic.setImageTintList(null);
        profilePic.setImageDrawable(Drawable.createFromPath(c.getString(0)));
        c.close();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (this.getContentResolver() != null) {
            Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Connection.fragmentName = oldFragment;
    }
}
