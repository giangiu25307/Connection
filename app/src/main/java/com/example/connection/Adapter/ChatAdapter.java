package com.example.connection.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Model.Chat;
import com.example.connection.Model.User;
import com.example.connection.Controller.ChatController;
import com.example.connection.Database.Database;
import com.example.connection.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    public ArrayList<Chat> chatsList;
    private User user;
    private Database database;
    private Bitmap bitmap, bitmap2;
    private int position;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    Date date = new Date();
    ChatController chatController;
    public ArrayList<String> selectedChat = new ArrayList<>();

    public ChatAdapter(Context context, ArrayList<Chat> chatList, Database database, ChatController chatController) {
        this.context = context;
        this.chatsList = chatList;
        this.database = database;
        this.chatController = chatController;
        //Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(chatCursor));
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.lyt_chat, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {

        if (chatsList.get(position) == null) {
            return;
        }
        System.out.println("Position " + position);

        Chat chat = chatsList.get(position);

        String id = chat.getId();
        if (selectedChat.contains(id)) {
            holder.chatLayout.setBackground(context.getDrawable(R.drawable.bg_chat_card_selection));
        } else {
            holder.chatLayout.setBackground(context.getDrawable(R.drawable.bg_chat_card));
        }

        holder.name.setText(chat.getName());
        String lastMessage = chat.getLastMessage();
        System.out.println("Id chat: " + id + " , name: " + chat.getName());
        if (lastMessage != null) {
            System.out.println("true");
            holder.lastMessage.setText(lastMessage);
            holder.lastMessage.setTypeface(null, Typeface.NORMAL);
        }else{
            System.out.println("false");
            holder.lastMessage.setText("No message");
            holder.lastMessage.setTypeface(null, Typeface.ITALIC);
        }

        String datetime = chat.getDateTime();
        if (datetime != null) {
            try {
                date = format.parse(datetime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date date2 = null;
            try {
                date2 = format.parse(String.valueOf(LocalDateTime.now()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (DateFormat.format("dd", date).equals(DateFormat.format("dd", date2)) && DateFormat.format("MM", date).equals(DateFormat.format("MM", date2)) && DateFormat.format("yyyy", date).equals(DateFormat.format("yyyy", date2))) {
                datetime = String.valueOf(DateFormat.format("HH:mm", date));
            } else {
                String year = String.valueOf(DateFormat.format("yyyy", date));
                datetime = DateFormat.format("dd/MM", date) + "/" + year.substring(year.length() - 2);
            }
            holder.timeLastMessage.setText(datetime);
        } else {
            holder.timeLastMessage.setText("");
        }


        holder.unreadMessage.setText(database.countMessageNotRead(id));
        if(holder.unreadMessage.getText().equals("0"))
            holder.unreadMessage.setVisibility(View.INVISIBLE);
        else
            holder.unreadMessage.setVisibility(View.VISIBLE);
        File profilePic = new File(database.getUser(id).getProfilePic());
        if(profilePic.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(profilePic.getAbsolutePath());
            holder.profilePic.setImageBitmap(myBitmap);
        }
        holder.itemView.setTag(id);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void swapCursor(Cursor newChatsList) {
        newChatsList.moveToFirst();
        chatsList.clear();
        do {
            chatsList.add(new Chat(newChatsList.getString(0), newChatsList.getString(1), newChatsList.getString(2), newChatsList.getString(3)));
        } while (newChatsList.moveToNext());

        if (chatsList != null) {
            notifyDataSetChanged();
        }

    }

    public void removeChat(String id) {
        Iterator<Chat> iterator = chatsList.iterator();

        while (iterator.hasNext()) {
            Chat chat = iterator.next();
            if (chat.getId().equals(id)) {
                iterator.remove();
                int position = chatsList.indexOf(chat);
                chatsList.remove(chat);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, chatsList.size());
            }
        }
    }

    public void refreshChat(String id, String lastMessage, String dateTime) {
        Iterator<Chat> iterator = chatsList.iterator();

        while (iterator.hasNext()) {
            Chat chat = iterator.next();
            if (chat.getId().equals(id)) {
                int position = chatsList.indexOf(chat);
                chat.setLastMessage(lastMessage);
                chat.setDateTime(dateTime);
                iterator.remove();
                chatsList.remove(position);
                chatsList.add(0, chat);
                notifyItemRangeChanged(0, chatsList.size());
                notifyItemChanged(0);
                return;
            }
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private ConstraintLayout chatLayout;
        private final ImageView profilePic;
        private final TextView name, lastMessage, timeLastMessage, unreadMessage;

        private ViewHolder(View itemView) {
            super(itemView);

            chatLayout = itemView.findViewById(R.id.chatLayout);
            profilePic = itemView.findViewById(R.id.profilePhoto);
            name = itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            timeLastMessage = itemView.findViewById(R.id.timeLastMessage);
            unreadMessage = itemView.findViewById(R.id.textViewUnreadMessage);
        }

    }


}