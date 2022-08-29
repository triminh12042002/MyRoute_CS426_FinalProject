package com.example.myroute_cs426_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    private EditText nameET;
    private EditText emailET;
    private EditText passwordET;
    private Button loginButton;
    private Button signUpButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        nameET = findViewById(R.id.nameET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        signUpButton = findViewById(R.id.signUpButton);

        // test add data to database



        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                String name = nameET.getText().toString();
                //Toast.makeText(SignUp.this, email + " / " + password, Toast.LENGTH_SHORT).show();
                if(email.isEmpty() || password.isEmpty() || name.isEmpty())
                    Toast.makeText(SignUp.this, "username and password can not be empty", Toast.LENGTH_SHORT).show();
                else
                    signup(name, email, password);

            }
        });
    }

    private void signup(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUp.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                            //Log.w("TAG",  name + email + mAuth.getCurrentUser().getUid());
                            addUserToDatabase(name, email, mAuth.getCurrentUser().getUid());
                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Occur error: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void addUserToDatabase(String name, String email, String uid) {
//        mDbRef = FirebaseDatabase.getInstance().getReference("https://myroutecs426finalproject-876b0-default-rtdb.firebaseio.com/");
        User user = new User(name, email, uid);
//        mDbRef.child("user").child(uid).setValue(user);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://myroutecs426finalproject-876b0-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference();
        myRef.child("user").child(uid).setValue(user);


    }


}