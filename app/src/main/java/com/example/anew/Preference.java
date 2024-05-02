package com.example.anew;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Preference extends AppCompatActivity {

    SeekBar size,pitch,volume;

    String theme;
    AudioManager manager;
    TextView demo;
    Button savePreference;
    int fontSize,pitchRate,speechVolume;
    int maxVolume,currentVolume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        size=findViewById(R.id.text_size);
        manager=(AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume=manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume=manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        pitch=findViewById(R.id.pitch_rate);
        volume=findViewById(R.id.volume);
        demo=findViewById(R.id.demoTxtView);
        savePreference=findViewById(R.id.savePreference);
        size.setProgress(25);
        size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fontSize=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                demo.setTextSize(fontSize);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                demo.setTextSize(fontSize);
            }
        });

        pitch.setProgress(70);
        pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pitchRate=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

       volume.setMax(maxVolume);
       volume.setProgress(currentVolume);
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speechVolume=progress;
                manager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        savePreference.setOnClickListener(v->{
            SharedPreferences preferences=getSharedPreferences("preferences",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putInt("volume",speechVolume);
            editor.putInt("textSize",fontSize);
            editor.putString("theme",theme);
            editor.putInt("pitchRate",pitchRate);
            editor.apply();
            Toast.makeText(this,"Preferences saved successfully",Toast.LENGTH_SHORT).show();
        });

    }
}