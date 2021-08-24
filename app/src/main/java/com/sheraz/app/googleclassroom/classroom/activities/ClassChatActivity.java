package com.sheraz.app.googleclassroom.classroom.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sheraz.app.googleclassroom.R;
import com.sheraz.app.googleclassroom.classroom.model.Messages;
import com.sheraz.app.googleclassroom.classroom.adapter.GroupChatAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ClassChatActivity extends AppCompatActivity {

    private static final int OPEN_GALLERY = 111;
    RecyclerView classesChat;
    EditText message;
    ImageButton sendMessage, sendAttachment;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    Bundle bundle;

    GroupChatAdapter adapter;
    List<Messages> messagesList;
    ProgressDialog progressDialog;

    Uri imgUri;
    String senderUid = "";
    String name = "";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        classesChat = findViewById(R.id.classChat);
        message = findViewById(R.id.class_typedMessage);
        sendMessage = findViewById(R.id.send_messageBtn);
        sendAttachment = findViewById(R.id.send_attach);

        messagesList = new ArrayList<>();
        bundle = getIntent().getExtras();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        senderUid = mAuth.getCurrentUser().getUid();
        name = bundle.getString("name");

        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        classesChat = findViewById(R.id.classChat);
        classesChat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        classesChat.setHasFixedSize(true);

        if(!isNetworkConnected()){
            Toast.makeText(this, "Please Connect Your Device To Internet ! !", Toast.LENGTH_SHORT).show();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Class Chat . . .");
        progressDialog.setTitle(name);
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseDatabase.getReference().child("ClassChat")
                .child(name)
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
                            progressDialog.dismiss();
                            adapter = new GroupChatAdapter(messagesList, getApplicationContext());
                            classesChat.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ClassChatActivity.this, "Null Data !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ClassChatActivity.this, "Error in fetching: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Please Connect Your Device To Internet ! !", Toast.LENGTH_SHORT).show();
                }
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                String currentTime = timeFormat.format(calendar.getTime());
                String typedMessage = message.getText().toString();

                Messages messages = new Messages(typedMessage, senderUid, currentTime, mAuth.getCurrentUser().getPhoneNumber());
                message.setText("");

                firebaseDatabase.getReference().child("ClassChat")
                        .child(name)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ClassChatActivity.this, "Error in sending Message: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        sendAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent();
                openGallery.setAction(Intent.ACTION_GET_CONTENT);
                openGallery.setType("image/*");
                startActivityForResult(openGallery, OPEN_GALLERY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_GALLERY && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                imgUri = data.getData();
                saveImageToDatabase();

            }
        }
    }

    private void saveImageToDatabase() {
        progressDialog.setMessage("Uploading . . .");
        progressDialog.show();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, outputStream);
            byte[] arr = outputStream.toByteArray();
            StorageReference reference = firebaseStorage.getReference("Classchat");
            reference.putBytes(arr)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imgURL = uri.toString();
                                        Calendar calendar = Calendar.getInstance();
                                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                                        String currentTime = timeFormat.format(calendar.getTime());
                                        String typedMessage = message.getText().toString();
                                        Messages messages = new Messages(typedMessage, senderUid, currentTime,mAuth.getCurrentUser().getPhoneNumber());
                                        messages.setMessage("Photo");
                                        messages.setImgURL(imgURL);
                                        message.setText("");
                                        firebaseDatabase.getReference().child("ClassChat")
                                                .child(name)
                                                .child("messages")
                                                .push()
                                                .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    adapter.notifyDataSetChanged();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ClassChatActivity.this, "Error in sending Message: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
        } catch (IOException ioException) {
            Toast.makeText(this, "Error: " + ioException.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,CreatedClasses.class));
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}