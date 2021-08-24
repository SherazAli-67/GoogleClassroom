package com.sheraz.app.googleclassroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sheraz.app.googleclassroom.chat.adapter.ChatAdapter;
import com.sheraz.app.googleclassroom.classroom.model.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView chats;
    ChatAdapter chatAdapter;
    FirebaseAuth mAuth;
    DatabaseReference userRef;
    List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Chats");
        mAuth = FirebaseAuth.getInstance();

        userRef = FirebaseDatabase.getInstance()
                .getReference("users");
        chats = findViewById(R.id.chats);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        chats.setLayoutManager(linearLayoutManager);
        chats.setHasFixedSize(true);
        users = new ArrayList<>();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                if(snapshot.exists()){
                    if(snapshot != null){
                        for(DataSnapshot snapshot1: snapshot.getChildren()){
                            User user = snapshot1.getValue(User.class);
                            users.add(user);
                        }
                        chatAdapter = new ChatAdapter(users,getApplicationContext());
                        chats.setAdapter(chatAdapter);
                        chatAdapter.notifyDataSetChanged();
                    }else{
                        printMessage("No User !");
                    }
                }else{
                    printMessage("User Does Not Exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                printMessage("Database Error: "+error.getMessage());
            }
        });
    }

    public void printMessage(String msg){
        Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show();
    }
}