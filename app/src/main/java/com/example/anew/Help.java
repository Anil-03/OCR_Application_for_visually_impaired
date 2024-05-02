package com.example.anew;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class Help extends AppCompatActivity {
    private static final int SPEECH_REQUEST_CODE = 100;
    private long lastVolumeUpClickTime = 0;
    private int volumeUpClickCount = 0;
    ArrayList<String> commands= new ArrayList<>();

    String oc="Open the device camera to capture an image";
    String ui="Open image picker interface to select an image";
    String ud="Open document picker interface to select a document";
    String ra="Read the text again";
    String st="Save the text as text document";
    String sp="Save the text as pdf";
    String gb="Go back to home page";
    Voice speech;
    Boolean speechRecognition,speechOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        speech=new Voice(this);
        new Handler().postDelayed(()-> speech.speak("Say list of commands to know available commands"),1000);
        commands.add("1.Open Camera");
        commands.add("2.Upload Image");
        commands.add("3.Upload Document");
        commands.add("4.Read Text Again");
        commands.add("5.Save as Text");
        commands.add("6.Save as PDF");
        commands.add("7.Go back");
        SharedPreferences helpPref=getSharedPreferences("settings",MODE_PRIVATE);
        speechRecognition=helpPref.getBoolean("speechRecognitionFlag",true);
        speechOutput=helpPref.getBoolean("speechOutputFlag",true);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(speechRecognition){
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
        }
        return super.onKeyUp(keyCode, event);
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
                for (String c:commands) {
                    speech.speak(c);
                    for(int i=0;i<100;i++)
                    {
                        i++;
                    }
                }
                break;
            default:
                speech.speak("Command not defined Say list of commands to know about available commands");
                break;
        }
    }
}