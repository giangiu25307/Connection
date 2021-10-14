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
    int layoutWidth;
    int layoutHeight;

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
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        setHasOptionsMenu(true);
        //View newView = LayoutInflater.from(getContext()).inflate(R.layout.lyt_map_user, view.findViewById(R.id.mapGridLayout), false);
        getScreenDimension();
        addViewToLayout(view, initializeUserArray());
        /*drawing(view);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(!Multicast.dbUserEvent){
                        Multicast.dbUserEvent=true;
                        MapFragment fragment = new MapFragment().newInstance(connectionController,database);
                        fragmentManager.beginTransaction().replace(R.id.home_fragment, fragment).commitAllowingStateLoss();
                    }
                }
            }
        });
        thread.start();*/
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                layoutWidth = view.getWidth();
                layoutHeight = view.getHeight();
            }
        });
        return view;
    }

    public void drawing(View view) {
        Cursor c = database.getAllUsers();
        c.moveToFirst();
        final ArrayList<User> userList = new ArrayList<>();
        String[] arrayName = new String[c.getCount() == 0 ? 1 : c.getCount()];

        //userList.add(ConnectionController.myUser);
        //arrayName[0] = ConnectionController.myUser.getName();

        for (int i = 0; i < c.getCount(); i++) {
            if (i == 0) {
                if (MyNetworkInterface.getMyP2pNetworkInterface("p2p0") != null) {
                    user = new User(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10));
                    userList.add(user);
                    arrayName[i] = c.getString(1);
                }
            }else {
                user = new User(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10));
                userList.add(user);
                arrayName[i] = c.getString(1);
            }
            c.moveToNext();
        }
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.listview_row, R.id.textViewList, arrayName);
        AbsoluteLayout mapLayout = view.findViewById(R.id.mapLayout);
        drawController = new DrawController(mapLayout.getContext(), userList, mapLayout);
        mapLayout.addView(drawController);
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
                        Connection.minAge = minAge.getText().toString();
                        Connection.maxAge = maxAge.getText().toString();
                        drawController.applyFilters(Connection.minAge, Connection.maxAge, Connection.genders);
                        alertDialog.dismiss();
                        Fragment fragment = new MapFragment().newInstance(connectionController, database);
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

    private ArrayList<User> initializeUserArray(){
        Cursor c = database.getAllUsers();
        c.moveToFirst();
        final ArrayList<User> userList = new ArrayList<>();
        String[] arrayName = new String[c.getCount() == 0 ? 1 : c.getCount()];

        userList.add(ConnectionController.myUser);
        arrayName[0] = ConnectionController.myUser.getName();

        for (int i = 0; i < c.getCount(); i++) {
            if (i == 0) {
                if (MyNetworkInterface.getMyP2pNetworkInterface("p2p0") != null) {
                    user = new User(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10));
                    userList.add(user);
                    arrayName[i] = c.getString(1);
                }
            }else {
                user = new User(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10));
                userList.add(user);
                arrayName[i] = c.getString(1);
            }
            c.moveToNext();
        }
        return userList;
    }

    private void addViewToLayout(View view, ArrayList<User> userArrayList){
        FlowLayout parent = view.findViewById(R.id.mapGridLayout);

        for(int i = 0; i < (Math.min(userArrayList.size(), 25)); i++){
            user = userArrayList.get(i);
            //createLayout(user);
            parent.addView(createLayout(user));
        }

    }

    private LinearLayout createLayout(User user){
        LinearLayout linearLayout = new LinearLayout(getContext());
        //linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(Drawable.createFromPath(user.getProfilePic()));
        System.out.println("Misure di default: " + layoutWidth + " " + layoutHeight);
        System.out.println("Misure dpToPx: " + dpToPx((layoutWidth / 5) - 2) + " " + dpToPx((layoutHeight / 5) - 2));
        System.out.println("Misure px: " + ((layoutWidth / 5) - 2) + " " + ((layoutHeight / 5) - 2));
        imageView.setLayoutParams(new ViewGroup.LayoutParams((layoutWidth / 5) - 2, (layoutHeight / 5) - 2));
        //imageView.getLayoutParams().width = dpToPx(75);
        //imageView.getLayoutParams().height = dpToPx(75);
        TextView textView = new TextView(getContext());
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f);
        tableLayoutParams.setMargins(0, dpToPx(5), 0, 0);
        textView.setLayoutParams(tableLayoutParams);
        textView.setText(user.getUsername());
        //TODO Da cambiare in base al tema
        textView.setTextColor(Color.WHITE);
        linearLayout.addView(imageView);
        linearLayout.addView(textView);
        return linearLayout;
    }

    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void getScreenDimension(){
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        layoutWidth = size.x;
        layoutHeight = size.y;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

