package com.sheraz.app.googleclassroom.classroom.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sheraz.app.googleclassroom.R;
import com.sheraz.app.googleclassroom.classroom.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupProfile extends AppCompatActivity {

    private static final int IMAGE_REQUESTCODE = 111;
    CircleImageView userProfile;
    EditText userName;
    TextView setup;

    StorageReference storageUserRef;
    DatabaseReference userRef;
    FirebaseAuth mAuth;

    ProgressDialog progressDialog;
    String imgURL;
    Uri imgUri;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        userProfile = findViewById(R.id.userImg);
        userName = findViewById(R.id.userName);
        setup = findViewById(R.id.setupBtn);

        storageUserRef = FirebaseStorage.getInstance().getReference("users");
        userRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Setup Profile");

        if(!isNetworkConnected()){
            Toast.makeText(getApplicationContext(), "Please Connect To Internet ! !", Toast.LENGTH_SHORT).show();
        }
        //Getting UserImage From Gallery.
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpImage();
            }
        });

        //Saving User Info
        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Please Connect To Internet ! !", Toast.LENGTH_SHORT).show();
                }
                progressDialog.setMessage("Saving User Profile ...");
                progressDialog.show();
                saveUser();
            }
        });
    }

    private void setUpImage() {
        Intent setupImage = new Intent();
        setupImage.setAction(Intent.ACTION_GET_CONTENT);
        setupImage.setType("image/*");
        startActivityForResult(setupImage, IMAGE_REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUESTCODE && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            userProfile.setImageURI(imgUri);
        }
    }

    private void saveUser() {
        try {
            //Compressing  Image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] imgByte = outputStream.toByteArray();

            storageUserRef.putBytes(imgByte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        storageUserRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imgURL = uri.toString();
                                saveUserInDatabase();
                            }
                        });

                    } else {
                        printMessage("Error in saving User Profile: " + task.getException().getMessage());
                    }
                }
            });
        } catch (IOException e) {
            printMessage("Error: " + e.getMessage());
        }
    }

    private void saveUserInDatabase() {
        progressDialog.setMessage("Saving User Info...");
        progressDialog.show();

        String phoneNumbr = mAuth.getCurrentUser().getPhoneNumber();
        String user_id = mAuth.getCurrentUser().getUid();
        String name = userName.getText().toString();
        String img = imgURL;

        Toast.makeText(this, "img: "+imgURL, Toast.LENGTH_SHORT).show();
        User user = new User(name, user_id,phoneNumbr,img);
        userRef.push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    startActivity(new Intent(SetupProfile.this, CreateOrJoinActivity.class));
                } else {
                    progressDialog.dismiss();
                    printMessage("User Saved Failed in Database: " + task.getException().getMessage());
                }
            }
        });
    }

    public void printMessage(String msg) {
        Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}