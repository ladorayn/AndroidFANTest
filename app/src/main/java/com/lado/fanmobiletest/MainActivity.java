package com.lado.fanmobiletest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.util.LogicUtils;
import com.lado.fanmobiletest.adapter.UserAdapter;
import com.lado.fanmobiletest.databinding.ActivityMainBinding;
import com.lado.fanmobiletest.model.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    FirebaseFirestore fFirestore;
    FirebaseAuth fAuth;
    ArrayList<User> users;
    UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());

        linearLayoutManager = new LinearLayoutManager(MainActivity.this);

        recyclerView = activityMainBinding.recycleView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        users = new ArrayList<>();

        fAuth = FirebaseAuth.getInstance();
        fFirestore = FirebaseFirestore.getInstance();

        adapter = new UserAdapter(MainActivity.this, users);
        recyclerView.setAdapter(adapter);

        activityMainBinding.allToggleButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                adapter.setUsers(new ArrayList<>());
                adapter.notifyDataSetChanged();

                activityMainBinding.allToggleButton.setTextColor(getResources().getColorStateList(R.color.white));
                activityMainBinding.allToggleButton.setBackgroundTintList(getResources().getColorStateList(R.color.login_button_normal));

                activityMainBinding.notVerifiedToggleButton.setTextColor(R.color.black);
                activityMainBinding.notVerifiedToggleButton.setBackgroundTintList(getResources().getColorStateList(R.color.gray));

                activityMainBinding.verifiedToggleButton.setTextColor(R.color.black);
                activityMainBinding.verifiedToggleButton.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
                AllFilter();
            }
        });

        activityMainBinding.verifiedToggleButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                adapter.setUsers(new ArrayList<>());
                adapter.notifyDataSetChanged();

                activityMainBinding.verifiedToggleButton.setTextColor(getResources().getColorStateList(R.color.white));
                activityMainBinding.verifiedToggleButton.setBackgroundTintList(getResources().getColorStateList(R.color.login_button_normal));

                activityMainBinding.notVerifiedToggleButton.setTextColor(R.color.black);
                activityMainBinding.notVerifiedToggleButton.setBackgroundTintList(getResources().getColorStateList(R.color.gray));

                activityMainBinding.allToggleButton.setTextColor(R.color.black);
                activityMainBinding.allToggleButton.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
                FilterVerified();
            }
        });

        activityMainBinding.notVerifiedToggleButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                adapter.setUsers(new ArrayList<>());
                adapter.notifyDataSetChanged();

                activityMainBinding.notVerifiedToggleButton.setTextColor(getResources().getColorStateList(R.color.white));
                activityMainBinding.notVerifiedToggleButton.setBackgroundTintList(getResources().getColorStateList(R.color.login_button_normal));


                activityMainBinding.verifiedToggleButton.setTextColor(R.color.black);
                activityMainBinding.verifiedToggleButton.setBackgroundTintList(getResources().getColorStateList(R.color.gray));

                activityMainBinding.allToggleButton.setTextColor(R.color.black);
                activityMainBinding.allToggleButton.setBackgroundTintList(getResources().getColorStateList(R.color.gray));
                FilterNotVerified();
            }
        });

        EventChangeListener();

        activityMainBinding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        setContentView(activityMainBinding.getRoot());
    }

    private void AllFilter() {
        fFirestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<User> filteredUsers = new ArrayList<>();
                            for (DocumentChange dc : task.getResult().getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    String name = (String) dc.getDocument().get("name");
                                    String email = (String) dc.getDocument().get("email");
                                    Boolean isEmailVerified = (Boolean) dc.getDocument().get("isEmailVerified");
                                    User user = new User(name, email, isEmailVerified);
                                    filteredUsers.add(user);
                                }
                            }
                            adapter.setUsers(filteredUsers);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("FIRESTORE_ERROR", task.getException().getMessage());
                        }
                    }
                });
    }

    private void FilterNotVerified() {
        fFirestore.collection("users").whereEqualTo("isEmailVerified", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<User> filteredUsers = new ArrayList<>();
                            for (DocumentChange dc : task.getResult().getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    String name = (String) dc.getDocument().get("name");
                                    String email = (String) dc.getDocument().get("email");
                                    Boolean isEmailVerified = (Boolean) dc.getDocument().get("isEmailVerified");
                                    User user = new User(name, email, isEmailVerified);
                                    filteredUsers.add(user);
                                }
                            }
                            adapter.setUsers(filteredUsers);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("FIRESTORE_ERROR", task.getException().getMessage());
                        }
                    }
                });
    }

    private void FilterVerified() {
        fFirestore.collection("users").whereEqualTo("isEmailVerified", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<User> filteredUsers = new ArrayList<>();
                            for (DocumentChange dc : task.getResult().getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    String name = (String) dc.getDocument().get("name");
                                    String email = (String) dc.getDocument().get("email");
                                    Boolean isEmailVerified = (Boolean) dc.getDocument().get("isEmailVerified");
                                    User user = new User(name, email, isEmailVerified);
                                    filteredUsers.add(user);
                                }
                            }
                            adapter.setUsers(filteredUsers);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("FIRESTORE_ERROR", task.getException().getMessage());
                        }
                    }
                });
    }

    private void EventChangeListener() {

        fFirestore.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FIRESTORE_ERROR", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {

                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                String name = (String) dc.getDocument().get("name");
                                String email = (String) dc.getDocument().get("email");
                                Boolean isEmailVerified = (Boolean) dc.getDocument().get("isEmailVerified");

                                User user = new User(name, email, isEmailVerified);

                                users.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}