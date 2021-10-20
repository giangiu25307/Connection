package com.example.connection.View;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.connection.Adapter.MapAdapter;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.DrawController;
import com.example.connection.Controller.MessageController;
import com.example.connection.Database.Database;
import com.example.connection.Model.User;
import com.example.connection.R;
import com.example.connection.UDP_Connection.Multicast;
import com.example.connection.UDP_Connection.MyNetworkInterface;
import com.example.connection.View.Layout.FlowLayout;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    private ConnectionController connectionController;
    private User user;
    private Database database;
    private ImageView filterImage;
    private DrawController drawController;

    public MapFragment() {

    }

    public MapFragment newInstance(ConnectionController connectionController, Database database, DrawController drawController) {
        MapFragment mapFragment = new MapFragment();
        mapFragment.setConnectionController(connectionController);
        mapFragment.setDatabase(database);
        mapFragment.setDrawController(drawController);
        return mapFragment;
    }

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setDrawController(DrawController drawController) {
        this.drawController = drawController;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.lyt_map, null);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        setHasOptionsMenu(true);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                drawController.init(getContext(),view,view.getHeight(), view.getWidth());
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filterIcon:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.dialog_map_filter);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                final TextView cancelTextView, applyTextView, male = alertDialog.findViewById(R.id.male),female = alertDialog.findViewById(R.id.female),other=alertDialog.findViewById(R.id.other);
                final EditText minAge = alertDialog.findViewById(R.id.editTextMinAge), maxAge=alertDialog.findViewById(R.id.editTextMaxAge);
                cancelTextView = alertDialog.findViewById(R.id.cancelTextView);
                applyTextView = alertDialog.findViewById(R.id.applyTextView);

                //Gender
                male.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Connection.genders[0].equals("")) {
                            male.setTextAppearance(R.style.genderSelected);
                            male.setBackgroundResource(R.drawable.bg_gender_filter_selected);
                            Connection.genders[0] = "male";
                        }else{
                            male.setTextAppearance(R.style.genderUnselected);
                            male.setBackgroundResource(R.drawable.bg_gender_filter_unselected);
                            Connection.genders[0] = "";
                        }
                    }
                });
                female.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Connection.genders[1].equals("")) {
                            female.setTextAppearance(R.style.genderSelected);
                            female.setBackgroundResource(R.drawable.bg_gender_filter_selected);
                            Connection.genders[1] = "female";
                        }else{
                            female.setTextAppearance(R.style.genderUnselected);
                            female.setBackgroundResource(R.drawable.bg_gender_filter_unselected);

                            Connection.genders[1] = "";
                        }
                    }
                });
                other.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Connection.genders[2].equals("")) {
                            other.setTextAppearance(R.style.genderSelected);
                            other.setBackgroundResource(R.drawable.bg_gender_filter_selected);
                            Connection.genders[2] = "other";
                        }else{
                            other.setTextAppearance(R.style.genderUnselected);
                            other.setBackgroundResource(R.drawable.bg_gender_filter_unselected);
                            Connection.genders[2] = "";
                        }
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
                        Connection.minAge = Integer.parseInt(minAge.getText().toString());
                        Connection.maxAge = Integer.parseInt(maxAge.getText().toString());
                        drawController.applyFilters(Connection.minAge, Connection.maxAge, Connection.genders);
                        alertDialog.dismiss();
                        Fragment fragment = new MapFragment().newInstance(connectionController, database, drawController);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.home_fragment, fragment).commit();
                    }
                });
                break;
            case R.id.gpsIcon:
                Intent intent = new Intent(getContext(), MessageController.getIstance().getClass());
                intent.putExtra("intentType", "messageController");
                intent.putExtra("communicationType", "tcp");
                intent.putExtra("msg", "porco dio");
                intent.putExtra("idChat", "2");
                getContext().sendBroadcast(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

