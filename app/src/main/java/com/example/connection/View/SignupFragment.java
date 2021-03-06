package com.example.connection.View;

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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.connection.Adapter.SliderAdapter;
import com.example.connection.Controller.Database;
import com.example.connection.Model.User;
import com.example.connection.R;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;


public class SignupFragment extends Fragment implements View.OnClickListener {

    private Database database;
    private static final int PICK_IMAGE = 1, CAPTURE_IMAGE = 1337;
    private ImageView profilePic;
    private RelativeLayout next, back;
    private ArrayList<String> nations = new ArrayList<String>();
    private int yearText = 0, monthText = 0, dayText = 0;
    private String countryCode="";

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

    public SignupFragment newInstance(Connection connection, Database database) {
        SignupFragment signupFragment = new SignupFragment();
        signupFragment.setConnection(connection);
        signupFragment.setDatabase(database);
        signupFragment.setNations();
        signupFragment.setUser();
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
        LinearLayout login = view.findViewById(R.id.linearLayoutLoginBackButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new LoginFragment().newInstance(connection,database);
                loadFragment(fragment);
            }
        });


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

        next.setOnClickListener(this);
        back.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextButton:
                if (currentPage < 3) {
                    if (checker()) {
                        viewPager.setCurrentItem(currentPage + 1);
                    } else viewPager.setCurrentItem(currentPage);
                } else {
                    //send user info to server
                    database.addUser("0", null, user.getUsername(), user.getMail(), user.getGender(), user.getName(), user.getSurname(), user.getCountry(), user.getCity(), user.getBirth(), user.getProfilePic(), user.getPublicKey());
                    database.setNumber("0", user.getNumber());
                    Fragment fragment = new HomeFragment().newInstance(connection,database);
                    loadFragment(fragment);
                }
                break;
            case R.id.backButton:
                if (currentPage > 0) viewPager.setCurrentItem(currentPage - 1);
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
                if (!user.getUsername().equals("")) usernameLabel.setText(user.getUsername());
                if (!user.getMail().equals("")) email.setText(user.getMail());
                if (!user.getPassword().equals("")) password.setText(user.getPassword());
                String mail = email.getText().toString().trim(), pass = password.getText().toString().trim(), username = usernameLabel.getText().toString().trim();
                if (!regexName.matcher(username).matches()) {
                    usernameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                } else {
                    user.setUsername(username);
                    usernameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    email.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                } else {
                    user.setMail(mail);
                    email.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                }
                if (!regexPassword.matcher(pass).matches()) {
                    password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                } else {
                    user.setPassword(pass);
                    password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                }
                return !user.getMail().equals("") && !user.getPassword().equals("") && !user.getUsername().equals("");
            case 1:
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
                EditText firstNameLabel = viewPager.findViewById(R.id.firstname), surnameLabel = viewPager.findViewById(R.id.surname);
                TextView gender = viewPager.findViewById(R.id.genderSignUpTextView);
                gender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                        dialogBuilder.setView(R.layout.dialog_gender);
                        final AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();
                        TextView male = alertDialog.findViewById(R.id.male), female = alertDialog.findViewById(R.id.female), other = alertDialog.findViewById(R.id.other);
                        final TextView gender = viewPager.findViewById(R.id.genderSignUpTextView);
                        male.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gender.setText("Maschio");
                                user.setGender("Maschio");
                                alertDialog.dismiss();
                            }
                        });
                        female.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gender.setText("Femmina");
                                user.setGender("Femmina");
                                alertDialog.dismiss();
                            }
                        });
                        other.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gender.setText("Altro");
                                user.setGender("Altro");
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                final AppCompatTextView dateOfBirth = viewPager.findViewById(R.id.dateOfBirthSignUpTextView);
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
                    gender.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                } else {
                    gender.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                }
                if (!regexName.matcher(firstname).matches()) {
                    firstNameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                } else {
                    user.setName(firstname);
                    firstNameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                }
                if (!regexName.matcher(surname).matches()) {
                    surnameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    viewPager.setPagingEnabled(false);
                } else {
                    user.setSurname(surname);
                    surnameLabel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    viewPager.setPagingEnabled(true);
                }
                return !user.getName().equals("") && !user.getSurname().equals("") && !user.getBirth().equals("");
            case 2:
                final TextView telephone = viewPager.findViewById(R.id.telephone), cities = viewPager.findViewById(R.id.city), numberCode = viewPager.findViewById(R.id.numberCode);
                Spinner country = viewPager.findViewById(R.id.country);

                String number = telephone.getText().toString().trim();
                String city = cities.getText().toString().trim();
                if (!regexPhoneNumber.matcher(number).matches()) {
                    System.out.println("Inserire un numero di telefono valido");
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
                        viewPager.setPagingEnabled(false);
                    }
                });
                if (!regexName.matcher(city).matches()) {
                    System.out.println("Inserire una città valida");
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
                AppCompatTextView gallery = viewPager.findViewById(R.id.gallery);
                AppCompatTextView photo = viewPager.findViewById(R.id.takePhoto);

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseProfilePic();
                        //if (!user.getProfilePic().equals("")) next.setText("Confirm");
                        //else next.setText("Next");
                    }
                });
                photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        captureImage();
                        //if (!user.getProfilePic().equals("")) next.setText("Confirm");
                        //else next.setText("Next");
                    }
                });
                return !user.getProfilePic().equals("");
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
                String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
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
            this.countryCode=countryCode;
            nations.add(obj.getDisplayCountry());
        }
    }

    private void setNationCode() {
        String[] locales = Locale.getISOCountries();
        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            if(user.getCountry().equals(obj.getDisplayCountry()))this.countryCode=countryCode;
        }
    }

    public void setConnection(Connection connection){
        this.connection = connection;
    }

    public void setUser(){
        user = new User();
    }

}
