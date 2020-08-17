package com.example.connection.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.R;

public class SignupFragment extends Fragment implements View.OnClickListener {

    Button loginButton, signupButton, skipButton;
    ConnectionController connectionController;
    Database database;
    ChatController chatController;

    public SignupFragment() {

    }

    public LoginFragment newInstance(ConnectionController connectionController, Database database, ChatController chatController) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setConnectionController(connectionController);
        loginFragment.setDatabase(database);
        loginFragment.setChatController(chatController);
        return loginFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.login_fragment, null);

        signupButton = view.findViewById(R.id.signupButton);
        signupButton.setOnClickListener(this);
        skipButton = view.findViewById(R.id.skipButton);
        skipButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        switch (v.getId()) {
            case R.id.signupButton:
                //fragment = new LoginFragment().newInstance();
                //loadFragment(fragment);
                break;
            case R.id.skipButton:
                fragment = new HomeFragment().newInstance(connectionController, database, chatController);
                loadFragment(fragment);
                break;
            default:
                break;
        }
    }

    public void loadFragment(Fragment newFragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment, newFragment);
        transaction.commit();
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

}
