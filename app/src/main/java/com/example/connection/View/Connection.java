package com.example.connection.View;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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

import com.example.connection.Controller.AccountController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.MessageController;
import com.example.connection.Database.Database;
import com.example.connection.Model.MapUsers;
import com.example.connection.R;
import com.example.connection.Services.MyForegroundService;
import com.example.connection.UDP_Connection.Multicast;
import com.example.connection.UDP_Connection.MyNetworkInterface;
import com.example.connection.vpn.LocalVPNService;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Objects;

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
    public static ArrayList<MapUsers> mapUsers = new ArrayList<MapUsers>();
    public static String minAge = "16", maxAge = "100";
    public static String[] genders = {"male", "female", "other"};
    private static final int VPN_REQUEST_CODE = 0x0F;
    private AccountController accountController;
    public static LocalVPNService localVPNService;
    private NotificationChannel channel;
    public static MyForegroundService foregroundService;

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
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment = new SplashScreenFragment();
        loadFragment(false);
        requestPermissions();
        xiamoiWifiPermission();

        //CHECKARE CI SIA QUALCUNO ALL'INTERNO DEL GRUPPO PRIMA DI MANDARE MESSAGGI INUTILI
        boolean createSample = false;
        if (createSample) {
            database.addUser("0", "192.168.49.20", "Andrew00", "andrew@gmail.com", "male", "Andrew", "Wand", "England", "London", "23-03-1997", "/photo","");
            database.addUser("2", "192.168.49.20", "Andrew1", "andrew@gmail.com", "male", "Andrew2", "Wand", "England", "London", "23-03-1997", "/photo","");
            database.createChat("2", "Andrew2", null);
            database.addMsg("Ciao", "2", "2");
            database.addMsg("WeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeWeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", "0", "2");
            database.addMsg("we", "0", "2");

            database.addUser("23", "192.168.49.20", "Andrew123", "andrew@12gmail.com", "male", "Andrewwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.createChat("23", "Andrew123", null);
            database.addMsg("Ciaooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo", "23", "23");

            database.addUser("25", "192.168.49.20", "Andrew345", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.createChat("25", "Andrew345", null);
            database.addMsg("wee", "25", "25");
            database.addMsg("Ciaooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo", "23", "23");

            database.addUser("27", "192.168.49.20", "Andrew386", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.createChat("27", "Andrew345", null);
            database.addMsg("wee", "27", "27");
            database.addMsg("Ciaooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo", "23", "23");

            database.addUser("29", "192.168.49.20", "Andrew980", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.createChat("29", "Andrew345", null);
            database.addMsg("wee", "29", "29");
            database.addMsg("Ciaooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo", "23", "23");

            database.addUser("31", "192.168.49.20", "Andrew132", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.createChat("31", "Andrew345", null);
            database.addMsg("wee", "31", "31");
            database.addMsg("Ciaooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo", "23", "23");

            database.addUser("33", "192.168.49.20", "Andrew847", "andrew@12gmail.com", "male", "Andrew", "Wa131nd", "England", "London", "23-03-1997", "/photo","");
            database.createChat("33", "Andrew345", null);
            database.addMsg("wee", "33", "33");
        }

        //database.addUser("0",null,"test0","test0@gmail.com","female","test0","test0","test0","test0","01-01-2000","nonlaho",null);//redminote7
        database.addUser("1",null,"test1","test1@gmail.com","male","test1","test1","test1","test1","01-01-2001","nothingToseehere",null);//xiaomia2litemio
        //database.addUser("2",null,"test2","test2@gmail.com","other","test2","test2","test2","test2","01-01-2002","macheccazonesoioscusi",null);//xiaomia2litesuo
        //database.addUser("3",null,"test3","test3@gmail.com","other","test3","test3","test3","test3","01-01-2003","azz",null);//s9
        createCountDowntimer();
        countDownTimer.start();
        foregroundService = new MyForegroundService();
        Intent notificationIntent = new Intent(this, foregroundService.getClass());
        this.startForegroundService(notificationIntent);
       MessageController.newInstance();

    }

    private void requestPermissions(){
        //ADD PERMISSIONS THAT WILL BE REQUIRED ON THE ARRAY BELOW
        final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.FOREGROUND_SERVICE};
        ActivityCompat.requestPermissions(this, permissions, 101);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        }
        //ENDS PERMISSIONS REQUEST
    }

    private void xiamoiWifiPermission(){
        try {
            String manufacturer = "xiaomi";
            if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
                //this will open auto start screen where user can enable permission for your app
                Intent intent1 = new Intent();
                intent1.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsTabActivity"));
                startActivity(intent1);
            }
        } catch (ActivityNotFoundException e) {
            System.out.println("Not MIUI device");
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
                if (firstLogin()) {
                    fragment = createLoginFragment();
                } else {
                    fragment = createHomeFragment();
                }
                loadFragment(true);
                startTimer2 = false;
            }
        };
    }

    private HomeFragment createHomeFragment() {
        return new HomeFragment().newInstance(this, database);
    }

    private LoginFragment createLoginFragment() {
        return new LoginFragment().newInstance(this, database, accountController);
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
        String myid = "";
        try {
            myid = database.getUser("0").getString(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Utente non trovato");
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            //waitingForVPNStart = true;
            localVPNService = new LocalVPNService();
            this.startService(new Intent(this, localVPNService.getClass()));
            //enableButton(false);
        }
    }

    public void startVpn() {

        Intent vpnIntent = VpnService.prepare(this);

        if (vpnIntent != null)
            this.startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        else
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
    }
}