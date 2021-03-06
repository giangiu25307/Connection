package com.example.connection.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.R;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button skipButton;
    private LinearLayout loginButton, signupButton;
    private ConnectionController connectionController;
    private Database database;
    private ChatController chatController;
    private EditText email, password;
    private Connection connection;

    public LoginFragment() {

    }

    public LoginFragment newInstance(Connection connection, Database database) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setConnection(connection);
        loginFragment.setDatabase(database);
        return loginFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.lyt_login, null);

        signupButton = view.findViewById(R.id.signupButton);
        signupButton.setOnClickListener(this);
        skipButton = view.findViewById(R.id.skipButton);
        skipButton.setOnClickListener(this);
        loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        email = view.findViewById(R.id.editTextEmail);
        password = view.findViewById(R.id.editTextNewPassword);

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        switch (v.getId()) {
            case R.id.signupButton:
                fragment = new SignupFragment().newInstance(connection, database);
                loadFragment(fragment);
                break;
            case R.id.skipButton:
                //connectionController.startServiceDiscovery();
                fragment = new HomeFragment().newInstance(connection, database);
                loadFragment(fragment);
                break;
            case R.id.loginButton:
                if (checker()) {
                    fragment = new HomeFragment().newInstance(connection, database);
                    loadFragment(fragment);
                    email.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                    password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                } else {
                    email.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                    password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
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

    public void setDatabase(Database database) {
        this.database = database;
    }

    private boolean checker() {
        String data = database.getMyEmail();
        if (data.equals(email.getText()) && data.equals(password.getText())) return true;
        else return false;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }

}
