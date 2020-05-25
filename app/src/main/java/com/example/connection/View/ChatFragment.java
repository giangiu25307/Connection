package com.example.connection.View;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Adapter.ChatAdapter;
import com.example.connection.Controller.Database;
import com.example.connection.R;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private ImageView globalButton;
    private int textColor;
    private SharedPreferences sharedPreferences;
    Database database;
    LinearLayout linearLayout;
    TextView requestTextView;
    int currentWidth;
    int currentHeight;

    public ChatFragment(Database database) {
        this.database = database;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.chat_fragment, null);

        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("appTheme", "light").equals("light")){
            textColor = Color.BLACK;
        }else{
            textColor = Color.WHITE;
        }

        globalButton = view.findViewById(R.id.globalButton);
        globalButton.setOnClickListener(this);

        /*
        TextView textView = view.findViewById(R.id.requestTextView);
        textView.setText("1");
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right);
        textView.startAnimation(animation);
        */
        linearLayout = view.findViewById(R.id.requestLinearLayout);
        requestTextView = view.findViewById(R.id.requestTextView2);

        final ViewTreeObserver viewTreeObserver = requestTextView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    currentWidth = requestTextView.getWidth();
                    currentHeight = requestTextView.getHeight();
                    if (currentWidth != 0 && currentHeight != 0) {
                        animRequestButton(currentWidth, currentHeight);
                        requestTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view){
        RecyclerView recyclerView = view.findViewById(R.id.chatRecyclerView);
        ChatAdapter chatAdapter = new ChatAdapter(getContext(), database.getAllChat(), database);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private Cursor getAllChat(){

        return null;
    }

    private void animRequestButton(int startWidth, final int currentHeight){
        //int startWidth2 = requestTextView.getWidth();
        //System.out.println("Width value: " + startWidth2);
        /*
        view.getLayoutParams().height = (int) (startWidth + endWidth * interpolatedTime);
        view.requestLayout();
        textView.startAnimation();
        TextAnimation a = new TextAnimation(textView);
        a.setDuration(3000);
        a.setParams(startWidth, 0);
        textView.startAnimation(a);
        */


        ValueAnimator anim = ValueAnimator.ofInt(startWidth, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = requestTextView.getLayoutParams();
                layoutParams.width = val;
                requestTextView.setLayoutParams(layoutParams);
                requestTextView.setHeight(currentHeight);
            }
        });
        anim.setDuration(1750);


        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f);
        valueAnimator.setDuration(1750);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                requestTextView.setAlpha(alpha);
            }
        });

        anim.start();
        valueAnimator.start();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.globalButton:
                //changeView(2);
                break;
            default:
                break;
        }
    }
}
