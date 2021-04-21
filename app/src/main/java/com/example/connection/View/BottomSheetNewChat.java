package com.example.connection.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.connection.Model.User;
import com.example.connection.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetNewChat extends BottomSheetDialogFragment {

    private User user;

    public BottomSheetNewChat(String username, String birth,String gender,String profilePic){
        this.user =  new User();
        user.setUsername(username);
        user.setBirth(birth);
        user.setGender(gender);
        user.setProfilePic(profilePic);
    }

    public BottomSheetNewChat(User user){
        this.user = user;
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

        CircleImageView profileImage = view.findViewById(R.id.profilePicMapAlertDialog2);
        profileImage.setImageBitmap(BitmapFactory.decodeFile(user.getProfilePic()));
        TextView name = view.findViewById(R.id.nameMapAlertDialog2),information = view.findViewById(R.id.informationMapAlertDialog2);
        name.setText(user.getUsername());
        String info =user.getGender()+" "+user.getAge();
        information.setText(info);
        TextView send = view.findViewById(R.id.sendMessageTextView),cancel = view.findViewById(R.id.cancelTextView);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), ChatActivity.class);
                myIntent.putExtra("idChat", user.getIdUser());
                myIntent.putExtra("name", user.getName());
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
