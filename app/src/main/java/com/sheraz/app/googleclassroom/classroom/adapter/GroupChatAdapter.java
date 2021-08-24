package com.sheraz.app.googleclassroom.classroom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.sheraz.app.googleclassroom.R;
import com.sheraz.app.googleclassroom.classroom.model.Messages;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.GroupChatViewHolder> {

    List<Messages> messagesList;
    Context context;
    FirebaseAuth mAuth;

    public GroupChatAdapter(List<Messages> messagesList, Context context) {
        this.messagesList = messagesList;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public GroupChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_chat_layout, null);
        return new GroupChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatViewHolder holder, int position) {
        holder.groupChatTextMessage.setText(messagesList.get(position).getMessage());
        if(messagesList.get(position).getMessage().equals("Photo")){
            holder.groupChatTextMessage.setVisibility(View.GONE);
            holder.img.setVisibility(View.VISIBLE);
            Glide.with(context).load(messagesList.get(position).getImgURL()).into(holder.img);
        }
        holder.groupChatTextMessage.setText(messagesList.get(position).getMessage());
        holder.sentBy.setText(messagesList.get(position).getPhoneNumber());
        holder.timeStamp.setText("" + messagesList.get(position).getTimeStamp());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class GroupChatViewHolder extends RecyclerView.ViewHolder {

        TextView groupChatTextMessage;
        TextView sentBy;
        TextView timeStamp;
        ImageView img;
        public GroupChatViewHolder(@NonNull View itemView) {
            super(itemView);
            groupChatTextMessage = itemView.findViewById(R.id.groupChatTextMessage);
            sentBy = itemView.findViewById(R.id.sentBy);
            timeStamp = itemView.findViewById(R.id.sentMessageTime);
            img = itemView.findViewById(R.id.chatLayout_img);
        }
    }
}
