package com.example.connection.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.connection.Adapter.SliderAdapter;
import com.example.connection.Model.User;
import com.example.connection.R;

import java.util.regex.Pattern;


public class SignupFragment extends Fragment {

    private static final Pattern regexPassword = Pattern.compile("^" +
            "(?=.*[0-9])" + //at least 1 digit
            "(?=.*[a-z])" + //at least 1 lower case
            "(?=.*[A-Z])" + //at least 1 upper case
            "(?=.*[!?&%$#])" + //at least 1 special character of !?&%$#
            "(?=\\S+$)" + //no white spaces
            ".{8,}" + //at least length of 8 character
            "$");

    private int currentPage;
    private MyViewPager viewPager;
    private User user=new User();

    public SignupFragment newInstance() {
        SignupFragment signupFragment = new SignupFragment();
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
        Button next = view.findViewById(R.id.loginButton);

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
                if(checker())viewPager.setCurrentItem(currentPage + 1);
                else viewPager.setCurrentItem(currentPage);
            }
        });
        return view;
    }

    private boolean checker(){
        switch (currentPage) {
            default:
                return false;
            case 0:
                EditText email = viewPager.findViewById(R.id.email);
                EditText password = viewPager.findViewById(R.id.password);
                String mail = email.getText().toString().trim(), pass = password.getText().toString().trim();
                System.out.println(pass);
                if (Patterns.EMAIL_ADDRESS.matcher(mail).matches() && regexPassword.matcher(pass).matches()) {
                    user.setMail(mail);
                    user.setPassword(pass);
                    email.setBackgroundColor(0);
                    password.setBackgroundColor(0);
                    viewPager.setPagingEnabled(true);
                    return true;
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                        System.out.println("Inserire una Email valida");
                        email.setBackgroundColor(getResources().getColor(R.color.red));
                    }else{
                        email.setBackgroundColor(0);
                    }
                    if (!regexPassword.matcher(pass).matches()) {
                        System.out.println("Inserire una password valida,almeno un carattere numerico, lameno un carattere speciale fra !?&%$#, almeno una lettera maiuscola, almeno una lettera minuscola, almeno 8 caratteri");
                        password.setBackgroundColor(getResources().getColor(R.color.red));
                    }else{
                        password.setBackgroundColor(0);
                    }
                    viewPager.setPagingEnabled(false);
                    return false;
                }
            case 1:
                //stesso procedimento
                break;
        }
        return false;
    }
}
