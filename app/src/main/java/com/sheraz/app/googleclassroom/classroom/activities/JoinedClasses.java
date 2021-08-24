package com.sheraz.app.googleclassroom.classroom.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sheraz.app.googleclassroom.R;
import com.sheraz.app.googleclassroom.classroom.model.ClassModel;
import com.sheraz.app.googleclassroom.classroom.adapter.ClassesAdapter;

import java.util.ArrayList;
import java.util.List;

public class JoinedClasses extends AppCompatActivity {

    RecyclerView joinedClasses_list;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    FloatingActionButton joinClass;
    ClassesAdapter adapter;
    List<ClassModel> joinedClassesList;

    ProgressDialog progressDialog;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_classes);


        joinedClasses_list = findViewById(R.id.joinedClasses_list);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        joinClass = findViewById(R.id.joinNewClass);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        joinedClasses_list.setLayoutManager(layoutManager);

        joinedClassesList = new ArrayList<>();
        adapter = new ClassesAdapter(joinedClassesList,this);
        joinedClasses_list.setAdapter(adapter);


        if(!isNetworkConnected()){
            Toast.makeText(getApplicationContext(), "Please Connect To Internet ! !", Toast.LENGTH_SHORT).show();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Your Joined Classes . . .");
        progressDialog.setTitle("Classes");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseDatabase.getReference("Classes").child("Joined By Students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    ClassModel models = snapshot1.getValue(ClassModel.class);
                    joinedClassesList.add(models);
                }
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(JoinedClasses.this, "Error in getting Joined Classes: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        joinClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Please Connect To Internet ! !", Toast.LENGTH_SHORT).show();
                }
                Intent gotoJoinClass = new Intent(getApplicationContext(),JoinClass.class);
                gotoJoinClass.putExtra("uid",mAuth.getCurrentUser().getUid());
                startActivityForResult(gotoJoinClass,111);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111 && resultCode == RESULT_OK){

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}