package com.example.connection.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.R;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button loginButton, signupButton, skipButton;
    private ConnectionController connectionController;
    private Database database;
    private ChatController chatController;
    private EditText email,password;
    private Fragment map, chat, settings;

    public LoginFragment() {

    }

    public LoginFragment newInstance(ConnectionController connectionController, Database database, ChatController chatController, Fragment map, Fragment chat, Fragment settings) {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setConnectionController(connectionController);
        loginFragment.setDatabase(database);
        loginFragment.setChatController(chatController);
        loginFragment.setChat(chat);
        loginFragment.setMap(map);
        loginFragment.setSettings(settings);
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
        loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        email=view.findViewById(R.id.editTextEmail);
        password=view.findViewById(R.id.editTextNewPassword);

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        switch (v.getId()) {
            case R.id.signupButton:
                fragment = new SignupFragment().newInstance(connectionController, database, chatController, map, chat, settings);
                loadFragment(fragment);
                break;
            case R.id.skipButton:
                fragment = new HomeFragment().newInstance(connectionController, database, chatController, map, chat, settings);
                loadFragment(fragment);
                break;
            case R.id.loginButton:
                if(checker()){
                    fragment = new HomeFragment().newInstance(connectionController, database, chatController, map, chat, settings);
                    loadFragment(fragment);
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

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    private boolean checker() {
        String [] data = database.getMyEmailPassword();
        if(data[0].equals(email.getText())&&data[1].equals(password.getText()))return true;
        else return false;
    }

    public void setMap(Fragment map) {
        this.map = map;
    }

    public void setChat(Fragment chat) {
        this.chat = chat;
    }

    public void setSettings(Fragment settings) {
        this.settings = settings;
    }

}
