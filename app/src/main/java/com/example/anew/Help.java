package com.example.anew;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class Help extends AppCompatActivity {
    private static final int SPEECH_REQUEST_CODE = 100;
    String list_of_commands="1.Open Camera 2.Upload Image 3.Upload Document 4.read text again 5.Save as Text Document 6.Save as PDF 7.Go back";
    String oc="Open the device camera to capture an image";
    String ui="Open image picker interface to select an image";
    String ud="Open document picker interface to select a document";
    String ra="Read the text again";
    String st="Save the text as text document";
    String sp="Save the text as pdf";
    String gb="Go back to home page";
    Voice speech;
    GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        speech=new Voice(this);
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                // Handle double tap
                startSpeechRecognition();
                return super.onDoubleTap(e);
            }
        });
        speech.speak("Say list of commands to know available commands");
    }
    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...");

        //noinspection deprecation
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
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
        String ns=s.toLowerCase();
        switch(ns){
            case "open camera":
                speech.speak(oc);
                break;
            case"upload image":
                speech.speak(ui);
                break;
            case"upload document":
                speech.speak(ud);
                break;
            case"read text again":
                speech.speak(ra);
                break;
            case"save as text document":
                speech.speak(st);
                break;
            case"save as pdf":
                speech.speak(sp);
                break;
            case"go back":
                speech.speak(gb);
                break;
            case"list of commands":
                speech.speak(list_of_commands);
                break;

            default:
                speech.speak("Command not defined Say list of commands to know about available commands");
                break;
        }
    }
}