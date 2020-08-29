package com.example.connection.View;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Adapter.ChatAdapter;
import com.example.connection.Adapter.RequestAdapter;
import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.Database;
import com.example.connection.R;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private ImageView globalButton;
    private int textColor;
    private SharedPreferences sharedPreferences;
    private Database database;
    private LinearLayout linearLayout;
    private TextView requestTextView2,numberRequest;
    private int currentWidth;
    private int currentHeight;
    private ChatController chatController;
    private TextView totalChat;
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
        requestTextView2 = view.findViewById(R.id.requestTextView2);
        numberRequest = view.findViewById(R.id.numberRequest);
        int totalRequest = database.getAllRequestChat().getCount();
        numberRequest.setText(String.valueOf(totalRequest));//(totalRequest==0 ? "No" : ""+totalRequest);
        totalChat = view.findViewById(R.id.totalChat);
        int totalChatNumber = database.getAllNoRequestChat().getCount();
        totalChat.setText(totalChatNumber == 0 ? "Chat (0)" : "Chat (" + totalChatNumber + ")");
        final ViewTreeObserver viewTreeObserver = requestTextView2.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    currentWidth = requestTextView2.getWidth();
                    currentHeight = requestTextView2.getHeight();
                    if (currentWidth != 0 && currentHeight != 0) {
                        createCountDowntimer();
                        countDownTimer.start();
                        requestTextView2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                crossToRequestDialog(dialogBuilder);
            }
        });
        setupRecyclerView(view);
        return view;
    }

    private void createCountDowntimer() {
        countDownTimer = new CountDownTimer(secondsRemaining, 1500) {
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
        Cursor cursor = database.getAllNoRequestChat();
        ChatAdapter chatAdapter = new ChatAdapter(getContext(), cursor, database, chatController);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if(cursor.getCount() == 0){
            TextView textView = view.findViewById(R.id.textView0Chat);
            textView.setVisibility(View.VISIBLE);
        }
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
                ViewGroup.LayoutParams layoutParams = requestTextView2.getLayoutParams();
                layoutParams.width = val;
                requestTextView2.setLayoutParams(layoutParams);
                requestTextView2.setHeight(currentHeight);
                currentWidth=val;
            }
        });
        anim.setDuration(2000);
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

    private void crossToRequestDialog(AlertDialog.Builder dialogBuilder){
        dialogBuilder.setView(R.layout.request_alert_dialog);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        setupRequestRecyclerView(alertDialog);

    }

    private void setupRequestRecyclerView(AlertDialog view){
        RecyclerView recyclerView = view.findViewById(R.id.requestRecycleView);
        //System.out.println(database);
        Cursor cursor = database.getAllRequestChat();
        RequestAdapter requestAdapter = new RequestAdapter(getContext(), cursor, database, chatController, numberRequest);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(requestAdapter);
        /*if(cursor.getCount() == 0){                                    //THERE IS NOTHING HERE
            TextView textView = view.findViewById(R.id.textView0Chat);
            textView.setVisibility(View.VISIBLE);
        }*/
    }
}
