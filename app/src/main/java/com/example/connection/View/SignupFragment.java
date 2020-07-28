package com.example.connection.View;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.connection.Adapter.SliderAdapter;
import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.R;

public class SignupFragment extends Fragment {

    public SignupFragment newInstance() {
        SignupFragment signupFragment=new SignupFragment();
        return signupFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.signup_fragment, null);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        ViewPager viewPager=view.findViewById(R.id.viewPager);
        SliderAdapter sliderAdapter = new SliderAdapter(this.getContext(), progressBar);
        viewPager.setAdapter(sliderAdapter);
        return view;
    }
}
