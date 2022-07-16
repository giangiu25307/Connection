package com.example.connection.View;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.DrawController;
import com.example.connection.Database.Database;
import com.example.connection.Model.User;
import com.example.connection.R;
import com.example.connection.View.Layout.FlowLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MapFragment extends Fragment implements View.OnClickListener {

    private ConnectionController connectionController;
    private User user;
    private Database database;
    private ImageView filterImage;
    private DrawController drawController;
    private SharedPreferences sharedPreferences;
    int layoutWidth;
    int layoutHeight;
    FlowLayout parent;
    ArrayList<User> userList;
    private static MapFragment mapFragment;
    private Button backButton, nextButton;
    private CountDownTimer countDownTimer;
    private int waitToScan = 0;

    public MapFragment() {

    }

    public static MapFragment getIstance() {
        return mapFragment;
    }

    public MapFragment newInstance(ConnectionController connectionController, Database database, DrawController drawController) {
        mapFragment = new MapFragment();
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

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }

    public void setDrawController(DrawController drawController) {
        this.drawController = drawController;
    }

    public DrawController getDrawController() {
        return mapFragment.drawController;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.lyt_map, null);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        setHasOptionsMenu(true);
        backButton = view.findViewById(R.id.backMapButton);
        mapFragment.nextButton = view.findViewById(R.id.nextMapButton);
        if (mapFragment.userList != null && mapFragment.userList.size() - (Connection.page * 25) <= 0) {
            if (Connection.page != 0) {
                Connection.page = 0;
            }
            mapFragment.nextButton.setClickable(false);
            mapFragment.nextButton.setAlpha(0.5f);
        }
        mapFragment.parent = view.findViewById(R.id.mapFlowLayout);
        mapFragment.waitToScan = 0;
        mapFragment.sharedPreferences = getContext().getSharedPreferences("utils", Context.MODE_PRIVATE);
        mapFragment.parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapFragment.parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                graphicRefresh();
            }
        });

        return view;
    }

    public void graphicRefresh() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapFragment.setUserList(mapFragment.database.getAllFilteredUsers());
                mapFragment.drawController.init(getContext(), mapFragment.parent, mapFragment.parent.getHeight(), mapFragment.parent.getWidth(), mapFragment.userList);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("utils", Context.MODE_PRIVATE);
        if (!isNotificationChannelEnabled("chatMessageNotification") && !sharedPreferences.getBoolean("notificationsPopupShown", false)) {

            Snackbar snackbar = Snackbar.make(view, "", 8000);
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
            layout.setBackgroundColor(getContext().getColor(R.color.transparent));
            TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setVisibility(View.INVISIBLE);
            View snackView = getActivity().getLayoutInflater().inflate(R.layout.lyt_notification_snackbar, null);
            ImageButton imageButton = snackView.findViewById(R.id.imageView12);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getString(R.string.packagename))
                            .putExtra(Settings.EXTRA_CHANNEL_ID, "chatMessageNotification");
                    startActivity(settingsIntent);
                }
            });
            layout.setPadding(5, 5, 5, 5);
            layout.addView(snackView, 0);
            snackbar.show();
            sharedPreferences.edit().putBoolean("notificationsPopupShown", true).apply();
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        dialogBuilder.setView(R.layout.dialog_information_for_network);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        Button button = alertDialog.findViewById(R.id.okButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                connectionController.initProcess();
            }
        });
        button.setClickable(false);

        new CountDownTimer(50000, 1000) {
            @Override
            public void onTick(long l) {
                button.setText("Ok (" + ((int)l/1000) + ")");
            }
            @Override
            public void onFinish() {
                button.setText("Ok");
                button.setClickable(true);
            }
        }.start();
    }

    private boolean isNotificationChannelEnabled(@Nullable String channelId) {
        if (!TextUtils.isEmpty(channelId)) {
            NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = manager.getNotificationChannel(channelId);
            return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.map_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scanAgainIcon:
                if(mapFragment.waitToScan == 0)
                    manageScanAgain(item);
                else
                    Toast.makeText(mapFragment.getContext(), "Wait "+mapFragment.waitToScan +"s",Toast.LENGTH_SHORT).show();
                break;
            case R.id.filterIcon:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.dialog_map_filter);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                final Button male = alertDialog.findViewById(R.id.male), female = alertDialog.findViewById(R.id.female), other = alertDialog.findViewById(R.id.other), cancelButton, applyButton;
                final EditText minAge = alertDialog.findViewById(R.id.editTextMinAge), maxAge = alertDialog.findViewById(R.id.editTextMaxAge);
                cancelButton = alertDialog.findViewById(R.id.cancelButton);
                applyButton = alertDialog.findViewById(R.id.applyTextView);
                if (Connection.minAge != 16)
                    minAge.setText("" + Connection.minAge);
                if (Connection.maxAge != 100)
                    maxAge.setText("" + Connection.maxAge);
                if (Connection.genders[0].equals("")) {
                    male.setTextAppearance(R.style.genderUnselected);
                    male.setBackgroundResource(R.drawable.bg_gender_filter_unselected);
                }
                if (Connection.genders[1].equals("")) {
                    female.setTextAppearance(R.style.genderUnselected);
                    female.setBackgroundResource(R.drawable.bg_gender_filter_unselected);
                }
                if (Connection.genders[2].equals("")) {
                    other.setTextAppearance(R.style.genderUnselected);
                    other.setBackgroundResource(R.drawable.bg_gender_filter_unselected);
                }

                //Gender
                male.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Connection.genders[0].equals("")) {
                            male.setTextAppearance(R.style.genderSelected);
                            male.setBackgroundResource(R.drawable.bg_gender_filter_selected);
                            Connection.genders[0] = "male";
                        } else {
                            male.setTextAppearance(R.style.genderUnselected);
                            male.setBackgroundResource(R.drawable.bg_gender_filter_unselected);
                            Connection.genders[0] = "";
                        }
                    }
                });
                female.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Connection.genders[1].equals("")) {
                            female.setTextAppearance(R.style.genderSelected);
                            female.setBackgroundResource(R.drawable.bg_gender_filter_selected);
                            Connection.genders[1] = "female";
                        } else {
                            female.setTextAppearance(R.style.genderUnselected);
                            female.setBackgroundResource(R.drawable.bg_gender_filter_unselected);
                            Connection.genders[1] = "";
                        }
                    }
                });
                other.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Connection.genders[2].equals("")) {
                            other.setTextAppearance(R.style.genderSelected);
                            other.setBackgroundResource(R.drawable.bg_gender_filter_selected);
                            Connection.genders[2] = "other";
                        } else {
                            other.setTextAppearance(R.style.genderUnselected);
                            other.setBackgroundResource(R.drawable.bg_gender_filter_unselected);
                            Connection.genders[2] = "";
                        }
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                applyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Connection.minAge = Integer.parseInt(minAge.getText().toString().equals("") ? minAge.getHint().toString() : minAge.getText().toString());
                        Connection.maxAge = Integer.parseInt(maxAge.getText().toString().equals("") ? maxAge.getHint().toString() : maxAge.getText().toString());
                        alertDialog.dismiss();
                        Fragment fragment = new MapFragment().newInstance(mapFragment.connectionController, mapFragment.database, mapFragment.drawController);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.home_fragment, fragment).commit();
                    }
                });
                break;
            case R.id.randomIcon:
                int number = (int) (Math.random() * mapFragment.userList.size());
                BottomSheetNewChat bottomSheet = new BottomSheetNewChat(mapFragment.userList.get(number), true);
                if (getContext() != null) {
                    bottomSheet.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "ModalBottomSheet");
                } else {
                    Toast.makeText(getActivity(), "Unable to start a random chat, try again", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backMapButton:
                if (Connection.page > 0) {
                    Connection.page--;
                    backButton.setClickable(true);
                    backButton.setAlpha(1f);
                } else {
                    backButton.setClickable(false);
                    backButton.setAlpha(0.5f);
                }
            case R.id.nextMapButton:
                if (mapFragment.userList.size() - (Connection.page * 25) > 0) {
                    Connection.page++;
                    nextButton.setClickable(true);
                    nextButton.setAlpha(1f);
                } else {
                    nextButton.setClickable(false);
                    nextButton.setAlpha(0.5f);
                }
            default:
                graphicRefresh();
                break;
        }
    }

    private void manageScanAgain(MenuItem menuItem){
        mapFragment.connectionController.reScan();
        if(countDownTimer == null){
            countDownTimer = new CountDownTimer(30000, 1000) {
                @Override
                public void onTick(long l) {
                    mapFragment.waitToScan = (int)l/1000;
                }
                @Override
                public void onFinish() {
                    mapFragment.waitToScan = 0;
                }
            }.start();

        }else{
            countDownTimer.start();
        }
    }
}

