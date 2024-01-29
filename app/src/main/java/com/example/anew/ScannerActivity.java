package com.example.anew;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class ScannerActivity extends AppCompatActivity {

    ImageView captureIV;
    TextView resultTV;
    Button snapBtn;
    Button detectBtn;
    Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE=1;
    private static final int PERMISSION_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        captureIV=findViewById(R.id.newImgview);
        resultTV=findViewById(R.id.newTxtview);
        snapBtn=findViewById(R.id.btnSnap);
        detectBtn=findViewById(R.id.btnDetect);
        snapBtn.setOnClickListener(v -> {
            if(checkPermission()){
                captureImage();
            }else{
                requestPermission();
            }
        });
        detectBtn.setOnClickListener(v -> detectText());
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
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                    captureImage();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(ScannerActivity.this,"Failed to detect text from image",Toast.LENGTH_SHORT).show());
    }
}