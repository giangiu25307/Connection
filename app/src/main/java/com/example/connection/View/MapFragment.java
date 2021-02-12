package com.example.connection.View;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Controller.DrawController;
import com.example.connection.Model.User;
import com.example.connection.R;

import java.util.ArrayList;

public class MapFragment extends Fragment implements View.OnClickListener {

    private ConnectionController connectionController;
    private User user;
    private Database database;
    private ImageView filterImage;
    private DrawController drawController;

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
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.lyt_map, null);
        setHasOptionsMenu(true);


        Cursor c = connectionController.getAllClientList().get();
        c.moveToFirst();

        final ArrayList<User> userList = new ArrayList<>();
        String[] arrayName = new String[c.getCount()];
        for (int i = 0; i < c.getCount(); i++) {
            user = new User(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10));
            userList.add(user);
            arrayName[i] = c.getString(1);
            c.moveToNext();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.listview_row, R.id.textViewList, arrayName);
        AbsoluteLayout mapLayout = view.findViewById(R.id.mapLayout);
        drawController = new DrawController(mapLayout.getContext(), userList, mapLayout);
        mapLayout.addView(drawController);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filterIcon:

                break;
            case R.id.gpsIcon:
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.filterIcon:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.dialog_map_filter);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                final LinearLayout genderLayout, ageLayout;
                final TextView cancelTextView, applyTextView;
                genderLayout = alertDialog.findViewById(R.id.genderLayout);
                ageLayout = alertDialog.findViewById(R.id.ageLayout);
                cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
                applyTextView = alertDialog.findViewById(R.id.applyTextView);

                //gender alert dialog
                genderLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                        dialogBuilder.setView(R.layout.dialog_map_filter_gender);
                        final AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();
                        final TextView cancelTextView, applyTextView;
                        final CheckBox male, female, other;

                        cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
                        applyTextView = alertDialog.findViewById(R.id.applyTextView);
                        male = null;
                        female = null;
                        other = null;
                        if (Connection.genders[0] != null && Connection.genders[0].equals("male"))
                            male.setChecked(true);
                        if (Connection.genders[1] != null && Connection.genders[1].equals("female"))
                            female.setChecked(true);
                        if (Connection.genders[2] != null && Connection.genders[2].equals("other"))
                            other.setChecked(true);

                        cancelTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        applyTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (male != null && male.isChecked()) Connection.genders[0] = "male";
                                else Connection.genders[0] = "";
                                if (female != null && female.isChecked())
                                    Connection.genders[1] = "female";
                                else Connection.genders[1] = "";
                                if (other != null && other.isChecked())
                                    Connection.genders[2] = "other";
                                else Connection.genders[2] = "";
                                alertDialog.dismiss();
                            }
                        });
                    }
                });

                //age alert dialog
                ageLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                        dialogBuilder.setView(R.layout.dialog_map_filter_age);
                        final AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();
                        final TextView cancelTextView, applyTextView;

                        final EditText editTextMinAge = alertDialog.findViewById(R.id.editTextMinAge);
                        final EditText editTextMaxAge = alertDialog.findViewById(R.id.editTextMaxAge);

                        cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
                        applyTextView = alertDialog.findViewById(R.id.applyTextView);
                        if (!Connection.minAge.isEmpty()) editTextMinAge.setText(Connection.minAge);
                        if (!Connection.maxAge.isEmpty()) editTextMinAge.setText(Connection.maxAge);
                        cancelTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        applyTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Connection.minAge = editTextMinAge.getText().toString();
                                Connection.maxAge = editTextMaxAge.getText().toString();
                                alertDialog.dismiss();
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
                        drawController.applyFilters(Connection.minAge,Connection.maxAge,Connection.genders);
                        alertDialog.dismiss();
                        Fragment fragment = new MapFragment().newInstance(connectionController,database);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.home_fragment, fragment).commit();
                    }
                });
                break;
            case R.id.gpsIcon:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

