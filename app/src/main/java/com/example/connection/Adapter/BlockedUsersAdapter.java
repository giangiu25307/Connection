package com.example.connection.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Database.Database;
import com.example.connection.Model.User;
import com.example.connection.R;

import java.util.ArrayList;

public class BlockedUsersAdapter extends RecyclerView.Adapter<BlockedUsersAdapter.ViewHolder>{

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<User> blockedUsersList;
    private Database database;
    private ImageView noBlockedUsersImageView;
    private TextView noBlockedUsersTextView;

    public BlockedUsersAdapter(Context context, RecyclerView recyclerView, ArrayList<User> blockedUsersList, Database database, ImageView noBlockedUsersImageView, TextView noBlockedUsersTextView){
        this.context = context;
        this.recyclerView = recyclerView;
        this.blockedUsersList = blockedUsersList;
        this.database = database;
        this.noBlockedUsersImageView = noBlockedUsersImageView;
        this.noBlockedUsersTextView = noBlockedUsersTextView;
    }

    @NonNull
    @Override
    public BlockedUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.lyt_blocked_user, parent, false);
        BlockedUsersAdapter.ViewHolder holder = new BlockedUsersAdapter.ViewHolder(view, new BlockedUsersAdapter.ViewHolder.OnUnblockClickListener() {

            @Override
            public void unblockUser(int position) {
                User user = blockedUsersList.get(position);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.dialog_confirm_unblock_user);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                TextView unblockUserSubtitle = alertDialog.findViewById(R.id.unblockUserDialogSubtitle);
                Button cancel, confirm;
                cancel = alertDialog.findViewById(R.id.cancelButton);
                confirm = alertDialog.findViewById(R.id.confirmButton);

                String subtitle = "Are you sure you want to unblock " + user.getUsername() + "?";
                unblockUserSubtitle.setText(subtitle);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        database.unblockUser(user.getIdUser());
                        removeUser(position);
                        alertDialog.dismiss();
                    }
                });

            }

        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BlockedUsersAdapter.ViewHolder holder, int position) {
        if (blockedUsersList.get(position) == null) {
            return;
        }

        User user = blockedUsersList.get(position);

        String information = "";
        if(user.getGender() != null && user.getBirth() != null){
            information = user.getUsername() + ", " + user.getAge();
        }else{
            information = user.getUsername() != null ? user.getUsername() : user.getAge();
        }
        holder.username.setText(user.getUsername());
        holder.information.setText(information);
    }

    @Override
    public int getItemCount() {
        return blockedUsersList.size();
    }

    private void removeUser(int position){
        blockedUsersList.remove(position);
        notifyItemRemoved(position);
        if(blockedUsersList.size() == 0){
            if (recyclerView != null) {
                recyclerView.setVisibility(View.INVISIBLE);
            }
            if (noBlockedUsersImageView != null) {
                noBlockedUsersImageView.setVisibility(View.VISIBLE);
            }
            if (noBlockedUsersTextView != null) {
                noBlockedUsersTextView.setVisibility(View.VISIBLE);
            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        BlockedUsersAdapter.ViewHolder.OnUnblockClickListener listener;

        private ImageView profilePic;
        private TextView username, information;
        private Button unblockButton;

        private ViewHolder(View itemView, BlockedUsersAdapter.ViewHolder.OnUnblockClickListener listener) {
            super(itemView);
            this.listener = listener;
            profilePic = itemView.findViewById(R.id.profilePic);
            username = itemView.findViewById(R.id.userNameTextView);
            information = itemView.findViewById(R.id.informationTextView);
            unblockButton = itemView.findViewById(R.id.unblockButton);
            unblockButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.unblockButton:
                    listener.unblockUser(this.getLayoutPosition());
                    break;
                default:
                    break;
            }
        }

        public interface OnUnblockClickListener {
            void unblockUser(int position);
        }

    }
}
