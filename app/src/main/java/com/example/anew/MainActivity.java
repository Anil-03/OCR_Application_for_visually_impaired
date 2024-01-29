package com.example.anew;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button captureImg,uploadImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captureImg = findViewById(R.id.captureImg);
        uploadImg = findViewById(R.id.uploadImg);
        captureImg.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this,ScannerActivity.class);
            startActivity(i);
        });
        uploadImg.setOnClickListener(v -> {

        });
    }
}