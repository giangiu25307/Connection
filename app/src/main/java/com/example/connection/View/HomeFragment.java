package com.example.connection.View;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Database.Database;
import com.example.connection.R;
import com.example.connection.TCP_Connection.Encryption;
import com.example.connection.TCP_Connection.TcpClient;
import com.example.connection.TCP_Connection.TcpServer;
import com.example.connection.UDP_Connection.Multicast_P2P;
import com.example.connection.UDP_Connection.Multicast_WLAN;
import com.example.connection.UDP_Connection.MyNetworkInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class HomeFragment extends Fragment {

    private Fragment fragment;
    private ConnectionController connectionController;
    private Database database;
    private ChatController chatController;
    private BottomNavigationView bottomNavigationMenu;
    private HomeFragment homeFragment;
    private MapFragment map;
    private ChatFragment chat;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private SettingsFragment settings;
    private Connection connection;

    public HomeFragment() {

    }

    public HomeFragment newInstance(Connection connection, Database database) {
        homeFragment = new HomeFragment();
        homeFragment.setConnection(connection);
        homeFragment.setDatabase(database);
        homeFragment.setConnectionController();
        homeFragment.setChatController();
        homeFragment.setChat();
        homeFragment.setMap();
        homeFragment.setSettings();
        return homeFragment;
    }

    public HomeFragment newInstance(Connection connection, ConnectionController connectionController, Database database, ChatController chatController, MapFragment map, ChatFragment chat, SettingsFragment settings) {
        homeFragment = new HomeFragment();
        homeFragment.setConnection(connection);
        homeFragment.setDatabase(database);
        homeFragment.setConnectionController(connectionController);
        homeFragment.setChatController(chatController);
        homeFragment.setChat(chat);
        homeFragment.setMap(map);
        homeFragment.setSettings(settings);
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.lyt_home, null);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);

        bottomNavigationMenu = view.findViewById(R.id.bottomNavigationMenu);
        bottomNavigationMenu.setOnNavigationItemSelectedListener(bottomNavigationMenuListener);
        bottomNavigationMenu.setItemIconTintList(null);

        if (savedInstanceState == null && Connection.fragmentName.equals("MAP")) {
            bottomNavigationMenu.getMenu().getItem(0).setChecked(true);
            fragment = map;
            toolbarTitle.setText("Explore");
            loadFragment();
        } else if (savedInstanceState == null && Connection.fragmentName.equals("CHAT")) {
            bottomNavigationMenu.getMenu().getItem(1).setChecked(true);
            fragment = new ChatFragment().newInstance(database, chatController, toolbar);
            loadFragment();
        } else if (savedInstanceState == null && Connection.fragmentName.equals("SETTINGS")) {
            bottomNavigationMenu.getMenu().getItem(2).setChecked(true);
            fragment = new SettingsFragment().newInstance(connection, connectionController, database, chatController, map, chat);
            toolbarTitle.setText("Settings");
            loadFragment();
        }

        return view;

    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationMenuListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.home:
                    //if (Connection.fragmentName.equals("MAP")) break;
                    Connection.fragmentName = "MAP";
                    toolbarTitle.setText("Explore");
                    fragment = map;
                    break;
                case R.id.chat:
                    //if (Connection.fragmentName.equals("CHAT")) break;
                    Connection.fragmentName = "CHAT";
                    fragment = new ChatFragment().newInstance(database, chatController, toolbar);
                    break;
                case R.id.settings:
                    //if (Connection.fragmentName.equals("SETTINGS")) break;
                    Connection.fragmentName = "SETTINGS";
                    toolbarTitle.setText("Settings");
                    fragment = new SettingsFragment().newInstance(connection, connectionController, database, chatController, map, chat);
                    break;
                default:
                    break;
            }

            loadFragment();
            return true;

        }
    };

    private void loadFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.home_fragment, fragment).commit();
    }

    public void setChatController() {
        this.chatController = ChatController.getInstance();
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setMap() {
        this.map = new MapFragment().newInstance(connectionController, database);
    }

    public void setChat() {
        this.chat = new ChatFragment().newInstance(database, chatController, toolbar);
    }

    public void setSettings() {
        this.settings = new SettingsFragment().newInstance(connection, connectionController, database, chatController, map, chat);
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    public void setConnectionController() {
       this.connectionController = new ConnectionController(connection, database);
       //connectionController.active4G();
       //connectionController.initProcess();
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public void setMap(MapFragment map) {
        this.map = map;
    }

    public void setChat(ChatFragment chat) {
        this.chat = chat;
    }

    public void setSettings(SettingsFragment settings) {
        this.settings = settings;
    }

    public Fragment getMap() {
        return map;
    }

    public Fragment getChat() {
        return chat;
    }

}


