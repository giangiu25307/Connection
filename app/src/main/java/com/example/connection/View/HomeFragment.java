package com.example.connection.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomeFragment extends Fragment {

    ChipNavigationBar chipNavigationBar;
    Fragment fragment;
    ConnectionController connectionController;
    Database database;
    ChatController chatController;
    public HomeFragment() {

    }

    public HomeFragment newInstance(ConnectionController connectionController, Database database,ChatController chatController) {
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setConnectionController(connectionController);
        homeFragment.setChatController(chatController);
        homeFragment.setDatabase(database);
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.home_fragment, null);

        chipNavigationBar = view.findViewById(R.id.chip_navigation_bar);

        if(savedInstanceState == null){
            chipNavigationBar.setItemSelected(R.id.map, true);
            fragment = new MapFragment().newInstance(connectionController);
            loadFragment();
        }

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i){
                    case R.id.map:
                        fragment = new MapFragment().newInstance(connectionController);
                        break;
                    case R.id.chat:
                        fragment = new ChatFragment().newInstance(database,chatController);
                        break;
                    case R.id.settings:
                        fragment = new SettingsFragment().newInstance();
                        break;
                    default:
                        break;
                }

                loadFragment();

            }
        });

        return view;

    }

    private void loadFragment(){
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
}
