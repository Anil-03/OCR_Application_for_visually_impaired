package com.example.anew;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class Voice implements TextToSpeech.OnInitListener{
    private final TextToSpeech textToSpeech;
    private boolean isReady = false;

    public Voice(Context context) {
        textToSpeech = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language for text-to-speech
            int result = textToSpeech.setLanguage(Locale.US);
            // Language data is missing or the language is not supported
            isReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED;
        } else {
            // Initialization failed
            isReady = false;
        }
    }

    public void speak(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void stop() {
        if (isReady) {
            // Stop speaking
            textToSpeech.stop();
        }
    }
    public boolean isSpeaking() {
        return false;
    }
    public boolean isReady() {
        return isReady;
    }
}
