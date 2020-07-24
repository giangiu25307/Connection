package com.example.connection.View;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Adapter.ChatAdapter;
import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Model.Chats;
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
    ChatController chatController;
    TextView totalChat;
    private long secondsRemaining = 1500;
    private CountDownTimer countDownTimer;
    private Boolean startTimer = false,startTimer2 = true;

    public ChatFragment() {
    }

    public ChatFragment newInstance(Database database,ChatController chatController) {
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setDatabase(database);
        chatFragment.setChatController(chatController);
        return chatFragment;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.chat_fragment, null);

        /*
        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("appTheme", "light").equals("light")){
            textColor = Color.BLACK;
        }else{
            textColor = Color.WHITE;
        }

        globalButton = view.findViewById(R.id.globalButton);
        globalButton.setOnClickListener(this);


        TextView textView = view.findViewById(R.id.requestTextView);
        textView.setText("1");
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right);
        textView.startAnimation(animation);
        */
        linearLayout = view.findViewById(R.id.requestLinearLayout);
        requestTextView = view.findViewById(R.id.requestTextView2);

        totalChat = view.findViewById(R.id.totalChat);
        int totalChatNumber = database.getAllChat().getCount();
        totalChat.setText(totalChatNumber == 0 ? "Chat" : "Chat (" + totalChatNumber + ")");
        final ViewTreeObserver viewTreeObserver = requestTextView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    currentWidth = requestTextView.getWidth();
                    currentHeight = requestTextView.getHeight();
                    if (currentWidth != 0 && currentHeight != 0) {
                        createCountDowntimer();
                        countDownTimer.start();
                        requestTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
        setupRecyclerView(view);
        return view;
    }

    private void createCountDowntimer() {
        countDownTimer = new CountDownTimer(secondsRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                animRequestButton(currentWidth, currentHeight);
                startTimer = false;
            }
        };
    }

    private void setupRecyclerView(View view){
        RecyclerView recyclerView = view.findViewById(R.id.chatRecyclerView);
        System.out.println(database);
        ChatAdapter chatAdapter = new ChatAdapter(getContext(), database.getAllChat(), database, chatController);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (startTimer&&startTimer2) {
            createCountDowntimer();
            countDownTimer.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        countDownTimer.cancel();
        startTimer2 = false;
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
                currentWidth=val;
            }
        });
        anim.setDuration(1750);
        anim.start();

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
