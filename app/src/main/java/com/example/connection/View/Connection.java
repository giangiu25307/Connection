package com.example.connection.View;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pGroup;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.connection.Bluetooth.BluetoothAdvertiser;
import com.example.connection.Bluetooth.BluetoothScanner;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Model.User;
import com.example.connection.R;

public class Connection extends AppCompatActivity {
    private Fragment fragment;
    private CountDownTimer countDownTimer;
    private Boolean startTimer = false;
    private long secondsRemaining = 1500;
    private SharedPreferences sharedPreferences;
    Database database;
    User user;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;
    ConnectionController connectionController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        database = new Database(this);
        connectionController=new ConnectionController(this,database,user);
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user=new User("aaaaa","ciao","ciaoc","ciao","ciao","ciao","cioa","ciao","ciao","ciao","ciao");
        fragment = new SplashScreenFragment();
        loadFragment(false);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }else {


        }
        createCountDowntimer();
        countDownTimer.start();
        //BluetoothScanner bluetoothScanner=new BluetoothScanner();
        //database.addUser("prova","192.168.49.20","giangiugay","ciao","ciao","ciao","cioa","ciao","ciao","ciao","ciao");
        //database.addUser("1","192.168.49.20","Andrew00","andrew@gmail.com","male","Andrew","Wand","England","London","23","/photo");
       // database.createChat("1", "Andrew");
       // database.addMsg("Ciao", "1", "prova","1");
        //connectionController.Discovery();
        //bluetoothScanner.startBLEScan();
        connectionController.Discovery();
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
                fragment = new HomeFragment().newInstance(connectionController, database);
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
        registerReceiver(connectionController.getmReceiver(), connectionController.getmIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectionController.getmReceiver());
    }

    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
        startTimer = true;
    }
}