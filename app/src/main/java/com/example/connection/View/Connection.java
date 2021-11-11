package com.example.connection.View;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.VpnService;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.connection.Controller.AccountController;
import com.example.connection.Controller.DrawController;
import com.example.connection.Controller.MessageController;
import com.example.connection.Database.Database;
import com.example.connection.Model.MapUsers;
import com.example.connection.Model.User;
import com.example.connection.Model.UserPlus;
import com.example.connection.R;
import com.example.connection.Services.MyForegroundService;
import com.example.connection.vpn.LocalVPNService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Connection extends AppCompatActivity {
    private Fragment fragment;
    private CountDownTimer countDownTimer;
    private Boolean startTimer = false;
    private Boolean startTimer2 = true;
    private long secondsRemaining = 1000;
    private SharedPreferences sharedPreferences;
    public static boolean boot = true,isGlobalChatOpen=false;
    public static Database database;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;
    public static String fragmentName = "MAP",idChatOpen="";
    public static String lightOrDark = "light";
    //public static ArrayList<User> mapUsers = new ArrayList<User>();
    public static int minAge = 16, maxAge = 100;
    public static String[] genders = {"male", "female", "other"};
    private static final int VPN_REQUEST_CODE = 0x0F;
    private AccountController accountController;
    public static LocalVPNService localVPNService;
    private NotificationChannel channel;
    public static MyForegroundService foregroundService;
    public static int page=0;
    private DrawController drawController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        deleteDatabase("Connection");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        database = new Database(this);
        accountController = new AccountController();
        drawController = new DrawController(database);
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment = new SplashScreenFragment();
        loadFragment(false);
        requestPermissions();
        createCountDowntimer();
        countDownTimer.start();

        //CHECKARE CI SIA QUALCUNO ALL'INTERNO DEL GRUPPO PRIMA DI MANDARE MESSAGGI INUTILI
        boolean createSample = false;
        if (createSample) {
            database.addUser("0", "192.168.49.20", "Andrew00", "andrew@gmail.com", "male", "Andrew", "Wand", "England", "London", "23-03-1997", "/photo","");
            database.addUser("2", "192.168.49.20", "Andrew1", "andrew@gmail.com", "male", "Andrew2", "Wand", "England", "London", "23-03-1997", "/photo","");
            database.createChat("2", "Andrew2", null);
            database.addMsg("Ciao", "2", "2");
            database.addMsg("WeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeWeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", "0", "2");
            database.addMsg("we", "0", "2");
            database.setRequest("2", "false");

            database.addUser("23", "192.168.49.20", "Andrew123", "andrew@12gmail.com", "male", "Andrewwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.createChat("23", "Andrew123", null);
            database.addMsg("Ciaooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo", "23", "23");

            database.addUser("25", "192.168.49.20", "Andrew345", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.createChat("25", "Andrew345", null);
            database.addMsg("wee", "25", "25");
            database.addMsg("Ciaooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo", "25", "25");

            database.addUser("27", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.createChat("27", "Andrew345", null);
            database.addMsg("wee", "27", "27");
            database.addMsg("Ciaooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo", "27", "27");

            database.addUser("28", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("29", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("30", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("31", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("32", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("33", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("34", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("35", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("36", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("37", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("38", "192.168.49.20", "Andreeeeeeeeeeeeeeeeeeeeeew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("39", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("40", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("41", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("42", "192.168.49.20", "Andrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("43", "192.168.49.20", "Andrew38888888888888888888888888886", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("44", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("45", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("46", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("47", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("48", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("50", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("51", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("52", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("53", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.addUser("54", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
        }
        database.addUser("0", null, "Andrew00", "andrew@gmail.com", "male", "Andrew", "Wand", "England", "London", "23-03-1997", "/photo","");
        //database.addUser("1", "192.168.49.20", "Andrew00", "andrew@gmail.com", "male", "Andrew", "Wand", "England", "London", "23-03-1997", "/photo","");

        createNotificationChannels();
        foregroundService = new MyForegroundService();
        Intent notificationIntent = new Intent(this, foregroundService.getClass());
        this.startForegroundService(notificationIntent);
        MessageController.newInstance(this);

    }

    private void createNotificationChannels() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        //Chat channel
        NotificationChannel channel = new NotificationChannel("chatMessageNotification", "Chat message", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        //Global channel
        channel = new NotificationChannel("globalMessageNotification", "Global message", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
    }

    private void requestPermissions(){
        //ADD PERMISSIONS THAT WILL BE REQUIRED ON THE ARRAY BELOW
        final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.FOREGROUND_SERVICE};
        ActivityCompat.requestPermissions(this, permissions, 101);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        }
        //ENDS PERMISSIONS REQUEST
    }

    private boolean xiamoiWifiPermission(){

        try {
            String manufacturer = "xiaomi";
            if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER) && !sharedPreferences.getBoolean("DoNotShowXiaomiPermissionFragment",false)) {
                return true;
            }else{
                return false;
            }
        } catch (ActivityNotFoundException e) {
            System.out.println("Not MIUI device");
            return false;
        }
    }

    private void loadTheme() {
        String theme = sharedPreferences.getString("appTheme", "light");
        if (theme.equals("light")) {
            lightOrDark = "Light";
            setTheme(R.style.AppTheme);
            setStatusAndNavbarColor(true);
        } else if (theme.equals("dark")) {
            lightOrDark = "Dark";
            setTheme(R.style.DarkTheme);
            setStatusAndNavbarColor(false);
        } else {
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            lightOrDark = "Follow System";
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                setTheme(R.style.DarkTheme);
                setStatusAndNavbarColor(false);
            } else {
                setTheme(R.style.AppTheme);
                setStatusAndNavbarColor(true);
            }
        }

    }

    private void setStatusAndNavbarColor(boolean light) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (light) {
            window.setStatusBarColor(getColor(R.color.colorPrimary));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.setStatusBarColor(getColor(R.color.darkColorPrimary));
        }
        window.setNavigationBarColor(Color.BLACK);
    }

    private void loadFragment(boolean transition) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (transition) {
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        transaction.replace(R.id.main_fragment, fragment);
        transaction.commit();
    }

    private void createCountDowntimer() {
        countDownTimer = new CountDownTimer(secondsRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                if(xiamoiWifiPermission()){
                    fragment = createXiaomiPermissionFragment();
                    loadFragment(true);
                    startTimer2 = false;
                }else {
                    if (firstLogin()) {
                        fragment = createLoginFragment();
                    } else {
                        fragment = createHomeFragment();
                    }
                    loadFragment(true);
                    startTimer2 = false;
                }
            }
        };
    }

    private HomeFragment createHomeFragment() {
        return new HomeFragment().newInstance(this, database,drawController);
    }

    private LoginFragment createLoginFragment() {
        return new LoginFragment().newInstance(this, database, accountController,drawController);
    }

    private XiaomiPermissionFragment createXiaomiPermissionFragment(){
        return new XiaomiPermissionFragment().newInstance(this, database, accountController, drawController);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (startTimer && startTimer2) {
            createCountDowntimer();
            countDownTimer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
        startTimer = true;
        System.out.println("Activity in stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("distrutta");
        System.exit(1);
    }

    public boolean isAccessibilityEnabled() {
        int accessibilityEnabled = 0;
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            System.out.println("***ACCESSIBILIY IS ENABLED***: ");

            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase("ACCESSIBILITY_SERVICE_NAME")) {
                        return true;
                    }
                }
            }

        } else {
            System.out.println("***ACCESSIBILIY IS DISABLED***");


        }
        return accessibilityFound;
    }

    public boolean firstLogin() {
        try {
            database.getUser("0");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Utente non trovato");
            return true;
        }
        return false;
    }

}