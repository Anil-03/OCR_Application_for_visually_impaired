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

public class upload extends AppCompatActivity {
    Button save;
    private static final int PICK_IMAGE_REQUEST = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        save=findViewById(R.id.uploadDetectBtn);
        openFilePicker();
        //save.setOnClickListener(v -> );
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                processAndDisplayImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processAndDisplayImage(Bitmap imageBitmap) {
        // Display the selected image in your ImageView
        ImageView uploadIV= findViewById(R.id.uploadIV);
        uploadIV.setImageBitmap(imageBitmap);

        // Perform text recognition on the selected image
        detectText(imageBitmap);
    }

    private void detectText(Bitmap imageBitmap) {
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        Task<Text> result = recognizer.process(image)
                .addOnSuccessListener(text -> {
                    StringBuilder resultText = new StringBuilder();
                    for (Text.TextBlock block : text.getTextBlocks()) {
                        String blockText = block.getText();
                        resultText.append(blockText).append("\n");
                    }

                    // Display the recognized text in your TextView
                    TextView resultTextView = findViewById(R.id.uploadTV);
                    resultTextView.setText(resultText.toString());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to detect text from image", Toast.LENGTH_SHORT).show());
    }

}