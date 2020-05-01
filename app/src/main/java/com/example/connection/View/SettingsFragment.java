package com.example.connection.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.example.connection.R;

public class SettingsFragment extends Fragment {

    private ConstraintLayout themeLayout;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.settings_fragment, null);

        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        themeLayout = view.findViewById(R.id.themeLayout);
        themeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("App Theme");
                alertDialog.setMessage("Select app theme");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Dark",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                changetheme("dark", alertDialog);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Light",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                changetheme("light", alertDialog);
                            }
                        });
                alertDialog.show();
            }
        });

        return view;
    }

    private void changetheme(String theme, AlertDialog alertDialog){

        sharedPreferences.edit().putString("appTheme", theme).apply();
        alertDialog.dismiss();
        getActivity().recreate();

    }

}
