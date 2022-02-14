package com.example.connection.View;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.example.connection.Controller.MessageController;
import com.example.connection.Database.Database;
import com.example.connection.Model.User;
import com.example.connection.R;
import com.example.connection.View.Layout.FlowLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MapFragment extends Fragment {

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
        parent = view.findViewById(R.id.mapFlowLayout);
        userList = database.getAllFilteredUsers();
        sharedPreferences = getContext().getSharedPreferences("utils", Context.MODE_PRIVATE);
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                drawController.init(getContext(), parent, parent.getHeight(), parent.getWidth(), userList);
            }
        });

        return view;
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

                final Button male = alertDialog.findViewById(R.id.male), female = alertDialog.findViewById(R.id.female), other = alertDialog.findViewById(R.id.other), cancelButton, applyButton;
                final EditText minAge = alertDialog.findViewById(R.id.editTextMinAge), maxAge = alertDialog.findViewById(R.id.editTextMaxAge);
                cancelButton = alertDialog.findViewById(R.id.cancelTextView);
                applyButton = alertDialog.findViewById(R.id.applyTextView);

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
                        Connection.minAge = Integer.parseInt(minAge.getText().toString());
                        Connection.maxAge = Integer.parseInt(maxAge.getText().toString());
                        alertDialog.dismiss();
                        Fragment fragment = new MapFragment().newInstance(connectionController, database, drawController);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.home_fragment, fragment).commit();
                    }
                });
                break;
            case R.id.randomIcon:
                int number = (int) (Math.random() * userList.size());
                BottomSheetNewChat bottomSheet = new BottomSheetNewChat(userList.get(number), true);
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
    }
}

