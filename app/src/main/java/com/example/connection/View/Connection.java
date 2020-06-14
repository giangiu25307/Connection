package com.example.connection.View;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Controller.AutoClicker;
import com.example.connection.Model.User;
import com.example.connection.R;
//D/SupplicantP2pIfaceCallbackExt: Provision discovery request for WPS Config method: 128
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
    public static int touchX;
    public static int touchY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        database = new Database(this);
        touchX=displayMetrics.widthPixels/100*74;
        touchY=displayMetrics.heightPixels/100*92;
        touchX=750;
        touchY=1930;
        AutoClicker autoClicker=new AutoClicker();
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
        //database.addUser("prova","192.168.49.20","giangiu","ciao","ciao","ciao","cioa","ciao","ciao","ciao","ciao");
        database.addUser("1","192.168.49.20","Andrew00","andrew@gmail.com","male","Andrew","Wand","England","London","23","/photo");
        database.createChat("1", "Andrew");
        database.addMsg("Ciao", "1", "prova","1");
        //connectionController.createGroup();
        //bluetoothScanner.startBLEScan();

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
    public boolean isAccessibilityEnabled() {
        int accessibilityEnabled = 0;
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled==1) {
            System.out.println("***ACCESSIBILIY IS ENABLED***: ");

            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase("ACCESSIBILITY_SERVICE_NAME")){
                        return true;
                    }
                }
            }

        }
        else {
            System.out.println( "***ACCESSIBILIY IS DISABLED***");


        }
        return accessibilityFound;
    }
}