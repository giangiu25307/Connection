package com.example.connection.View;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.R;
import com.example.connection.TCP_Connection.TCP_Client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Connection extends AppCompatActivity {
    private Fragment fragment;
    private CountDownTimer countDownTimer;
    private Boolean startTimer = false;
    private long secondsRemaining = 1500;
    private SharedPreferences sharedPreferences;

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;
    ConnectionController connectionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment = new SplashScreenFragment();
        loadFragment(false);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }else {
            connectionController = new ConnectionController(this);
        }
        createCountDowntimer();
        countDownTimer.start();

    }

    private void loadTheme(){
        String theme = sharedPreferences.getString("appTheme", "light");
        if(theme.equals("light")){
            setTheme(R.style.AppTheme);
            setStatusAndNavbarColor(true);
        }else if(theme.equals("dark")){
            setTheme(R.style.DarkTheme);
            setStatusAndNavbarColor(false);
        }else{
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

    private void setStatusAndNavbarColor(boolean light){
        Window window = getWindow();
        if(light){
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
            window.setNavigationBarColor(Color.WHITE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }else{
            int color = getColor(R.color.regularBlack);
            window.setNavigationBarColor(color);
            window.setStatusBarColor(color);
        }
    }

    private void loadFragment(boolean transition){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(transition){
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        transaction.replace(R.id.main_fragment, fragment);
        transaction.commit();
    }

    private void createCountDowntimer(){
        countDownTimer = new CountDownTimer(secondsRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                fragment = new HomeFragment();
                loadFragment(true);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(startTimer){
            createCountDowntimer();
            countDownTimer.start();
        }
        //registerReceiver(connectionController.getmReceiver(), connectionController.getmIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(connectionController.getmReceiver());
    }

    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
        startTimer = true;
    }
}