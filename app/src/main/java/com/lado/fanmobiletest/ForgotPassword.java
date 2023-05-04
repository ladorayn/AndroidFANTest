package com.lado.fanmobiletest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.lado.fanmobiletest.databinding.ActivityForgotPasswordBinding;

public class ForgotPassword extends AppCompatActivity {

    ActivityForgotPasswordBinding activityForgotPasswordBinding;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();


        activityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());

        activityForgotPasswordBinding.resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = activityForgotPasswordBinding.email.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    return;
                }

                activityForgotPasswordBinding.progressBar.setVisibility(View.VISIBLE);

                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ForgotPassword.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                        activityForgotPasswordBinding.progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPassword.this, "Error! Reset link is not sent " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        activityForgotPasswordBinding.progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        activityForgotPasswordBinding.loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                finish();
            }
        });

        setContentView(activityForgotPasswordBinding.getRoot());
    }
}