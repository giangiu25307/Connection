package com.ConnectionProject.connection.View;

import android.annotation.SuppressLint;
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

import com.ConnectionProject.connection.Controller.ChatController;
import com.ConnectionProject.connection.Controller.ConnectionController;
import com.ConnectionProject.connection.Controller.DrawController;
import com.ConnectionProject.connection.Database.Database;
import com.ConnectionProject.connection.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    private Fragment fragment;
    private ConnectionController connectionController;
    private Database database;
    private ChatController chatController;
    private BottomNavigationView bottomNavigationMenu;
    private static HomeFragment homeFragment;
    private MapFragment map;
    private ChatFragment chat;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private SettingsFragment settings;
    private Connection connection;
    private DrawController drawController;
    private String lastFragmentName;

    public HomeFragment() {

    }

    public HomeFragment newInstance(Connection connection, Database database, DrawController drawController) {
        homeFragment = new HomeFragment();
        homeFragment.setConnection(connection);
        homeFragment.setDatabase(database);
        homeFragment.setDrawController(drawController);
        homeFragment.setConnectionController();
        homeFragment.setChatController();
        homeFragment.setChat();
        homeFragment.setMap();
        homeFragment.setSettings();
        homeFragment.setLastFragmentName(Connection.fragmentName);
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

    public static HomeFragment getInstance(){
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
        homeFragment = HomeFragment.getInstance();
        bottomNavigationMenu = view.findViewById(R.id.bottomNavigationMenu);
        bottomNavigationMenu.setOnNavigationItemSelectedListener(bottomNavigationMenuListener);
        bottomNavigationMenu.setItemIconTintList(null);
        Connection.fragmentName = homeFragment.getLastFragmentName();

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
        this.map = new MapFragment().newInstance(connectionController, database, drawController);
    }

    public void setChat() {
        this.chat = new ChatFragment().newInstance(database, chatController, toolbar);
    }

    public void setDrawController(DrawController drawController) {
        this.drawController = drawController;
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

    public String getLastFragmentName() {
        return homeFragment.lastFragmentName;
    }

    public void setLastFragmentName(String lastFragmentName) {
        this.lastFragmentName = lastFragmentName;
    }

    @Override
    public void onPause() {
        super.onPause();
        homeFragment.setLastFragmentName(Connection.fragmentName);
    }

    @Override
    public void onStop() {
        super.onStop();
        homeFragment.setLastFragmentName(Connection.fragmentName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeFragment.setLastFragmentName(Connection.fragmentName);
    }

    @Override
    public void onResume() {
        super.onResume();
        Connection.fragmentName = homeFragment.getLastFragmentName();
    }
}


