package com.example.anew;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button captureImg,uploadImg;
    private static final int PICK_IMAGE_REQUEST = 2;
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
            Intent intent=new Intent(MainActivity.this, upload.class);
            startActivity(intent);
        });
    }


}