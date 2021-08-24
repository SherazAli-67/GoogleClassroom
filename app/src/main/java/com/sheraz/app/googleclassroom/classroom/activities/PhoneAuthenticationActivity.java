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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.sheraz.app.googleclassroom.MainActivity;
import com.sheraz.app.googleclassroom.R;

import java.util.concurrent.TimeUnit;

public class PhoneAuthenticationActivity extends AppCompatActivity implements View.OnClickListener {

    CountryCodePicker countryCodePicker;
    TextView verifyNumber;
    EditText phoneNumber;

    String verificationID;
    String countryCode;
    String number;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    PhoneAuthOptions options;
    FirebaseAuth mAuth;

    ProgressDialog progressDialog;
    RelativeLayout no_internetLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);

        countryCodePicker = findViewById(R.id.countryCode);
        verifyNumber = findViewById(R.id.verifyNumber);
        phoneNumber = findViewById(R.id.phoneNumber);
        no_internetLayout = findViewById(R.id.no_internetLayout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Authentication");
        progressDialog.setMessage("Generating OTP . . .");
        progressDialog.setCancelable(false);
        mAuth = FirebaseAuth.getInstance();

        
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });
        verifyNumber.setOnClickListener(this);
        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                String smsCode = phoneAuthCredential.getSmsCode();
                verifyPhoneNumbr(smsCode);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                progressDialog.dismiss();
                super.onCodeSent(s, forceResendingToken);
                verificationID = s;
                Intent intent = new Intent(PhoneAuthenticationActivity.this, VerifyOTPActivity.class);
                intent.putExtra("code", verificationID);
                startActivity(intent);
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        if(isNetworkConnected()){
            progressDialog.show();
            String input = phoneNumber.getText().toString();
            number = countryCode + input;

            options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(number)
                    .setActivity(this)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setCallbacks(mCallBacks)
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }else{
            no_internetLayout.setVisibility(View.VISIBLE);
        }
    }

    private void verifyPhoneNumbr(String smsCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, smsCode);
        signInPhoneCredential(credential);
    }

    private void signInPhoneCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    startActivity(new Intent(PhoneAuthenticationActivity.this, MainActivity.class));
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(PhoneAuthenticationActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        if(mAuth.getCurrentUser() !=null)
            startActivity(new Intent(this,CreateOrJoinActivity.class));
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}