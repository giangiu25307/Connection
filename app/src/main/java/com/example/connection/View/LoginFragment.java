package com.example.connection.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.connection.Controller.AccountController;
import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.DrawController;
import com.example.connection.Database.Database;
import com.example.connection.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.Response;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button loginButton, signupButton;
    private ConnectionController connectionController;
    private Database database;
    private ChatController chatController;
    private EditText email, password;
    private Connection connection;
    private AccountController accountController;
    private DrawController drawController;
    private ImageButton showHidePassword;
    private boolean isPasswordShown;

    private static LoginFragment loginFragment;

    public LoginFragment() {

    }

    public LoginFragment newInstance(Connection connection, Database database, AccountController accountController, DrawController drawController) {
        loginFragment = new LoginFragment();
        loginFragment.setConnection(connection);
        loginFragment.setDatabase(database);
        loginFragment.setAccountController(accountController);
        loginFragment.setDrawController(drawController);
        return loginFragment;
    }

    public static LoginFragment getInstance() {
        return loginFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.lyt_login, null);

        loginFragment.signupButton = view.findViewById(R.id.signupButton);
        loginFragment.signupButton.setOnClickListener(this);
        loginFragment.loginButton = view.findViewById(R.id.loginButton);
        loginFragment.loginButton.setOnClickListener(this);
        loginFragment.email = view.findViewById(R.id.editTextEmail);
        loginFragment.password = view.findViewById(R.id.editTextPassword);
        loginFragment.showHidePassword = view.findViewById(R.id.showHidePassword);
        loginFragment.showHidePassword.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        switch (v.getId()) {
            case R.id.signupButton:
                fragment = new SignupFragment().newInstance(loginFragment.connection, loginFragment.database, loginFragment.accountController, drawController);
                loadFragment(fragment);
                break;
            case R.id.loginButton:
                try {
                    Response response = accountController.login(loginFragment.email.getText().toString(), loginFragment.password.getText().toString());
                    if (response != null && response.isSuccessful()) {
                        String test = response.body().string();
                        System.out.println("[RESPONSE BODY]" + test);
                        test = test.replace("\"user\":{","");
                        test = test.replace("}}","}");
                        System.out.println("[MODIFIED BODY]" + test);
                        Gson g = new Gson();
                        HashMap<String,String> map = g.fromJson(test,HashMap.class);
                        System.out.println(map.get("username"));
                        if(map.get("message").equals("Logged in!")) {
                            loginFragment.database.addUser(map.get("id"), "", map.get("username"), map.get("email"), map.get("gender"), map.get("name"), map.get("surname"), map.get("country"), ""/*map.get("city")*/, map.get("birthday"), "", "");
                            fragment = new HomeFragment().newInstance(loginFragment.connection, loginFragment.database, loginFragment.drawController);
                            loadFragment(fragment);
                        }else{
                            loginFragment.email.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                            loginFragment.password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data));
                            Toast.makeText(connection.getApplicationContext(), "Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        loginFragment.email.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                        loginFragment.password.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_input_data_wrong));
                        Snackbar snackbar = Snackbar.make(loginFragment.getActivity().getWindow().getDecorView().findViewById(R.id.lyt_chat_activity), "", Snackbar.LENGTH_LONG);
                        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
                        layout.setBackgroundColor(loginFragment.getActivity().getColor(R.color.transparent));
                        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
                        textView.setVisibility(View.INVISIBLE);

                        View snackView = loginFragment.getActivity().getLayoutInflater().inflate(R.layout.lyt_chats_messages_deleted_snackbar, null);
                        TextView textView1 = snackView.findViewById(R.id.textView);
                        textView1.setText("Mail and password don't match, please try again");
                        layout.setPadding(5, 5, 5, 5);
                        layout.addView(snackView, 0);
                        snackbar.show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.showHidePassword:
                if (!isPasswordShown) {
                    loginFragment.password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    loginFragment.showHidePassword.setImageResource(R.drawable.ic_hide_password);
                    loginFragment.isPasswordShown = true;
                } else {
                    loginFragment.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    loginFragment.showHidePassword.setImageResource(R.drawable.ic_show_password);
                    loginFragment.isPasswordShown = false;
                }
                loginFragment.password.setSelection(loginFragment.password.getText().length());
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

    private void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setAccountController(AccountController accountController) {
        this.accountController = accountController;
    }

    public void setDrawController(DrawController drawController) {
        this.drawController = drawController;
    }
}
