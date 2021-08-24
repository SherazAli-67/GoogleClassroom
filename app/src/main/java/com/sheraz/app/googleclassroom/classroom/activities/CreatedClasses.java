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
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sheraz.app.googleclassroom.R;
import com.sheraz.app.googleclassroom.classroom.model.ClassModel;
import com.sheraz.app.googleclassroom.classroom.adapter.CreatedClassesAdapter;

import java.util.ArrayList;
import java.util.List;

public class CreatedClasses extends AppCompatActivity {

    RecyclerView createdClassesList;
    FirebaseDatabase firebaseDatabase;
    CreatedClassesAdapter adapter;
    List<ClassModel> classesList;

    FloatingActionButton createNewClass;
    FirebaseAuth mAuth;
    String uid ="";
    ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classrooms);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("My Classes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createNewClass = findViewById(R.id.createNewClass);
        createdClassesList = findViewById(R.id.createdClassesList);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        classesList = new ArrayList<>();

        createdClassesList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        createdClassesList.setLayoutManager(layoutManager);

        adapter = new CreatedClassesAdapter(classesList, this);
        createdClassesList.setAdapter(adapter);
        uid = mAuth.getCurrentUser().getUid();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Your Classes . . .");
        progressDialog.setTitle("Classes");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if(!isNetworkConnected()){
            Toast.makeText(getApplicationContext(), "Please Connect To Internet ! !", Toast.LENGTH_SHORT).show();
        }
        createNewClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Please Connect To Internet ! !", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(), CreateClass.class);
                intent.putExtra("uid", uid);
                startActivityForResult(intent, 111);
            }
        });

        DatabaseReference classRef = firebaseDatabase.getReference("Classes");
        classRef.child("Created By Teachers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classesList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ClassModel classModel = snapshot1.getValue(ClassModel.class);
                    if (classModel.getUser_id().equals(uid)) {
                        classesList.add(classModel);
                    }
                }
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(CreatedClasses.this, "Error in CreatedClasses: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111 && resultCode == RESULT_OK){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return super.onNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}