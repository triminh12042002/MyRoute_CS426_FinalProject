package com.example.myroute_cs426_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Login extends AppCompatActivity {

    private EditText emailET;
    private EditText passwordET;
    private Button loginButton;
    private Button signUpButton;
    private Button offlineButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        offlineButton = findViewById(R.id.offlineButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(Login.this, SignUp.class);
                startActivity(signUpIntent);

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                if(email.isEmpty() || password.isEmpty())
                    Toast.makeText(Login.this, "username and password can not be empty", Toast.LENGTH_SHORT).show();
                else
                    login(email,password);

            }

        });
        offlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "User not exit, login failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


}