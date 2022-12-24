package com.ConnectionProject.connection.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.ConnectionProject.connection.Adapter.SliderAdapter;
import com.ConnectionProject.connection.Controller.AccountController;
import com.ConnectionProject.connection.Controller.DrawController;
import com.ConnectionProject.connection.Controller.ImageController;
import com.ConnectionProject.connection.Database.Database;
import com.ConnectionProject.connection.Model.User;
import com.ConnectionProject.connection.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

import okhttp3.Response;


public class SignupFragment extends Fragment implements View.OnClickListener {

    private Database database;
    private static final int PICK_IMAGE = 1, CAPTURE_IMAGE = 1337;
    private ImageView profilePic;
    private ImageButton next, back;
    private TextView signupInformationTextView;
    private String[] signupInformation = {"Account information", "Personal information", "Personal information", "Profile pic"};
    private ArrayList<String> nations = new ArrayList<String>();
    private int yearText = 0, monthText = 0, dayText = 0;
    private String countryCode = "";
    private DrawController drawController;
    private boolean isPasswordShown;
    private View[] views;
    private Animation slideUp, slideDown;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialogLoading;
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

    private int currentPage;
    private MyViewPager viewPager;
    private User user;
    private Connection connection;
    private AccountController accountController;

    public SignupFragment newInstance(Connection connection, Database database, AccountController accountController, DrawController drawController) {
        SignupFragment signupFragment = new SignupFragment();
        signupFragment.setConnection(connection);
        signupFragment.setDatabase(database);
        signupFragment.setNations();
        signupFragment.setUser();
        signupFragment.setAccountController(accountController);
        signupFragment.setDrawController(drawController);
        return signupFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.lyt_signup, null);
        viewPager = view.findViewById(R.id.viewPager);
        SliderAdapter sliderAdapter = new SliderAdapter(this.getContext());
        viewPager.setAdapter(sliderAdapter);
        next = view.findViewById(R.id.nextButton);
        back = view.findViewById(R.id.backButton);
        back.setAlpha(0.25f);
        Button login = view.findViewById(R.id.loginBackButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new LoginFragment().newInstance(connection, database, accountController, drawController);
                loadFragment(fragment);
            }
        });

        signupInformationTextView = view.findViewById(R.id.signupInformation);

        slideDown = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.slide_left);

        slideUp = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.slide_right);

        dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        dialogBuilder.setView(R.layout.dialog_signup_loading);
        alertDialogLoading = dialogBuilder.create();

        ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                System.out.println("Pagina: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                checker();
            }


        };
        viewPager.setOnPageChangeListener(viewListener);
        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                EditText password = viewPager.findViewById(R.id.password);
                ImageButton showHidePassword = viewPager.findViewById(R.id.showHidePassword);
                showHidePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isPasswordShown) {
                            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            showHidePassword.setImageResource(R.drawable.ic_hide_password);
                            password.setSelection(password.getText().length());
                            isPasswordShown = true;
                        } else {
                            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            showHidePassword.setImageResource(R.drawable.ic_show_password);
                            password.setSelection(password.getText().length());
                            isPasswordShown = false;
                        }
                    }
                });
            }
        });

        next.setOnClickListener(this);
        back.setOnClickListener(this);

        views = new View[3];
        views[0] = view.findViewById(R.id.view2);
        views[1] = view.findViewById(R.id.view3);
        views[2] = view.findViewById(R.id.view4);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextButton:
                if (currentPage < 3) {
                    if (checker()) {
                        views[currentPage].setBackgroundResource(R.drawable.bg_signup_page_indicator_selected);
                        viewPager.setCurrentItem(currentPage + 1);
                        animTextView(true, currentPage);
                        if (currentPage == 1) {
                            back.setAlpha(1f);
                        }
                        if (currentPage == 3) {
                            next.setImageResource(R.drawable.ic_done);
                        }
                    } else viewPager.setCurrentItem(currentPage);
                } else {
                    //send user info to server
                    /*dialogBuilder.setView(R.layout.dialog_signup_loading);
                    final AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();
                    Window window = alertDialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();

                    wlp.gravity = Gravity.TOP;
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
                    window.setAttributes(wlp);*/
                    alertDialogLoading.show();
                    try {

                        user.setProfilePicBase64(user.getProfilePic());
                        Response response = accountController.register(user.getPassword(), user.getUsername(), user.getMail(), user.getGender(), user.getName(), user.getSurname(), user.getCountry(), user.getCity(), user.getBirth(), user.getNumber(), user.getProfilePicBase64());
                        if (response.isSuccessful()) {
                            alertDialogLoading.dismiss();
                            Gson g = new Gson();
                            HashMap<String, String> map = g.fromJson(response.body().string(), HashMap.class);
                            database.addUser(map.get("id"), null, user.getUsername(), user.getMail(), user.getGender(), user.getName(), user.getSurname(), user.getCountry(), user.getCity(), user.getBirth(), ImageController.decodeImage(user.getProfilePicBase64(), getContext(), user.getIdUser(), user.getProfilePic().split("\\.")[user.getProfilePic().split("\\.").length - 1]), user.getPublicKey());
                            database.setNumber(map.get("id"), user.getNumber());
                            Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_LONG).show();
                            Fragment fragment = new LoginFragment().newInstance(connection, database, accountController, drawController);
                            loadFragment(fragment);
                        } else {
                            alertDialogLoading.dismiss();
                            dialogBuilder.setView(R.layout.dialog_signup_loading);
                            final AlertDialog errorAlertDialog = dialogBuilder.create();
                            errorAlertDialog.show();
                            TextView dialogTitle = errorAlertDialog.findViewById(R.id.dialogTitle);
                            dialogTitle.setText("Signup error");
                            TextView dialogErrorMessage = errorAlertDialog.findViewById(R.id.dialogErrorMessage);
                            dialogErrorMessage.setText("Signup error, please try again");
                            errorAlertDialog.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    errorAlertDialog.dismiss();
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.backButton:
                if (currentPage > 0) {
                    if (currentPage == 3) {
                        next.setImageResource(R.drawable.ic_next_long);
                    }
                    viewPager.setCurrentItem(currentPage - 1);
                    views[currentPage].setBackgroundResource(R.drawable.bg_signup_page_indicator_unselected);
                    animTextView(false, currentPage);
                }
                break;
            default:
                break;
        }
    }

    public void loadFragment(Fragment newFragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, newFragment);
        transaction.commit();
    }

    private boolean checker() {
        switch (currentPage) {
            default:
                return false;
            case 0:
                EditText usernameLabel = viewPager.findViewById(R.id.username);
                EditText email = viewPager.findViewById(R.id.email);
                EditText password = viewPager.findViewById(R.id.password);
                ImageView showHidePassword = viewPager.findViewById(R.id.showHidePassword);
                TextView passwordHintsTitle = viewPager.findViewById(R.id.passwordHintsTitle);
                TextView passwordHints = viewPager.findViewById(R.id.passwordHints);
                back.setAlpha(0.3f);
                showHidePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isPasswordShown) {
                            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            showHidePassword.setImageResource(R.drawable.ic_hide_password);
                            isPasswordShown = true;
                        } else {
                            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            showHidePassword.setImageResource(R.drawable.ic_show_password);
                            isPasswordShown = false;
                        }
                        password.setSelection(password.getText().length());
                    }
                });
                if (!user.getUsername().equals("")) usernameLabel.setText(user.getUsername());
                if (!user.getMail().equals("")) email.setText(user.getMail());
                if (!user.getPassword().equals("")) password.setText(user.getPassword());
                String mail = email.getText().toString().trim(), pass = password.getText().toString().trim(), username = usernameLabel.getText().toString().trim();
                if (!regexName.matcher(username).matches()) {
                    user.setUsername("");
                    usernameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                } else {
                    user.setUsername(username);
                    usernameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    user.setMail("");
                    email.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                } else {
                    user.setMail(mail);
                    email.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                }
                if (!regexPassword.matcher(pass).matches()) {
                    user.setPassword("");
                    password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                    passwordHintsTitle.setVisibility(View.VISIBLE);
                    passwordHints.setVisibility(View.VISIBLE);
                } else {
                    user.setPassword(pass);
                    password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                    passwordHintsTitle.setVisibility(View.INVISIBLE);
                    passwordHints.setVisibility(View.INVISIBLE);
                }
                return !user.getMail().equals("") && !user.getPassword().equals("") && !user.getUsername().equals("");
            case 1:
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
                EditText firstNameLabel = viewPager.findViewById(R.id.firstname), surnameLabel = viewPager.findViewById(R.id.surname);
                TextView gender = viewPager.findViewById(R.id.genderSignUpButton);
                gender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.setView(R.layout.dialog_gender);
                        final AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();
                        final Button gender = viewPager.findViewById(R.id.genderSignUpButton);
                        Button male = alertDialog.findViewById(R.id.male), female = alertDialog.findViewById(R.id.female), other = alertDialog.findViewById(R.id.other);
                        male.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gender.setText("Male");
                                user.setGender("Male");
                                alertDialog.dismiss();
                            }
                        });
                        female.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gender.setText("Female");
                                user.setGender("Female");
                                alertDialog.dismiss();
                            }
                        });
                        other.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gender.setText("Other");
                                user.setGender("Other");
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                final Button dateOfBirth = viewPager.findViewById(R.id.dateOfBirthSignUpButton);
                if (!user.getName().equals("")) firstNameLabel.setText(user.getName());
                if (!user.getSurname().equals("")) surnameLabel.setText(user.getSurname());
                if (!user.getGender().equals("")) gender.setText(user.getGender());
                if (!user.getBirth().equals("")) dateOfBirth.setText(user.getBirth());
                String firstname = firstNameLabel.getText().toString().trim(), surname = surnameLabel.getText().toString().trim();
                dateOfBirth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePickerDialog.show();
                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                yearText = year;
                                monthText = month;
                                dayText = dayOfMonth;
                                String birth = yearText != 0 ? (dayText < 10 ? "0" + dayText : dayText) + "-" + (monthText < 10 ? "0" + monthText : monthText) + "-" + yearText : "";
                                dateOfBirth.setText(birth);
                                if (birth.equals("")) {
                                    user.setBirth("");
                                    dateOfBirth.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                                    viewPager.setPagingEnabled(false);
                                } else {
                                    user.setBirth(birth);
                                    dateOfBirth.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                                    viewPager.setPagingEnabled(true);
                                }
                            }
                        });
                    }
                });
                if (gender.getText().toString().trim().isEmpty()) {
                    user.setGender("");
                    gender.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                } else {
                    gender.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                }
                if (!regexName.matcher(firstname).matches()) {
                    user.setName("");
                    firstNameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                } else {
                    user.setName(firstname);
                    firstNameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                }
                if (!regexName.matcher(surname).matches()) {
                    user.setSurname("");
                    surnameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                } else {
                    user.setSurname(surname);
                    surnameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                }
                return !user.getName().equals("") && !user.getSurname().equals("") && !user.getBirth().equals("") && !user.getGender().equals("");
            case 2:
                final TextView telephone = viewPager.findViewById(R.id.telephone), cities = viewPager.findViewById(R.id.city), numberCode = viewPager.findViewById(R.id.numberCode);
                Spinner country = viewPager.findViewById(R.id.country);
                if (!user.getNumber().equals("")) telephone.setText(user.getNumber());
                String number = telephone.getText().toString().trim();
                String city = cities.getText().toString().trim();
                if (!regexPhoneNumber.matcher(number).matches()) {
                    user.setNumber("");
                    telephone.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                } else {
                    telephone.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                    user.setNumber(number);
                }
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, nations);
                country.setAdapter(adapter);
                country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        user.setCountry(adapter.getItem(position));
                        viewPager.setPagingEnabled(true);
                        setNationCode();
                        String codeNumber = "+" + GetCountryZipCode();
                        numberCode.setText(codeNumber);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        user.setCountry("");
                        viewPager.setPagingEnabled(false);
                    }
                });
                if (!regexName.matcher(city).matches()) {
                    user.setCity("");
                    cities.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                } else {
                    cities.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                    user.setCity(city);
                }
                return !user.getNumber().equals("") && !user.getCity().equals("") && !user.getCountry().equals("");
            case 3:
                profilePic = viewPager.findViewById(R.id.profilePic);
                Button gallery = viewPager.findViewById(R.id.gallery);
                Button photo = viewPager.findViewById(R.id.takePhoto);

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseProfilePic();
                    }
                });
                photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        captureImage();
                    }
                });
                return !user.getProfilePic().equals("");
        }
    }

    private void animTextView(boolean isNext, int currentPage) {
        signupInformationTextView.setText(signupInformation[currentPage]);
        signupInformationTextView.clearAnimation();

        if (isNext) {
            signupInformationTextView.startAnimation(slideDown);
        } else {
            signupInformationTextView.startAnimation(slideUp);
        }
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    private void captureImage() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, CAPTURE_IMAGE);
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
                int index = cursor.getColumnIndex(filePath[0]);
                if (index < 0) return;
                String imagePath = cursor.getString(index);
                ImageController.myImagePathToCopy = imagePath;
                user.setProfilePic(imagePath);//database.setProfilePic(imagePath);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Drawable draw = new BitmapDrawable(getResources(), bitmap);
                profilePic.setImageTintList(null);
                profilePic.setImageDrawable(draw);
                cursor.close();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getContext(), photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                String imagePath = getRealPathFromURI(tempUri);

                ImageController.myImagePathToCopy = imagePath;

                File file = new File(imagePath);
                user.setProfilePic(imagePath);//database.setProfilePic(imagePath);
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

    public String GetCountryZipCode() {
        String CountryZipCode = "";
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(this.countryCode.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    private void setNations() {
        String[] locales = Locale.getISOCountries();
        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            this.countryCode = countryCode;
            nations.add(obj.getDisplayCountry());
        }
    }

    private void setNationCode() {
        String[] locales = Locale.getISOCountries();
        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            if (user.getCountry().equals(obj.getDisplayCountry())) this.countryCode = countryCode;
        }
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setUser() {
        user = new User();
    }

    public void setAccountController(AccountController accountController) {
        this.accountController = accountController;
    }

    public void setDrawController(DrawController drawController) {
        this.drawController = drawController;
    }

    @Override
    public void onPause() {
        super.onPause();
        signupInformationTextView.clearAnimation();
    }
}
