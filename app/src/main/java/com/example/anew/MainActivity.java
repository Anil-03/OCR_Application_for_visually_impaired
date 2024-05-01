package com.example.anew;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    Button captureImg,uploadImg,uploadDoc;
    private TextToSpeech textToSpeech;
    private long lastVolumeUpClickTime = 0;
    private int volumeUpClickCount = 0;
    private static final int SPEECH_REQUEST_CODE = 1001;
    private boolean welcomeMessageSpoken = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textToSpeech = new TextToSpeech(this, this);
        speak("Welcome to OCR Application");
        captureImg = findViewById(R.id.captureImg);
        uploadImg = findViewById(R.id.uploadImg);
        uploadDoc=findViewById(R.id.uploadDoc);
        captureImg.setOnClickListener(v -> gotoCapture());
        uploadImg.setOnClickListener(v -> gotoUpload());
        uploadDoc.setOnClickListener(v-> gotoDocumentUpload());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.help){
            gotoHelp();
            return true;
        }
        else if(item.getItemId()==R.id.preferences)
        {
            gotoPreferences();
            return true;
        } else if (item.getItemId()==R.id.settings) {
            gotoSettings();
            return true;
        }
        else
            return false;
        
    }

    private void gotoSettings() {
    }

    private void gotoPreferences() {

    }

    private void gotoHelp() {
        Intent help=new Intent(MainActivity.this, Help.class);
        startActivity(help);
    }

    private void gotoDocumentUpload() {
        Intent i=new Intent(MainActivity.this,DocumentUpload.class);
        startActivity(i);
    }

    private void gotoUpload() {
        Intent i=new Intent(MainActivity.this, Upload.class);
        startActivity(i);
    }

    private void gotoCapture() {
        Intent i=new Intent(MainActivity.this,ScannerActivity.class);
        startActivity(i);
    }

    private void speak(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void startSpeechRecognition() {
        textToSpeech.stop();
        speak("Speak Command");
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    handleVoiceCommand(result.get(0));
                }
            }
        }
    }


    private void handleVoiceCommand(String s) {
        String ns = s.toLowerCase();
        switch (ns){
            case "open camera":
                speak(ns);
                gotoCapture();
                break;
            case "upload image":
                speak(ns);
                gotoUpload();
                break;
            case "upload document":
                gotoDocumentUpload();
                break;
            case"help":
                speak("Opening Help");
                gotoHelp();
                break;
            case"how to use app":
                speak("You can use voice commands to navigate through the app Just say help to know about available commands");
            default:
                speak("Command not recognized");
                startSpeechRecognition();
                break;
        }
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
                    speak("Welcome to OCR Application, Speak the command to perform action Double tap on the volume up button to start speech recognition and speak help to know about available commands");
                    welcomeMessageSpoken = true;
                }
            }
        } else {
            speak("TextToSpeech initialization failed");
        }
    }

    @Override
    protected void onPause() {
        textToSpeech.stop();
        super.onPause();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}