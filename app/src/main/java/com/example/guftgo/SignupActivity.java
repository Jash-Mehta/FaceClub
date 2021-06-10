package com.example.guftgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.guftgo.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating your account...");
        progressDialog.setCancelable(false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String email = binding.sihnupEmailbox.getText().toString();
                String password = binding.signupPassword.getText().toString();
                String name = binding.signupName.getText().toString();
                Users users = new Users(name,email,password);

                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String uid = task.getResult().getUser().getUid();
                            firebaseFirestore.collection("Users")
                                    .document(uid)
                                    .set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(SignupActivity.this, "data is saved in database", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(SignupActivity.this,task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}