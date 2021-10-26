package com.example.connection.View;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.connection.Controller.AccountController;
import com.example.connection.Controller.DrawController;
import com.example.connection.Database.Database;
import com.example.connection.R;

public class XiaomiPermissionFragment extends Fragment {

    private Connection connection;
    private XiaomiPermissionFragment xiaomiPermissionFragment;
    private Database database;
    private AccountController accountController;
    private DrawController drawController;

    public XiaomiPermissionFragment(){
    }

    public XiaomiPermissionFragment newInstance(Connection connection, Database database, AccountController accountController, DrawController drawController) {
        xiaomiPermissionFragment.setConnection(connection);
        xiaomiPermissionFragment.setDatabase(database);
        xiaomiPermissionFragment.setAccountController(accountController);
        xiaomiPermissionFragment.setDrawController(drawController);
        return xiaomiPermissionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_xiaomi_permission,null);
        Button openPermission = view.findViewById(R.id.ButtonOpenAppPermission);
        openPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsTabActivity"));
                startActivity(intent1);
            }
        });
        CheckBox checkBox = view.findViewById(R.id.checkBox);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    sharedPreferences.edit().putBoolean("DoNotShowXiaomiPermissionFragment",true).apply();
                }
                else{
                    sharedPreferences.edit().putBoolean("DoNotShowXiaomiPermissionFragment",false).apply();
                }
            }
        });
        ImageView next = view.findViewById(R.id.imageView50);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = connection.getSupportFragmentManager().beginTransaction();
                if (connection.firstLogin()) {
                    transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    transaction.replace(R.id.main_fragment, new LoginFragment().newInstance(connection,database,accountController,drawController));
                    transaction.commit();
                } else {
                    transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    transaction.replace(R.id.main_fragment, new HomeFragment().newInstance(connection,database,drawController));
                    transaction.commit();
                }
            }
        });
        return view;
    }

    private void setConnection(Connection connectiona){
        this.connection = connection;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setAccountController(AccountController accountController) {
        this.accountController = accountController;
    }

    public void setDrawController(DrawController drawController) {
        this.drawController = drawController;
    }
}
