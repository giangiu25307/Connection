package com.example.connection.View;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.connection.Controller.AutoClicker;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Model.User;
import com.example.connection.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MapFragment extends Fragment {

    ConnectionController connectionController;
    User user;

    public MapFragment() {
    }

    public MapFragment newInstance(ConnectionController connectionController) {
        MapFragment mapFragment = new MapFragment();
        mapFragment.setConnectionController(connectionController);
        return mapFragment;
    }

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
          @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.map_fragment, null);

            Cursor c = connectionController.getAllClientList().get();
            c.moveToFirst();
            ListView listView = view.findViewById(R.id.listView);

            final ArrayList<User> userList = new ArrayList<>();
            String[] arrayName = new String[c.getCount()];
            for (int i = 0; i < c.getCount(); i++) {
                user = new User(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10));
                userList.add(user);
                arrayName[i] = c.getString(1);
                c.moveToNext();

            }
            ImageView gpsbutton=view.findViewById(R.id.gpsButton);
            gpsbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AutoClicker autoClicker=AutoClicker.getInstance();
                    autoClicker.clicker();
                }
            });
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.listview_row, R.id.textViewList, arrayName);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {

                    user = userList.get(position);

                    String information = user.toString();

                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getContext(), information, duration);
                    toast.show();

                }

            });
        return view;
        }
    }

