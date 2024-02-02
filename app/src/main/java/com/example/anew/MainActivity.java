package com.example.anew;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    Button captureImg,uploadImg;
    private TextToSpeech textToSpeech;
    private static final int SPEECH_REQUEST_CODE = 1001;
    private boolean welcomeMessageSpoken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textToSpeech = new TextToSpeech(this, this);
        speak("Welcome to OCR Application");
        startSpeechRecognition();
        captureImg = findViewById(R.id.captureImg);
        uploadImg = findViewById(R.id.uploadImg);
        captureImg.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this,ScannerActivity.class);
            startActivity(i);
        });
        uploadImg.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this,upload.class);
            startActivity(i);
        });
    }

    private void speak(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...");

        //noinspection deprecation
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && result.size() > 0) {
                    handleVoiceCommand(result.get(0));
                }
            }
        }
    }

    private void handleVoiceCommand(String s) {
        Toast.makeText(this, "Voice command: " + s, Toast.LENGTH_SHORT).show();
    }

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int langResult = textToSpeech.setLanguage(Locale.US);
            if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                    langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
            } else {
                // Speak the welcome message only if it hasn't been spoken before
                float speechRate = 0.7f;
                textToSpeech.setSpeechRate(speechRate);
                if (!welcomeMessageSpoken) {
                    speak("Welcome to OCR Application, Speak the command to perform actions");
                    welcomeMessageSpoken = true;
                    // Start speech recognition after the welcome message
                    startSpeechRecognition();
                }
            }
        } else {
            Toast.makeText(this, "TextToSpeech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}