package com.lado.fanmobiletest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lado.fanmobiletest.databinding.ActivitySignupBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding activitySignupBinding;
    FirebaseAuth fAuth;
    FirebaseFirestore fFirestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        fAuth = FirebaseAuth.getInstance();
        fFirestore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            if (fAuth.getCurrentUser().isEmailVerified()) {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

        activitySignupBinding.loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        activitySignupBinding.regisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();

                if (nameValidate() && emailValidate() && passwordValidate() && confirmValidate()) {

                    activitySignupBinding.progressBar.setVisibility(View.VISIBLE);

                    String name = activitySignupBinding.name.getText().toString().trim();
                    String email = activitySignupBinding.email.getText().toString().trim();
                    String password = activitySignupBinding.password.getText().toString().trim();

                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser fUser = fAuth.getCurrentUser();

                                assert fUser != null;
                                fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SignupActivity.this, "Verification email has been sent", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("EMAIL_VERIFICATION", "onFailure: Email not sent, "+ e.getLocalizedMessage());
                                    }
                                });


                                userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                                DocumentReference documentReference = fFirestore.collection("users").document(userID);
                                Map<String, Object> user =  new HashMap<>();
                                user.put("name", name);
                                user.put("email", email);
                                user.put("isEmailVerified", false);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("REGISTER", "onSuccess: user profile is created for "+ userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("REGISTER", "onFailure: "+ e.getLocalizedMessage());
                                    }
                                });

                                activitySignupBinding.progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(SignupActivity.this, "Error!! "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                activitySignupBinding.progressBar.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupActivity.this, "Error!! "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            activitySignupBinding.progressBar.setVisibility(View.GONE);
                        }
                    });
                }

                activitySignupBinding.name.clearFocus();
                activitySignupBinding.email.clearFocus();
                activitySignupBinding.password.clearFocus();
                activitySignupBinding.confirmPassword.clearFocus();




            }
        });

        setContentView(activitySignupBinding.getRoot());
    }

    private void validateInput() {
        if (!nameValidate()){
            activitySignupBinding.name.setError("min 5 letter, max 50 letter");
            activitySignupBinding.name.setText("");
        } else {
            activitySignupBinding.name.setError(null);
        }
        if (!emailValidate()) {
            activitySignupBinding.email.setError("email not valid");
            activitySignupBinding.email.setText("");
        } else {
            activitySignupBinding.email.setError(null);
        }
        if (!passwordValidate()) {
            activitySignupBinding.password.setError("min 8 char, should contains number, uppercase and lowercase");
            activitySignupBinding.password.setText("");
        } else {
            activitySignupBinding.password.setError(null);
        }
        if (!confirmValidate()){
            activitySignupBinding.confirmPassword.setError("password not match");
            activitySignupBinding.confirmPassword.setText("");
        } else {
            activitySignupBinding.confirmPassword.setError(null);
        }
    }

    private boolean confirmValidate() {
        String password = activitySignupBinding.password.getText().toString();
        String confirmPassword = activitySignupBinding.confirmPassword.getText().toString();
        return (password.equals(confirmPassword) && !confirmPassword.isEmpty());
    }

    private boolean passwordValidate() {
        String password = activitySignupBinding.password.getText().toString();
        Pattern upperCasePattern = Pattern.compile("[A-Z ]");
        Pattern lowerCasePattern = Pattern.compile("[a-z ]");
        Pattern digitCasePattern = Pattern.compile("[0-9 ]");
        return !(password.isEmpty() || password.length() < 8 || !upperCasePattern.matcher(password).find() ||  !lowerCasePattern.matcher(password).find() || !digitCasePattern.matcher(password).find());
    }

    private boolean emailValidate() {
        String email = activitySignupBinding.email.getText().toString();
        return !(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private boolean nameValidate() {
        String name = activitySignupBinding.name.getText().toString();
        return !(name.isEmpty() || (name.length() < 3 || name.length() > 50));
    }
}