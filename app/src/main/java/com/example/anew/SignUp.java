package com.example.anew;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail,signupPassword,username;
    Voice speech;
    String UserName;
    private Button signupButton;
    private TextView loginRedirect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        speech=new Voice(this);
        username=findViewById(R.id.signupUsername);
        auth=FirebaseAuth.getInstance();
        signupEmail=findViewById(R.id.signupEmail);
        signupPassword=findViewById(R.id.signupPassword);
        signupButton=findViewById(R.id.btnSignup);
        loginRedirect=findViewById(R.id.loginRedirectText);
        signupButton.setOnClickListener(v -> {
            String user=signupEmail.getText().toString().trim();
            String pass=signupPassword.getText().toString().trim();
            UserName=username.getText().toString();

            if(user.isEmpty()){
                signupEmail.setError("Email cannot be empty");
            }
            if(pass.isEmpty())
            {
                signupPassword.setError("Password cannot be empty");
            }else{
                auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(SignUp.this,"SignUp Successful",Toast.LENGTH_LONG).show();
                        SharedPreferences signupPref=getSharedPreferences("signup",MODE_PRIVATE);
                        SharedPreferences.Editor editor=signupPref.edit();
                        editor.putString("username",UserName);
                        editor.putString("email",user);
                        editor.putString("password",pass);
                        editor.apply();

                        speech.speak("SignUp Successful");
                        startActivity(new Intent(SignUp.this, Login.class));
                    }else{
                        Toast.makeText(SignUp.this,"SignUp Failed"+ Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        loginRedirect.setOnClickListener(v -> startActivity(new Intent(SignUp.this,Login.class)));
    }
}