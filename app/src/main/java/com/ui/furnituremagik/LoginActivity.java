package com.ui.furnituremagik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mobileText,verifyText;
    private ValidationClass validationClass;
    private Button getOtpButton,signInButton;
    String mobileNumber;
    String verificationId;
    String code;
    PhoneAuthCredential credential;
    ConnectivityManager connectivityManager;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(getResources().getString(R.string.app_name));
            actionBar.setSubtitle(getResources().getString(R.string.sign_in));
        }
        mobileText=findViewById(R.id.mobile_number);
        verifyText=findViewById(R.id.verify_code);
        getOtpButton=findViewById(R.id.get_otp);
        signInButton=findViewById(R.id.sign_in);
        mAuth = FirebaseAuth.getInstance();
        validationClass=new ValidationClass();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validationClass.getMobileDataState(connectivityManager)) {
                    getVerificationCode();
                }
                else
                {
                    Snackbar.make(view,"Turn On Mobile Data!",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                }
            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code=verifyText.getText().toString();
                verifySignIn(code);
            }
        });


        mobileText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if(validationClass.mobileValidation(mobileText.getText().toString()))
               {
                   getOtpButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                   getOtpButton.setTextColor(getResources().getColor(android.R.color.white));
                   getOtpButton.setEnabled(true);
               }
               else
               {
                   getOtpButton.setEnabled(false);
                   getOtpButton.setBackgroundColor(getResources().getColor(R.color.bgsnowgrey));
                   getOtpButton.setTextColor(getResources().getColor(android.R.color.darker_gray));
               }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        verifyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(validationClass.codeValidation(verifyText.getText().toString()))
                {
                    signInButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    signInButton.setTextColor(getResources().getColor(android.R.color.white));
                    signInButton.setEnabled(true);
                }
                else
                {
                    signInButton.setEnabled(false);
                    signInButton.setBackgroundColor(getResources().getColor(R.color.bgsnowgrey));
                    signInButton.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void getVerificationCode() {
        //[
        // Testing1:
        //      Mobile Number: 8939477936,
        //      OTP 762859
        //Testing1:
        //      Mobile Number: 9994122274,
        //      OTP 762859
        // ]
        mobileNumber = "+91"+mobileText.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobileNumber, 60, TimeUnit.SECONDS, this, mCallbacks);
    }

    private void verifySignIn(String code) {
        credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//            signInWithPhoneAuthCredential(phoneAuthCredential);
            code = phoneAuthCredential.getSmsCode();
            if (code != null){
                verifySignIn(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId=s;
            verifyText.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            signInButton.setEnabled(false);
            verifyText.requestFocus();
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            intent=new Intent(LoginActivity.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("mobileKey",mobileNumber);
                            startActivity(intent);
//                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}