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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sheraz.app.googleclassroom.R;
import com.sheraz.app.googleclassroom.classroom.model.ClassModel;

import java.util.ArrayList;
import java.util.List;

public class JoinClass extends AppCompatActivity {

    TextInputLayout classJoiningCode;
    TextView joinClass;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    List<ClassModel> classesList;
    Bundle bundle;
    String uid = "";

    ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Finding Class. . .");
        progressDialog.setTitle("Student Join");
        progressDialog.setCancelable(false);

        classJoiningCode = findViewById(R.id.classJoinCode);
        joinClass = findViewById(R.id.joinClassBtn);

        bundle = getIntent().getExtras();
        uid = bundle.getString("uid");

        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        classesList = new ArrayList<>();

        if(!isNetworkConnected()){
            Toast.makeText(getApplicationContext(), "Please Connect To Internet ! !", Toast.LENGTH_SHORT).show();
        }
        joinClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Please Connect To Internet ! !", Toast.LENGTH_SHORT).show();
                }
                progressDialog.show();
                String code = classJoiningCode.getEditText().getText().toString();
                List<String> classCodes = new ArrayList<>();
                if (code.isEmpty()) {
                    classJoiningCode.setError("Empty Error Code !");
                } else {
                    firebaseDatabase.getReference("Classes")
                            .child("Created By Teachers").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                ClassModel model = snapshot1.getValue(ClassModel.class);
                                if(model.getClassCode().equals(code)){
                                    progressDialog.dismiss();
                                    Toast.makeText(JoinClass.this, "Class Found !", Toast.LENGTH_SHORT).show();
                                    saveDataToJoinedClasses(model);
                                    break;
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(JoinClass.this, "Error in Joining Class: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    private void saveDataToJoinedClasses(ClassModel model) {
        progressDialog.setMessage("Joining Class");
        progressDialog.show();
        firebaseDatabase.getReference("Classes").child("Joined By Students").child(uid).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(JoinClass.this, "Class Joined !", Toast.LENGTH_SHORT).show();
                    Intent moveToMyClasses = new Intent();
                    setResult(RESULT_OK, moveToMyClasses);
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(JoinClass.this, "Error in Joining class: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}