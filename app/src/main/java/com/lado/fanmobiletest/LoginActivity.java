package com.lado.fanmobiletest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lado.fanmobiletest.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding activityLoginBinding;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        // Update isEmailVerified to true in Firestore
                        FirebaseFirestore.getInstance().collection("users")
                                .document(user.getUid())
                                .update("isEmailVerified", true)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("LoginActivity", "isEmailVerified updated to true");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("LoginActivity", "Error updating isEmailVerified", e);
                                    }
                                });
                    } else {
                        Log.d("LoginActivity", "Email is not verified yet");
                    }
                }
            }
        };

        // Add the listener to FirebaseAuth
        fAuth.addAuthStateListener(authStateListener);

        if (fAuth.getCurrentUser() != null) {
            if (fAuth.getCurrentUser().isEmailVerified()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

        activityLoginBinding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            }
        });

        activityLoginBinding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = activityLoginBinding.email.getText().toString().trim();
                String password = activityLoginBinding.password.getText().toString().trim();

                validateInput(email, password);

                if (!(emailValidate(email) && passwordValidate(password))) {
                    return;
                }

                boolean notValidInput = !(emailValidate(email) || passwordValidate(password));
                if (notValidInput) {
                    return;
                }

                activityLoginBinding.progressBar.setVisibility(View.VISIBLE);

                // authenticate user
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser fUser = fAuth.getCurrentUser();
                            if (fUser != null) {
                                if (fUser.isEmailVerified()) {


                                    db.collection("users")
                                            .document(fUser.getUid())
                                            .update("isEmailVerified", true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("LoginActivity", "isEmailVerified updated to true");
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("LoginActivity", "Error updating isEmailVerified", e);
                                                    Toast.makeText(LoginActivity.this, "Error updating isEmailVerified", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(LoginActivity.this, "Please check your email to verify your account", Toast.LENGTH_SHORT).show();
                                    activityLoginBinding.progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error!! "+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        activityLoginBinding.progressBar.setVisibility(View.GONE);
                    }
                });

                activityLoginBinding.email.setText("");
                activityLoginBinding.password.setText("");
            }


        });


        activityLoginBinding.signupPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        setContentView(activityLoginBinding.getRoot());
    }

    private void validateInput(String email, String password) {
        if (!emailValidate(email)) {
            activityLoginBinding.email.setError("Email is required!");
        }

        if (!passwordValidate(password)) {
            activityLoginBinding.password.setError("Password is required and must be >= 8 characters");
        }
    }

    private boolean passwordValidate(String password) {

        return !(TextUtils.isEmpty(password) || (!TextUtils.isEmpty(password) && (password.length() < 8)));
    }

    private boolean emailValidate(String email) {
        return !TextUtils.isEmpty(email);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the listener from FirebaseAuth when the activity is destroyed
        if (authStateListener != null) {
            fAuth.removeAuthStateListener(authStateListener);
        }
    }
}
