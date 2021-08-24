package com.sheraz.app.googleclassroom.classroom.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.sheraz.app.googleclassroom.R;

public class CreateOrJoinActivity extends AppCompatActivity {

    TextView createClass, joinClass;
    FirebaseAuth mAuth;
    RelativeLayout no_internetLayout;
    ImageButton reload;
    ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_join);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Checking Internet");
        progressDialog.setMessage("Reloading. . . ");
        progressDialog.setCancelable(false);

        createClass = findViewById(R.id.createClass);
        joinClass = findViewById(R.id.joinClass);
        no_internetLayout = findViewById(R.id.createOrJOin_no_internetLayout);
        reload = findViewById(R.id.reload);
        mAuth = FirebaseAuth.getInstance();

        if (!isNetworkConnected()) {
            Toast.makeText(this, "Please Connect To Internet ! !", Toast.LENGTH_SHORT).show();
        }

        createClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkConnected()) {
                    Toast.makeText(getApplicationContext(), "Please Connect To Internet ! !", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(CreateOrJoinActivity.this, CreatedClasses.class);
                    intent.putExtra("uid", mAuth.getCurrentUser().getUid());
                    startActivity(intent);
                }

            }
        });

        joinClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkConnected()) {
                    Toast.makeText(getApplicationContext(), "Please Connect To Internet ! !", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(CreateOrJoinActivity.this, JoinedClasses.class));
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