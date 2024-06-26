package com.example.anew;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.Locale;

public class ScannerActivity extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 100;
    private static final int CREATE_DOCUMENT_REQUEST_CODE = 101;
    private long lastVolumeUpClickTime = 0;
    private int volumeUpClickCount = 0;
    Boolean login;
    ImageView captureIV;
    Boolean speechRecognition,speechOutput;
    int fontSize;
    TextView resultTV;
    Voice speech;
    String text_result;
    Button saveBtn,uploadCloud;
    String currentDateTime="Apr 3 2024";
    Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE=1;
    private static final int PERMISSION_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        speech=new Voice(this);
        captureIV=findViewById(R.id.newImgview);
        resultTV=findViewById(R.id.newTxtview);
        saveBtn=findViewById(R.id.btnDetect);
        uploadCloud=findViewById(R.id.uploadCloud);
        saveBtn.setOnClickListener(v->saveToText());
        uploadCloud.setOnClickListener(v->checkCredentialAndUpload());
        if(checkPermission()){
            captureImage();
        }else{
            requestPermission();
        }
        SharedPreferences scannerSetting=getSharedPreferences("settings",MODE_PRIVATE);
        speechRecognition=scannerSetting.getBoolean("speechRecognitionFlag",true);
        speechOutput=scannerSetting.getBoolean("speechOutputFlag",true);
        SharedPreferences scannerPref=getSharedPreferences("preferences",MODE_PRIVATE);
        fontSize=scannerPref.getInt("textSize",25);
        resultTV.setTextSize(fontSize);

    }

    private void checkCredentialAndUpload() {
        SharedPreferences loginPRef=getSharedPreferences("login",MODE_PRIVATE);
        login=loginPRef.getBoolean("loginState",false);
        if(!login){
            startActivity(new Intent(ScannerActivity.this,Login.class));
        }
        else{
            saveData();
        }
    }

    private void showSaveOptions(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.save_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId()==R.id.saveText)
            {
                saveData();
            }
            return false;
        });
        popupMenu.show();
    }
    private boolean checkPermission(){
        int cameraPermission= ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        return  cameraPermission== PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        int PERMISSION_CODE=200;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraPermission) {
                    speech.speak( "Permission Granted..");
                    captureImage();
                } else {
                    speech.speak("Permission Denied");
                }
            }
        }
    }


    private void handleVoiceCommand(String s) {
        String ns=s.toLowerCase();
        switch(ns){
            case "read text again":
                speech.speak(resultTV.getText().toString());

            case"go back":
                speech.speak("Going Back");
                gotoHome();
                break;
            case"save as text":
                speech.speak("Saving as Text Document");
                saveToText();
                break;
            default:
                speech.speak("Command not recognized");
                break;
        }
    }

    private void saveData(){

        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Document");
        AlertDialog.Builder builder=new AlertDialog.Builder(ScannerActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog=builder.create();
        dialog.show();

        storageReference.putBytes(resultTV.getText().toString().getBytes()).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
            while(!uriTask.isComplete());
            Uri urlDocument=uriTask.getResult();
            uploadData();
            dialog.dismiss();
        }).addOnFailureListener(e -> dialog.dismiss());
    }

    private void uploadData() {
        String document_name="document";
        String date_time=currentDateTime;
        String data=resultTV.getText().toString();
        String user="prajapatianil2003@gmail.com";

        Data dataClass=new Data(document_name,date_time,data,user);

        FirebaseDatabase.getInstance().getReference("Scanned_Document").child(document_name).setValue(dataClass).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(ScannerActivity.this,"Saved",Toast.LENGTH_LONG).show();
                finish();
            }
        }).addOnFailureListener(e -> Toast.makeText(ScannerActivity.this,e.getMessage(),Toast.LENGTH_LONG).show());
    }

    private void saveToText() {
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

    private void gotoHome() {
        Intent goBack=  new Intent(ScannerActivity.this,MainActivity.class);
        startActivity(goBack);
    }

    @Override
    protected void onResume() {
        speech.stop();
        super.onResume();
    }

    @Override
    protected void onStop() {
        speech.stop();
        super.onStop();
    }

    private void startSpeechRecognition() {
        speech.speak("Speak Command");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening...");

        //noinspection deprecation
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

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
    private void captureImage() {
        Intent takePicture=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePicture,REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            assert data != null;
            Bundle extras =data.getExtras();
            assert extras != null;
            imageBitmap=(Bitmap) extras.get("data");
            captureIV.setImageBitmap(imageBitmap);
            detectText();
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

    private void detectText() {
        InputImage image=InputImage.fromBitmap(imageBitmap,0);
        TextRecognizer recognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result= recognizer.process(image).addOnSuccessListener(text -> {
            StringBuilder result1 =new StringBuilder();
            for(Text.TextBlock block:text.getTextBlocks()){
                String blockText=block.getText();
                Point[] blockCornerPoint=block.getCornerPoints();
                Rect blockFrame=block.getBoundingBox();
                for(Text.Line line:block.getLines()){
                    String lineTxt=line.getText();
                    Point[] lineCornerPoint=line.getCornerPoints();
                    Rect lineRect=line.getBoundingBox();
                    for(Text.Element element:line.getElements()){
                        String elementText=element.getText();
                        result1.append(elementText);
                    }
                    resultTV.append(blockText);
                    text_result=blockText;
                }
            }
        }).addOnFailureListener(e -> speech.speak("Failed to detect text from image"));
        if(speechOutput){
            if(text_result!=null)
                speech.speak(text_result);
            else
                speech.speak("No text detected");
        }
    }
}