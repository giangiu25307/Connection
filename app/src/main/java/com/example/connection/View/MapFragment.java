package com.example.connection.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Controller.DrawController;
import com.example.connection.Model.User;
import com.example.connection.R;

import java.util.ArrayList;

public class MapFragment extends Fragment implements View.OnClickListener {

    ConnectionController connectionController;
    User user;
    Database database;
    ImageView filterImage;

    public MapFragment() {

    }

    public MapFragment newInstance(ConnectionController connectionController, Database database) {
        MapFragment mapFragment = new MapFragment();
        mapFragment.setConnectionController(connectionController);
        mapFragment.setDatabase(database);
        return mapFragment;
    }

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.map_fragment, null);
        filterImage = view.findViewById(R.id.filterButton);
        filterImage.setOnClickListener(this);

        Cursor c = connectionController.getAllClientList().get();
        c.moveToFirst();
        //ListView listView = view.findViewById(R.id.listView);

        final ArrayList<User> userList = new ArrayList<>();
        String[] arrayName = new String[c.getCount()];
        for (int i = 0; i < c.getCount(); i++) {
            user = new User(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10));
            userList.add(user);
            arrayName[i] = c.getString(1);
            c.moveToNext();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.listview_row, R.id.textViewList, arrayName);
        /*listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {

                user = userList.get(position);
                Intent myIntent = new Intent(getActivity(), ChatActivity.class);
                myIntent.putExtra("idChat", user.getIdUser());
                myIntent.putExtra("name", user.getUsername());
                getActivity().startActivity(myIntent);
                String information = user.toString();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(), information, duration);
                toast.show();

            }

        });*/

        AbsoluteLayout mapLayout = view.findViewById(R.id.mapLayout);
        DrawController drawController = new DrawController(mapLayout.getContext(),userList,mapLayout);
        mapLayout.addView(drawController);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filterButton:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.filter_alert_dialog);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                final TextView genderTextView, ageTextView, cancelTextView, applyTextView;
                genderTextView = alertDialog.findViewById(R.id.genderTextView);
                ageTextView = alertDialog.findViewById(R.id.ageTextView);
                cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
                applyTextView = alertDialog.findViewById(R.id.applyTextView);

                genderTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                        dialogBuilder.setView(R.layout.gender_alert_dialog_with_checkbox);
                        final AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();
                        final TextView cancelTextView, applyTextView;

                        cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
                        applyTextView = alertDialog.findViewById(R.id.applyTextView);

                        cancelTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        applyTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                });

                ageTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                        dialogBuilder.setView(R.layout.age_alert_dialog);
                        final AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();
                        final TextView cancelTextView, applyTextView;

                        cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
                        applyTextView = alertDialog.findViewById(R.id.applyTextView);

                        cancelTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        applyTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                });

                cancelTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                applyTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                break;
            case R.id.gpsButton:
                break;
            default:
                break;
        }
    }

}

