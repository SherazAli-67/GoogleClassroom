package com.sheraz.app.googleclassroom.classroom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.sheraz.app.googleclassroom.R;
import com.sheraz.app.googleclassroom.classroom.model.Messages;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter{
    List<Messages> messagesList;
    Context context;

    public static final int SENT_MESSAGE =1;
    public static final int RECEIVER_MESSAGE =2;

    public MessageAdapter(List<Messages> messagesList, Context context) {
        this.messagesList = messagesList;
        this.context = context;
//        Toast.makeText(context, "size: "+messagesList.size(), Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENT_MESSAGE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_message_layout,parent,false);
            return new SenderViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiving_message_layout,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messages = messagesList.get(position);
        if(holder.getClass() == SenderViewHolder.class){
            SenderViewHolder viewHolder = (SenderViewHolder)holder;
            viewHolder.senderMessage.setText(messages.getMessage());
        }else{
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.receiverMessage.setText(messages.getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        String senderID = FirebaseAuth.getInstance().getUid();
        if(messagesList.get(position).getSender_id().equals(senderID)){
            return SENT_MESSAGE;
        }else
            return RECEIVER_MESSAGE;
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView senderMessage;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.sender_txtMessage);
        }
    }

    public  class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView receiverMessage;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMessage = itemView.findViewById(R.id.receiver_txtMessage);
        }
    }
}
