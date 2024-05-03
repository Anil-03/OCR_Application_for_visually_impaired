package com.example.anew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {
    Spinner eng_spinner, lang_spinner;

    Boolean getSpeechRecognitionState,speechOutputState;
    Voice speech;
    Button help,feedback,logout,saveSettings;
    Boolean speechRecognition=true;
    Boolean speechOutput=true;
    String engine="Firebase";
    String language="English";
    private FirebaseAuth auth;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch speech_recognition;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch speech_output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        speech = new Voice(this);
        help=findViewById(R.id.help);
        auth=FirebaseAuth.getInstance();

        saveSettings=findViewById(R.id.saveSettings);
        eng_spinner = findViewById(R.id.engine_spinner);
        lang_spinner = findViewById(R.id.language_spinner);
        speech_recognition = findViewById(R.id.speech_recognition);
        speech_output = findViewById(R.id.speechOutput);

        ArrayList<String> engineList = new ArrayList<>();
        engineList.add("Firebase ML Kit");
        ArrayAdapter<String> eng_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, engineList);
        eng_adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        eng_spinner.setAdapter(eng_adapter);
        eng_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                speech.speak("You have selected" + item);
                engine=item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayList<String> languageList = new ArrayList<>();
        languageList.add("English");
        ArrayAdapter<String> lang_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languageList);
        lang_adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        lang_spinner.setAdapter(lang_adapter);

        lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String item=adapterView.getItemAtPosition(position).toString();
                speech.speak("You have selected"+item);
                language=item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        speech_recognition.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(speech_recognition.isChecked()){
                speech.speak("Speech Recognition Enabled");
                speechRecognition=true;
            }else{
                speech.speak("Speech Recognition Disabled");
                speechRecognition=false;
            }

        });

        speech_output.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(speech_output.isChecked())
            {
                speech.speak("Speech Output Enabled");
                speechOutput=true;
            }else{
                speech.speak("Speech Output is Disabled");
                speechOutput=false;
            }

        });
        help.setOnClickListener(v -> {
            Intent help=new Intent(Settings.this, Help.class);
            startActivity(help);
        });

        saveSettings.setOnClickListener(v-> {
            storePreference();
            Toast.makeText(this,"Settings saved successfully",Toast.LENGTH_LONG).show();
        });
        stateChange(speechOutput,speechRecognition);
    }

    private void stateChange(Boolean so,Boolean sr) {
        if(so)
        {
            speech_output.setChecked(true);
        }
        if(sr){
            speech_recognition.setChecked(true);
        }
    }

    private void storePreference() {
        SharedPreferences settingsPref=getSharedPreferences("settings",MODE_PRIVATE);
        SharedPreferences.Editor editor=settingsPref.edit();
        editor.putBoolean("speechRecognitionFlag",speechRecognition);
        editor.putBoolean("speechOutputFlag",speechOutput);
        editor.putString("engine",engine);
        editor.putString("language",language);
        editor.apply();
    }
}