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
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sheraz.app.googleclassroom.R;
import com.sheraz.app.googleclassroom.classroom.model.ClassModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateClass extends AppCompatActivity {

    private static final int IMAGE_REQUESTCODE = 111;
    TextInputLayout className, section, subject;
    CircleImageView classImg;
    TextView createClass;

    FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage;
    FirebaseAuth mAuth;

    ProgressDialog progressDialog;
    String currentUID = "";
    String classCode = "";

    String imgURL;
    Uri imgUri;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("Create Class");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Classroom");
        progressDialog.setMessage("Creating Class");
        progressDialog.setCancelable(false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();
        className = findViewById(R.id.className);
        section = findViewById(R.id.section);
        subject = findViewById(R.id.subject);
        createClass = findViewById(R.id.createClassBtn);
        classImg = findViewById(R.id.classImage);

        if(!isNetworkConnected()){
            Toast.makeText(getApplicationContext(), "Please Connect To Internet ! !", Toast.LENGTH_SHORT).show();
            return;
        }
        classImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getImage = new Intent();
                getImage.setType("image/*");
                getImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(getImage, IMAGE_REQUESTCODE);
            }
        });

        createClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Please Connect To Internet ! !", Toast.LENGTH_SHORT).show();
                    return;
                }
                String classTitle = className.getEditText().getText().toString();
                String sec = section.getEditText().getText().toString();
                String sub = subject.getEditText().getText().toString();
                String uid = mAuth.getCurrentUser().getUid();
                classCode = getClassCode(20);
                if (fieldsAreValid(classTitle, sec, sub)) {
                    progressDialog.show();
                    saveImageInDatabase();
                } else
                    Toast.makeText(CreateClass.this, "Invalid !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUESTCODE && resultCode == RESULT_OK) {
            classImg.setImageURI(data.getData());
            imgUri = data.getData();
        }
    }

    private void saveImageInDatabase() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, outputStream);
            byte[] imgByte = outputStream.toByteArray();

            String name = className.getEditText().getText().toString();
            StorageReference imgRef = storage.getReference("Classes").child(name);

            imgRef.putBytes(imgByte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CreateClass.this, "Image Saved !", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imgURL = uri.toString();
                                saveIntoDatabase();
                            }
                        });

                    }
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, "Error in saving Image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveIntoDatabase() {
        progressDialog.setMessage("Saving Class in the Database. . .");
        String classTitle = className.getEditText().getText().toString();
        String sec = section.getEditText().getText().toString();
        String sub = subject.getEditText().getText().toString();
        String uid = mAuth.getCurrentUser().getUid();
        classCode = getClassCode(6);

        ClassModel classModel = new ClassModel(classTitle, sub, sec, uid, classCode, imgURL);
        firebaseDatabase.getReference("Classes")
                .child("Created By Teachers")
                .push()
                .setValue(classModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(CreateClass.this, "Error in creating class: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean fieldsAreValid(String classTitle, String sec, String sub) {
        if (classTitle.isEmpty() && sub.isEmpty()) {
            className.setError("Class name cannot be empty");
            subject.setError("Subject name cannot be empty");
            return false;

        } else if (sub.isEmpty()) {
            subject.setError("Subject name cannot be empty");
            return false;
        } else if (classTitle.isEmpty()) {
            className.setError("Class name cannot be empty");
        }

        return true;
    }

    public String getClassCode(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}