package com.example.connection.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.connection.Adapter.SliderAdapter;
import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Model.User;
import com.example.connection.R;

import java.util.regex.Pattern;


public class SignupFragment extends Fragment {

    private ConnectionController connectionController;
    private Database database;
    private ChatController chatController;
    private static final int PICK_IMAGE = 1;
    private ImageView profilePic;

    private static final Pattern regexPassword = Pattern.compile("^" +
            "(?=.*[0-9])" + //at least 1 digit
            "(?=.*[a-z])" + //at least 1 lower case
            "(?=.*[A-Z])" + //at least 1 upper case
            "(?=.*[!?&%$#])" + //at least 1 special character of !?&%$#
            "(?=\\S+$)" + //no white spaces
            ".{8,}" + //at least length of 8 character
            "$");

    private static final Pattern regexName = Pattern.compile("^" +
            ".{2,26}" + //at least length of 8 character
            "$");

    private int currentPage;
    private MyViewPager viewPager;
    private User user = new User();

    public SignupFragment newInstance(ConnectionController connectionController, Database database, ChatController chatController) {
        SignupFragment signupFragment = new SignupFragment();
        signupFragment.setConnectionController(connectionController);
        signupFragment.setDatabase(database);
        signupFragment.setChatController(chatController);
        return signupFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.signup_fragment, null);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        viewPager = view.findViewById(R.id.viewPager);
        SliderAdapter sliderAdapter = new SliderAdapter(this.getContext());
        viewPager.setAdapter(sliderAdapter);
        Button next = view.findViewById(R.id.nextButton);

        ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                checker();
            }
        };
        viewPager.setOnPageChangeListener(viewListener);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker()) viewPager.setCurrentItem(currentPage + 1);
                else viewPager.setCurrentItem(currentPage);
            }
        });
        return view;
    }

    private boolean checker() {
        switch (currentPage) {
            default:
                return false;
            case 0:
                boolean emailCheck = true, passwordCheck = true, usernameB = true;
                EditText usernameLabel = viewPager.findViewById(R.id.username);
                EditText email = viewPager.findViewById(R.id.email);
                EditText password = viewPager.findViewById(R.id.password);
                if (!user.getUsername().equals("")) usernameLabel.setText(user.getUsername());
                if (!user.getMail().equals("")) email.setText(user.getMail());
                if (!user.getPassword().equals("")) password.setText(user.getPassword());
                String mail = email.getText().toString().trim(), pass = password.getText().toString().trim(), username = usernameLabel.getText().toString().trim();
                if (!regexName.matcher(username).matches()) {
                    System.out.println("Inserire un username valido");
                    //email.setBackground(getResources().getDrawable(R.drawable.input_data_background_wrong));
                    usernameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background_wrong));
                    //email.setBackgroundColor(getResources().getColor(R.color.red));
                    viewPager.setPagingEnabled(false);
                    usernameB = false;
                } else {
                    user.setUsername(username);
                    usernameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background));
                    usernameB = true;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    System.out.println("Inserire una Email valida");
                    //email.setBackground(getResources().getDrawable(R.drawable.input_data_background_wrong));
                    email.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background_wrong));
                    //email.setBackgroundColor(getResources().getColor(R.color.red));
                    viewPager.setPagingEnabled(false);
                    emailCheck = false;
                } else {
                    user.setMail(mail);
                    email.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background));
                    emailCheck = true;
                }
                if (!regexPassword.matcher(pass).matches()) {
                    System.out.println("Inserire una password valida, almeno un carattere numerico, almeno un carattere speciale fra !?&%$#, almeno una lettera maiuscola, almeno una lettera minuscola, almeno 8 caratteri");
                    //password.setBackground(getResources().getDrawable(R.drawable.input_data_background_wrong));
                    password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background_wrong));
                    //password.setBackgroundColor(getResources().getColor(R.color.red));
                    viewPager.setPagingEnabled(false);
                    passwordCheck = false;
                } else {
                    user.setPassword(pass);
                    password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background));
                    passwordCheck = true;
                }
                if (emailCheck && passwordCheck && usernameB) {
                    viewPager.setPagingEnabled(true);
                    return true;
                } else return false;
            case 1:
                EditText firstNameLabel = viewPager.findViewById(R.id.firstname);
                EditText surnameLabel = viewPager.findViewById(R.id.surname);
                if (!user.getName().equals("")) firstNameLabel.setText(user.getName());
                if (!user.getSurname().equals("")) surnameLabel.setText(user.getSurname());
                String firstname = firstNameLabel.getText().toString().trim(), surname = surnameLabel.getText().toString().trim();
                boolean name = true, sur = true;
                if (!regexName.matcher(firstname).matches()) {
                    System.out.println("Inserire una nome valido");
                    //email.setBackground(getResources().getDrawable(R.drawable.input_data_background_wrong));
                    firstNameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background_wrong));
                    //email.setBackgroundColor(getResources().getColor(R.color.red));
                    viewPager.setPagingEnabled(false);
                    name = false;
                } else {
                    user.setName(firstname);
                    firstNameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background));
                    name = true;
                }
                if (!regexName.matcher(surname).matches()) {
                    System.out.println("Inserire un cognome valido");
                    //password.setBackground(getResources().getDrawable(R.drawable.input_data_background_wrong));
                    surnameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background_wrong));
                    //password.setBackgroundColor(getResources().getColor(R.color.red));
                    viewPager.setPagingEnabled(false);
                    sur = false;
                } else {
                    user.setSurname(surname);
                    surnameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.input_data_background));
                    sur = true;
                }
                if (name && sur) {
                    viewPager.setPagingEnabled(true);
                    return true;
                } else return false;
            case 2:
                profilePic = viewPager.findViewById(R.id.profilePic);
                EditText gallery = viewPager.findViewById(R.id.gallery);
                EditText photo = viewPager.findViewById(R.id.takePhoto);
                final boolean[] galleryB = {false},photoB = {false};
                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseProfilePic();
                        galleryB[0] = true;
                    }
                });
                photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //manca come aprire la fotocamera
                        photoB[0]=false;
                    }
                });
                return galleryB[0]||photoB[0];
        }
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

    private void chooseProfilePic() {
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
                database.setProfilePic(imagePath);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Drawable draw = new BitmapDrawable(getResources(), bitmap);
                profilePic.setImageTintList(null);
                profilePic.setImageDrawable(draw);
                cursor.close();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

}
