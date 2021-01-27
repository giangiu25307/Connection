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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Model.MapUsers;
import com.example.connection.Model.User;
import com.example.connection.R;

import java.util.ArrayList;
import java.util.Objects;

//D/SupplicantP2pIfaceCallbackExt: Provision discovery request for WPS Config method: 128
public class Connection extends AppCompatActivity {
    private Fragment fragment;
    private CountDownTimer countDownTimer;
    private Boolean startTimer = false;
    private Boolean startTimer2 = true;
    private long secondsRemaining = 1000;
    private SharedPreferences sharedPreferences;
    public static boolean boot = true;
    Database database;
    User user;
    ChatController chatController;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;
    ConnectionController connectionController;
    public static String fragmentName = "MAP";
    public static String lightOrDark = "light";
    public static ArrayList<MapUsers> mapUsers = new ArrayList<MapUsers>();
    public static String minAge = "", maxAge = "";
    public static String[] genders = {"","",""};
    private Fragment map, chat, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        database = new Database(this);
        connectionController = new ConnectionController(this, database, user);
        chatController = new ChatController();
        chatController = chatController.newIstance(this, connectionController);
        //LocalizationController localizationController=null;

        // localizationController=new LocalizationController(database,this);
        loadTheme();
        requestStoragePermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //user = new User("aaaaa", "ciao", "ciaoc", "ciao", "ciao", "ciao", "cioa", "ciao", "ciao", "25-03-2000", "ciao");
        fragment = new SplashScreenFragment();
        loadFragment(false);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {

        }
        createCountDowntimer();
        countDownTimer.start();
        map = new MapFragment().newInstance(connectionController, database);
        chat = new ChatFragment().newInstance(database, chatController, null);
        settings = new SettingsFragment().newInstance(connectionController, database, chatController, map, chat);
        //BluetoothScanner bluetoothScanner=new BluetoothScanner();

        boolean createSample = true;
        if (createSample) {
            database.addUser("0", "192.168.49.20", "Andrew00", "andrew@gmail.com", "male", "Andrew", "Wand", "England", "London", "23-03-1997", "/photo");
            database.addUser("2", "192.168.49.20", "Andrew1", "andrew@gmail.com", "male", "Andrew2", "Wand", "England", "London", "23-03-1997", "/photo");
            database.createChat("2", "Andrew2");
            database.addMsg("Ciao", "2", "2");
            database.addMsg("Wee", "0", "2");
            database.addUser("23", "192.168.49.20", "Andrew123", "andrew@12gmail.com", "ma123le", "Andr1ew2", "Wa131nd", "England", "London", "23-03-1997", "/photo");
            database.createChat("23", "Andrew123");
            database.addMsg("Ciao", "23", "23");
            database.addUser("25", "192.168.49.20", "Andrew345", "andrew@12gmail.com", "ma123le", "Andr1ew2", "Wa131nd", "England", "London", "23-03-1997", "/photo");
            database.createChat("25", "Andrew345");
            database.addMsg("Weee", "25", "25");
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
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_NO:
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    lightOrDark = "Follow System";
                    setTheme(R.style.AppTheme);
                    setStatusAndNavbarColor(true);
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    lightOrDark = "Follow System";
                    setTheme(R.style.DarkTheme);
                    setStatusAndNavbarColor(false);
                    break;
                default:
                    break;
            }
        }

    }

    private void setStatusAndNavbarColor(boolean light) {

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (light) {
            window.setStatusBarColor(getColor(R.color.colorPrimaryDark));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.setStatusBarColor(getColor(R.color.darkColorPrimaryDark));
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
                if(firstLogin()) {
                    fragment = new LoginFragment().newInstance(connectionController, database, chatController, map, chat, settings);
                }else {
                    fragment = new HomeFragment().newInstance(connectionController, database, chatController,map,chat);
                }
                loadFragment(true);
                startTimer2 = false;
            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (startTimer && startTimer2) {
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
        System.out.println("Activity in stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Activity distrutta");
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

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(this), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(this), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    public boolean firstLogin(){
        String myid="";
        try {
            myid = database.getUser("0").getString(0);
        }catch(IndexOutOfBoundsException e){
            System.out.println("Utente non trovato");
            return true;
        }
        return false;
    }
}