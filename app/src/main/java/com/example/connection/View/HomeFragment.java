package com.example.connection.View;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.example.connection.Controller.Database;
import com.example.connection.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    private Fragment fragment;
    private ConnectionController connectionController;
    private Database database;
    private ChatController chatController;
    private BottomNavigationView bottomNavigationMenu;
    private HomeFragment homeFragment;
    private Fragment map, chat;
    private int currentColor;
    private int[][] states;
    private int[] colors;
    private ColorStateList navigationViewColorStateList;
    private Toolbar toolbar;
    private TextView toolbarTitle;

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
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.lyt_home, null);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);

        bottomNavigationMenu = view.findViewById(R.id.bottomNavigationMenu);
        bottomNavigationMenu.setOnNavigationItemSelectedListener(bottomNavigationMenuListener);
        bottomNavigationMenu.setItemIconTintList(null);

        states = new int[][]{new int[]{-android.R.attr.state_checked}, new int[]{android.R.attr.state_checked}, new int[]{}};
        currentColor = getContext().getColor(R.color.pink);

        if (savedInstanceState == null && Connection.fragmentName.equals("MAP")) {

            bottomNavigationMenu.getMenu().getItem(0).setChecked(true);
            fragment = map;
            currentColor = getContext().getColor(R.color.colorAccent);
            toolbarTitle.setText("Explore");
            loadFragment();
        } else if (savedInstanceState == null && Connection.fragmentName.equals("CHAT")) {
            bottomNavigationMenu.getMenu().getItem(1).setChecked(true);
            fragment = new ChatFragment().newInstance(database, chatController, toolbar);
            currentColor = getContext().getColor(R.color.colorAccent);
            loadFragment();
        } else if (savedInstanceState == null && Connection.fragmentName.equals("SETTINGS")) {
            bottomNavigationMenu.getMenu().getItem(2).setChecked(true);
            fragment = new SettingsFragment().newInstance(connectionController,database,chatController,map,chat);
            currentColor = getContext().getColor(R.color.colorAccent);
            toolbarTitle.setText("Settings");
            loadFragment();
        }

        return view;

    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationMenuListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.map:
                    //if (Connection.fragmentName.equals("MAP")) break;
                    Connection.fragmentName = "MAP";
                    toolbarTitle.setText("Explore");
                    fragment = map;
                    currentColor = getContext().getColor(R.color.colorAccent);
                    break;
                case R.id.chat:
                    //if (Connection.fragmentName.equals("CHAT")) break;
                    Connection.fragmentName = "CHAT";
                    fragment = new ChatFragment().newInstance(database, chatController, toolbar);
                    currentColor = getContext().getColor(R.color.colorAccent);
                    break;
                case R.id.settings:
                    //if (Connection.fragmentName.equals("SETTINGS")) break;
                    Connection.fragmentName = "SETTINGS";
                    toolbarTitle.setText("Settings");
                    fragment = new SettingsFragment().newInstance(connectionController,database,chatController,map,chat);
                    currentColor = getContext().getColor(R.color.colorAccent);
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
        colors = new int[]{getContext().getColor(R.color.darkHintcolor), currentColor, getContext().getColor(R.color.darkHintcolor)};
        navigationViewColorStateList = new ColorStateList(states, colors);
        bottomNavigationMenu.setItemTextColor(navigationViewColorStateList);
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


