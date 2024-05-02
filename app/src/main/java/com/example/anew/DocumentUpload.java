package com.example.anew;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class DocumentUpload extends AppCompatActivity {

    private static final int PICK_DOCUMENT_REQ_CODE = 100;
    Voice speech;
    Button save;
    TextView result;
    private final int CREATE_DOCUMENT_REQUEST_CODE=1;
    int font_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);
        speech=new Voice(this);
        save=findViewById(R.id.btnSaveDoc);
        result=findViewById(R.id.txtResultDoc);
        save.setOnClickListener(this::showSaveOptions);
        getDocument();
    }

    private void showSaveOptions(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.save_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId()==R.id.saveText)
            {
                saveToDoc(result.getText().toString());

            }

            return false;
        });
        popupMenu.show();
    }

    private void getDocument() {
        Intent getDoc=new Intent(Intent.ACTION_GET_CONTENT);
        getDoc.setType("*/*");
        startActivityForResult(getDoc,PICK_DOCUMENT_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_DOCUMENT_REQ_CODE & resultCode==RESULT_OK){
            if(data!=null){
                Uri selectedDocUri=data.getData();
                if(selectedDocUri!=null){
                    String mimeType=getContentResolver().getType(selectedDocUri);
                    if(mimeType!=null && mimeType.startsWith("text")){
                        readTextFromDocument(selectedDocUri);
                    }

                }
            }
        }
        if (requestCode == CREATE_DOCUMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                TextView resultTV=findViewById(R.id.uploadTV);
                writeTextToFile(uri,resultTV.getText().toString());
            }
        }

    }
    private void readTextFromDocument(Uri selectedDocUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedDocUri);
            if (inputStream != null) {
                StringBuilder stringBuilder = new StringBuilder();
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) {
                    stringBuilder.append(scanner.nextLine());
                    stringBuilder.append("\n");
                }
                String documentContent = stringBuilder.toString();
                handleRecognizedText(documentContent);
            }
        } catch (IOException e) {
            //e.printStackTrace();
            speech.speak("Unable to read the file");
        }
    }

    private void handleRecognizedText(String documentContent) {
        result.setText(documentContent);
        speech.speak(documentContent);
    }


    private void writeTextToFile(Uri uri, String text) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                writer.write(text);
                writer.close();
                outputStream.close();
                Toast.makeText(this, "Text saved to document", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            //e.printStackTrace();
            Toast.makeText(this, "Error saving text to document", Toast.LENGTH_SHORT).show();
        }
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
}