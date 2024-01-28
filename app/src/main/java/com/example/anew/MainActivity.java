package com.example.anew;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final Button captureImg = findViewById(R.id.captureImg);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captureImg.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this,ScannerActivity.class);
            startActivity(i);
        });
    }
}