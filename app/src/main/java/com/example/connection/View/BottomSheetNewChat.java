package com.example.connection.View;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.connection.Model.User;
import com.example.connection.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetNewChat extends BottomSheetDialogFragment {

    private User user;

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

        return view;
    }
}
