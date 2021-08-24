package com.sheraz.app.googleclassroom.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sheraz.app.googleclassroom.R;
import com.sheraz.app.googleclassroom.chat.ChatActivity;
import com.sheraz.app.googleclassroom.classroom.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>
{
    List<User> users;
    Context context;

    public ChatAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
        Toast.makeText(context, "Size: "+users.size(), Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout,null);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        User user = users.get(position);
        holder.user_title.setText(user.getName());
        Glide.with(context).load(user.getImg()).into(holder.userImg);
        holder.lastMessage.setText("Last Message  . . .");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoChatActivity = new Intent(context,ChatActivity.class);

                gotoChatActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                gotoChatActivity.putExtra("name",user.getName());
                gotoChatActivity.putExtra("uid",user.getUser_id());
                context.startActivity(gotoChatActivity);
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        CircleImageView userImg;
        TextView user_title;
        TextView lastMessage;
        TextView timeStamp;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.chatLayout_userImg);
            user_title = itemView.findViewById(R.id.chatLayout_userName);
            lastMessage = itemView.findViewById(R.id.chatLayout_lastMessage);
            timeStamp = itemView.findViewById(R.id.timeStamp);
        }
    }
}
