package com.example.connection.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.R;

import java.lang.reflect.Array;

public class MapFragment extends Fragment {

    ConnectionController connectionController;

    public MapFragment(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.map_fragment, null);

        //connectionController.getClientList();

        ListView listView = view.findViewById(R.id.listView);
        String [] array = {"Antonio","Giovanni"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.listview_row, R.id.textViewList, array);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){

                String name = (String) adapter.getItemAtPosition(position);
                System.out.println(name);

            }

        });

        return view;

    }

}
