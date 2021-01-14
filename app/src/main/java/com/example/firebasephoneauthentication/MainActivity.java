package com.example.firebasephoneauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button verifyBtn;
    EditText phoneNumEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        verifyBtn = findViewById(R.id.verify);
        phoneNumEt = findViewById(R.id.phone);
        phoneNumEt.requestFocus();
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!phoneNumEt.getText().toString().isEmpty()) {
                    String phoneNum = phoneNumEt.getText().toString();//.substring(1);
                    startActivity(new Intent(MainActivity.this, OtpActivity.class).putExtra("phoneNum", phoneNum));

                } else {
                    phoneNumEt.setError("PhoneNum required");
                }
            }
        });
    }
}