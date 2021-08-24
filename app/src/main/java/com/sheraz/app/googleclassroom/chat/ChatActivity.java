package com.sheraz.app.googleclassroom.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sheraz.app.googleclassroom.R;
import com.sheraz.app.googleclassroom.classroom.adapter.MessageAdapter;
import com.sheraz.app.googleclassroom.classroom.model.Messages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    RecyclerView message_recyclerView;
    EditText typeMessage;
    ImageButton sendMessage;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;

    String senderRoom, receiverRoom;
    Bundle bundle;

    MessageAdapter adapter;
    List<Messages> messagesList;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesList = new ArrayList<>();
        bundle = getIntent().getExtras();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        String senderUid = mAuth.getCurrentUser().getUid();
        String name = bundle.getString("name");
        String receiverUid = bundle.getString("uid");

        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        message_recyclerView = findViewById(R.id.messages_recyclerView);
        message_recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        message_recyclerView.setHasFixedSize(true);

        typeMessage = findViewById(R.id.typeMessage);
        sendMessage = findViewById(R.id.sendMessage);

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        firebaseDatabase.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesList.clear();
                        if (snapshot != null) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Messages messages = snapshot1.getValue(Messages.class);
                                messagesList.add(messages);
                            }
                            adapter = new MessageAdapter(messagesList, getApplicationContext());
                            message_recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatActivity.this, "Error in fetching: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                String currentTime = timeFormat.format(calendar.getTime());
                String message = typeMessage.getText().toString();

                Messages messages = new Messages(message, senderUid, currentTime, mAuth.getCurrentUser().getPhoneNumber());
                firebaseDatabase.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseDatabase.getReference().child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .push()
                                    .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ChatActivity.this, "At receiver end !", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ChatActivity.this, "Error in sending Message: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}