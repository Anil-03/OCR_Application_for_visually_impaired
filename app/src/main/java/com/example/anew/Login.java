package com.example.anew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText loginEmail,loginPassword;
    Voice speech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        speech=new Voice(this);
        auth=FirebaseAuth.getInstance();
        loginEmail=findViewById(R.id.loginEmail);
        loginPassword=findViewById(R.id.loginPassword);
        TextView signupRedirectText = findViewById(R.id.signupRedirectText);
        Button loginButton = findViewById(R.id.btnLogin);

        loginButton.setOnClickListener(v->{
            String email=loginEmail.getText().toString();
            String pass=loginPassword.getText().toString();
            if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                if(!pass.isEmpty()){
                    auth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(authResult -> {
                        Toast.makeText(Login.this,"Login Successful",Toast.LENGTH_LONG).show();
                        speech.speak("Login Successful");
                        startActivity(new Intent(Login.this,MainActivity.class));
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(Login.this,"Login Failed",Toast.LENGTH_LONG).show();
                        speech.speak("Login Failed");
                    });

                }else{
                    loginPassword.setError("Password cannot be empty");
                    speech.speak("Password cannot be empty");
                }
            } else if (email.isEmpty()) {
                loginEmail.setError("Email cannot be empty");
                speech.speak("Email cannot be empty");
            }else {
                loginEmail.setError("Please enter valid email/password");
            }
        });
        signupRedirectText.setOnClickListener(v -> startActivity(new Intent(Login.this,SignUp.class)));
    }
}