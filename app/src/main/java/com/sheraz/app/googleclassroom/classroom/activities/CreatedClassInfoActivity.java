package com.sheraz.app.googleclassroom.classroom.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sheraz.app.googleclassroom.R;
import com.sheraz.app.googleclassroom.chat.ChatActivity;
import com.sheraz.app.googleclassroom.classroom.model.ClassModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreatedClassInfoActivity extends AppCompatActivity {

    CircleImageView classImg;
    TextView classTitle, classTitle2, classTeacher, classCode, openChat;
    Bundle bundle;

    FirebaseDatabase firebaseDatabase;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_class_info);

        getSupportActionBar().setTitle("Class Info");
        classImg = findViewById(R.id.classInfo_classImg);
        classTitle = findViewById(R.id.classInfo_classTitle);
        classTitle2 = findViewById(R.id.classInfo_classTitle2);
        classTeacher = findViewById(R.id.classInfo_classTeacher);
        classCode = findViewById(R.id.classInfo_classCode);
        openChat = findViewById(R.id.classInfo_chatActivityBtn);

        bundle = getIntent().getExtras();
        String code = bundle.getString("classcode");
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Class Info");
        progressDialog.setMessage("Getting Class Information. . .");
        progressDialog.setCancelable(false);
        progressDialog.show();

        openChat.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(! isNetworkConnected()){
                    Toast.makeText(CreatedClassInfoActivity.this, "Your Device is Not Connected To Internet", Toast.LENGTH_SHORT).show();
                }else{
                    Intent gotoClassChat = new Intent(getApplicationContext(), ClassChatActivity.class);
                    gotoClassChat.putExtra("name", classTitle.getText().toString());
                    gotoClassChat.putExtra("classcode", classCode.getText().toString());
                    startActivity(gotoClassChat);
                }
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("Classes")
                .child("Created By Teachers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ClassModel model = snapshot1.getValue(ClassModel.class);
                    if (model.getClassCode().equals(code)) {
                        progressDialog.dismiss();
                        classTitle.setText(model.getClass_name());
                        classTitle2.setText(model.getClass_name());
                        classTeacher.setText(model.getSubject_teacher());
                        classCode.setText(model.getClassCode());
                        Glide.with(CreatedClassInfoActivity.this).load(model.getImg()).into(classImg);
                        Toast.makeText(CreatedClassInfoActivity.this, "Class Found !", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(CreatedClassInfoActivity.this, "Error in Joining Class: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}