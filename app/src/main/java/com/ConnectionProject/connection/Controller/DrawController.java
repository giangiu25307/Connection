package com.ConnectionProject.connection.Controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.ConnectionProject.connection.Database.Database;
import com.ConnectionProject.connection.Model.User;
import com.ConnectionProject.connection.R;
import com.ConnectionProject.connection.View.BottomSheetNewChat;
import com.ConnectionProject.connection.View.Connection;
import com.ConnectionProject.connection.View.Layout.FlowLayout;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class DrawController {

    private int layoutWidth;
    private int layoutHeight;
    private Context context;
    private Database database;

    public DrawController(Database database) {
        this.database = database;
    }

    /**
     *  Create the layout of the map to insert the user i will connect to
     *
     * @param context      context of the application
     * @param parent       the layout in which i'm contained
     * @param layoutHeight height of my smartphone
     * @param layoutWidth  width of my smartphone
     * @param userList     users to shown in the map
     */
    public void init(Context context, FlowLayout parent, int layoutHeight, int layoutWidth, ArrayList<User> userList) {
        parent.removeAllViews();
        this.context = context;
        this.layoutHeight = layoutHeight;
        this.layoutWidth = layoutWidth;
        addViewToLayout(parent, userList);
    }

    /**
     *  Add view to the layout
     *
     * @param parent       the layout in which i'm contained
     * @param userList     users to shown in the map
     */
    private void addViewToLayout(FlowLayout parent, ArrayList<User> userList) {
        for (int i = (Connection.page * 25), j = 0; i < userList.size() - (Connection.page * 25) && j < 25; i++, j++) {
            User user = userList.get(i);
            parent.addView(createLayout(user));
        }
    }

    /**
     *  Create the layout of the cell of the map
     *
     * @param user    user to shown in the layout
     */
    private LinearLayout createLayout(User user) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setTag(user.getIdUser());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        CircleImageView imageView = new CircleImageView(context);
        imageView.setPadding(25, 0, 25, 0);
        imageView.setImageDrawable(Drawable.createFromPath(user.getProfilePic()));
        TextView textView = new TextView(context);
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f);
        textView.setText(user.getUsername());
        textView.measure(0, 0);
        textView.setGravity(Gravity.CENTER);
        tableLayoutParams.setMargins(0, -35, 0,0 );
        textView.setLayoutParams(tableLayoutParams);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(layoutWidth / 5 - 2, layoutHeight / 5 - 2 - textView.getMeasuredHeight() - 5));

        textView.setTextColor(context.getTheme().obtainStyledAttributes(R.styleable.themeAttrs).getColor(R.styleable.themeAttrs_textColor, Color.WHITE));

        linearLayout.setBackgroundResource(R.drawable.bg_map_user);
        linearLayout.addView(imageView);
        linearLayout.addView(textView);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetNewChat bottomSheet = new BottomSheetNewChat(user, false);
                bottomSheet.show(((AppCompatActivity)context).getSupportFragmentManager(), "ModalBottomSheet");
            }
        });
        return linearLayout;
    }

}
