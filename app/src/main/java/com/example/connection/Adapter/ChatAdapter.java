package com.example.connection.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.example.connection.Model.LastMessage;
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
    public ArrayList<String> selectedUsersList = new ArrayList<>();

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

        return new ViewHolder(view, new ViewHolder.OnChatClickListener() {

            @Override
            public void openChat(int p) {
                /*chatCursor.moveToPosition(p);
                final String id = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.ID_CHAT));
                Intent myIntent = new Intent(context, ChatActivity.class);
                // myIntent.putExtra("chatController", chatController); //Optional parameters\
                myIntent.putExtra("idChat", id);
                //TODO Da cambiare in username
                myIntent.putExtra("username", chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.NAME)));
                context.startActivity(myIntent);*/
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {

        if (chatsList.get(position) == null) {
            return;
        }

        Chat chat = chatsList.get(position);

        String id = chat.getId();
        if(selectedUsersList.contains(id)){
            holder.chatLayout.setBackground(context.getDrawable(R.drawable.bg_chat_card_selection));
        }else{
            holder.chatLayout.setBackground(context.getDrawable(R.drawable.bg_chat_card));
        }

        holder.name.setText(chat.getName());
        String lastMessage = chat.getLastMessage();
        holder.lastMessage.setText(lastMessage);

        String datetime = chat.getDateTime();
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
        }else{
            String year = String.valueOf(DateFormat.format("yyyy", date));
            datetime = DateFormat.format("dd/MM", date) + "/" + year.substring(year.length() - 2);
        }
        holder.timeLastMessage.setText(datetime);

        //TODO Implementere messaggi non letti
        File profilePic = new File(database.getUser(id).getProfilePic());
        //TODO Settare l'immagine

        holder.itemView.setTag(id);

        /*

        if (profilePic.exists()) {

            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circle);
            bitmap2 = BitmapFactory.decodeFile(profilePic.getAbsolutePath());
            BitmapDrawable layer1 = new BitmapDrawable(context.getResources(), bitmap2);
            BitmapDrawable layer2 = new BitmapDrawable(context.getResources(), bitmap);
            Drawable[] layers = {layer1, layer2};
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            ImageView profilePicImageView = holder.profilePic;
            profilePicImageView.setImageTintList(null);
            profilePicImageView.setImageDrawable(layerDrawable);
            //bitmap = BitmapFactory.decodeFile(profilePic.getAbsolutePath());

        }

        */

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
        while(newChatsList.moveToNext()){
            chatsList.add(new Chat(newChatsList.getString(0), newChatsList.getString(1), newChatsList.getString(2), newChatsList.getString(3)));
        }

        if (chatsList != null) {
            notifyDataSetChanged();
        }

    }

    public void removeChat(String id){
        Iterator<Chat> iterator = chatsList.iterator();

        while (iterator.hasNext()) {
            Chat chat = iterator.next();
            if(chat.getId().equals(id)){
                iterator.remove();
                int position = chatsList.indexOf(chat);
                chatsList.remove(chat);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, chatsList.size());
            }

        }
        /*for (Chat chat: chatsList){
            if(chat.getId().equals(id)){
                int position = chatsList.indexOf(chat);
                chatsList.remove(chat);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, chatsList.size());
            }
        }*/
    }

    public void refreshChat(String id, String lastMessage, String dateTime){
        Iterator<Chat> iterator = chatsList.iterator();

        while (iterator.hasNext()) {
            Chat chat = iterator.next();
            if(chat.getId().equals(id)){
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnChatClickListener listener;

        private ConstraintLayout chatLayout;
        private final ImageView profilePic;
        private final TextView name, lastMessage, timeLastMessage;

        private ViewHolder(View itemView, OnChatClickListener listener) {
            super(itemView);
            this.listener = listener;

            chatLayout = itemView.findViewById(R.id.chatLayout);
            chatLayout.setOnClickListener(this);
            profilePic = itemView.findViewById(R.id.profilePhoto);
            name = itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            timeLastMessage = itemView.findViewById(R.id.timeLastMessage);

        }

        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.chatLayout) {
                listener.openChat(this.getLayoutPosition());
            }

        }

        public interface OnChatClickListener {
            void openChat(int p);
        }

    }


}