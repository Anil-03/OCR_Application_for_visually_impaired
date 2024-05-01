package com.example.anew;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import java.util.ArrayList;
import java.util.Locale;

public class Upload extends AppCompatActivity {
    private static final int SPEECH_REQUEST_CODE =100 ;
    private static final int CREATE_DOCUMENT_REQUEST_CODE = 1;
    Button save;
    private long lastVolumeUpClickTime = 0;
    private int volumeUpClickCount = 0;
    Voice speech;
    String OCR_Result;
    TextView resultTextView;
    private static final int PICK_IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        speech = new Voice(this);
        save = findViewById(R.id.uploadDetectBtn);
        save.setOnClickListener(this::showSaveOptions);
        resultTextView = findViewById(R.id.uploadTV);
        openFilePicker();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            long currentTime = System.currentTimeMillis();
            // Check if it's a double click within a short duration
            if (currentTime - lastVolumeUpClickTime < 500) {
                volumeUpClickCount++;
                // Perform speech recognition on double click
                if (volumeUpClickCount == 2) {
                    startSpeechRecognition();
                    volumeUpClickCount = 0; // Reset click count
                }
            } else {
                volumeUpClickCount = 1; // Reset click count if it's a single click
            }
            lastVolumeUpClickTime = currentTime;
            return true; // Consume the event
        }
        return super.onKeyUp(keyCode, event);
    }


    private void showSaveOptions(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.save_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId()==R.id.saveText)
            {
                saveToDoc(resultTextView.getText().toString());

            } else if (item.getItemId()==R.id.savePDF) {
                saveAsPDF(resultTextView.getText().toString());
            }

            return false;
        });
        popupMenu.show();
    }

    private void saveAsPDF(String string) {
    }

    private void saveToDoc(String string) {

        try {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TITLE, "ocr_document.txt");

            startActivityForResult(intent, CREATE_DOCUMENT_REQUEST_CODE);
        }catch (Exception e)
        {
            String errorMsg=e.toString();
            speech.speak("Error saving document");
            speech.speak(errorMsg);
        }
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
                speech.speak("Unknown exception occurred");
            }
        }
        if (requestCode == SPEECH_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    handleVoiceCommand(result.get(0));
                }
            }
        }
    }

    private void startSpeechRecognition() {
        speech.stop();
        speech.speak("Speak Command");
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...");

            //noinspection deprecation
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
            // Now myObject is initialized after 2 seconds
        }, 1000);
    }

    private void handleVoiceCommand(String s) {
        String ns=s.toLowerCase();
        switch (ns){
            case"read text again":
                speech.speak("Reading text again");
                speech.speak(resultTextView.getText().toString());
                break;
            case"go back":
                speech.speak("Going Back");
                gotoHome();
                break;
            default:
                speech.speak("Command not recognized");
                break;
        }
    }

    private void gotoHome() {
        Intent goBack=new Intent(Upload.this,MainActivity.class);
        startActivity(goBack);
    }

    private void processAndDisplayImage(Bitmap imageBitmap) {
        // Display the selected image in your ImageView
        ImageView uploadIV = findViewById(R.id.uploadIV);
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

                    resultTextView.setText(resultText.toString());
                    speech.speak(resultText.toString());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to detect text from image", Toast.LENGTH_SHORT).show());
        OCR_Result = resultTextView.getText().toString();

    }

    @Override
    protected void onPause() {
        speech.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        speech.stop();
        super.onDestroy();
    }
}