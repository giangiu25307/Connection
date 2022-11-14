package com.ConnectionProject.connection.View;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ConnectionProject.connection.Model.User;
import com.ConnectionProject.connection.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetNewChat extends BottomSheetDialogFragment {

    private User user;
    private boolean randomChat;

    public BottomSheetNewChat(User user, boolean randomChat){
        this.user = user;
        this.randomChat = randomChat;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.customBottomSheet);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.lyt_bs_new_chat, container, false);

        if(randomChat){
            TextView bottomSheetTitle = view.findViewById(R.id.bottomSheetTitle);
            bottomSheetTitle.setText("New random chat");
        }
        CircleImageView profileImage = view.findViewById(R.id.profilePicMapAlertDialog2);
        profileImage.setImageDrawable(Drawable.createFromPath(user.getProfilePic()));
        TextView name = view.findViewById(R.id.nameMapAlertDialog2),information = view.findViewById(R.id.informationMapAlertDialog2);
        name.setText(user.getUsername());
        String info = user.getGender() + " " + user.getAge();
        information.setText(info);
        Button send = view.findViewById(R.id.sendMessageButton), cancel = view.findViewById(R.id.cancelButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), ChatActivity.class);
                myIntent.putExtra("idChat", user.getIdUser());
                myIntent.putExtra("username", user.getUsername());
                getContext().startActivity(myIntent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
