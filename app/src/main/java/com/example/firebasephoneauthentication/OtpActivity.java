package com.example.firebasephoneauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class OtpActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button verifyBtn;
    EditText verifyEt;
    String currentPhoneCode;
    String phoneNum;
    ImageView backIcon;
    CShowProgress showProgress = CShowProgress.getInstance();
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            currentPhoneCode = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.VISIBLE);
                verifyEt.setText(code);
                phoneVerification(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            progressBar.setVisibility(View.INVISIBLE);
            Log.d("clima", e.getMessage());
            Toast.makeText(OtpActivity.this, "Error in OTP Verification " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        verifyBtn = findViewById(R.id.verify);
        verifyEt = findViewById(R.id.phone);
        progressBar = findViewById(R.id.progressBar);

        getSupportActionBar().hide();
        //backIcon = findViewById(R.id.back_icon);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(true);


        mAuth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        phoneNum = intent.getStringExtra("phoneNum");
        if (phoneNum != null) {
            startPhoneNumberVerification(phoneNum);
        }

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (verifyEt.getText().toString().isEmpty() || verifyEt.getText().toString().length() < 6) {
                    verifyEt.setError("Wrong OTP...");
                    verifyEt.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                phoneVerification(verifyEt.getText().toString());
            }
        });


    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void phoneVerification(String codeByUser) {
        if (currentPhoneCode != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(currentPhoneCode, codeByUser);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        showProgress.showProgress(OtpActivity.this);
        progressBar.setVisibility(View.INVISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        showProgress.hideProgress();
                        if (task.isSuccessful()) {
                            Toast.makeText(OtpActivity.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            Log.d("climaid", user.getUid());
                            Log.d("climaid2", user.getPhoneNumber());

                        } else {
                            Toast.makeText(OtpActivity.this, "signInWithCredential:failed", Toast.LENGTH_SHORT).show();
                            Log.w("clima", "signInWithCredential:failure", task.getException());
                            progressBar.setVisibility(View.INVISIBLE);
                            //showProgress.showProgress();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}