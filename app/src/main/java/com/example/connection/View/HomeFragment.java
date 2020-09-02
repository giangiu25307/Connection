package com.example.connection.View;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Model.Chats;
import com.example.connection.R;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    private Fragment fragment;
    private ConnectionController connectionController;
    private Database database;
    private ChatController chatController;
    private BottomNavigationView bottomNavigationMenu;
    private HomeFragment homeFragment;
    private Fragment map, chat;

    public HomeFragment() {

    }

    public HomeFragment newInstance(ConnectionController connectionController, Database database, ChatController chatController, Fragment map, Fragment chat) {
        homeFragment = new HomeFragment();
        homeFragment.setConnectionController(connectionController);
        homeFragment.setChatController(chatController);
        homeFragment.setDatabase(database);
        homeFragment.setChat(chat);
        homeFragment.setMap(map);
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.home_fragment, null);

        //chipNavigationBar = view.findViewById(R.id.chip_navigation_bar);
        System.out.println("Fragment creato");

        bottomNavigationMenu = view.findViewById(R.id.bottomNavigationMenu);
        bottomNavigationMenu.setOnNavigationItemSelectedListener(bottomNavigationMenuListener);
        bottomNavigationMenu.setItemIconTintList(null);

        if (savedInstanceState == null && Connection.fragmentName.equals("MAP")) {
            bottomNavigationMenu.getMenu().getItem(0).setChecked(true);
            fragment = map;
            loadFragment();
        } else if (savedInstanceState == null && Connection.fragmentName.equals("CHAT")) {
            bottomNavigationMenu.getMenu().getItem(1).setChecked(true);
            fragment = chat;
            loadFragment();
        } else if (savedInstanceState == null && Connection.fragmentName.equals("SETTINGS")) {
            bottomNavigationMenu.getMenu().getItem(2).setChecked(true);
            fragment = new SettingsFragment().newInstance(connectionController,database,chatController,map,chat);
            loadFragment();
        }

        /*
        bottomNavigationMenu.OnNa (new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.map:
                        fragment = new MapFragment().newInstance(connectionController, database);
                        Connection.fragmentName = "MAP";
                        break;
                    case R.id.chat:
                        fragment = new ChatFragment().newInstance(database, chatController);
                        Connection.fragmentName = "CHAT";
                        break;
                    case R.id.settings:
                        fragment = new SettingsFragment().newInstance(connectionController, database, chatController);
                        Connection.fragmentName = "SETTINGS";
                        break;
                    default:
                        break;
                }

                loadFragment();

            }
        });
        */

        return view;

    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationMenuListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.map:
                    //if (Connection.fragmentName.equals("MAP")) break;
                    Connection.fragmentName = "MAP";
                    fragment = map;
                    break;
                case R.id.chat:
                    //if (Connection.fragmentName.equals("CHAT")) break;
                    Connection.fragmentName = "CHAT";
                    fragment = chat;
                    break;
                case R.id.settings:
                    //if (Connection.fragmentName.equals("SETTINGS")) break;
                    Connection.fragmentName = "SETTINGS";
                    fragment = new SettingsFragment().newInstance(connectionController,database,chatController,map,chat);
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

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setMap(Fragment map) {
        this.map = map;
    }

    public void setChat(Fragment chat) {
        this.chat = chat;
    }

    public Fragment getMap() {
        return map;
    }

    public Fragment getChat() {
        return chat;
    }

}


