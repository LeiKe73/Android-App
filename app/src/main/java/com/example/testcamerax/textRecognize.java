package com.example.testcamerax;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class textRecognize extends AppCompatActivity {

    private AppDatabase db;
    private  GraphicOverlay mGraphicOverlay;

    //声明位置在onCreate之前
    //初始化一般放在回调函数内，这里特殊
    private final ActivityResultLauncher<String> mResquestLauncher = //String类型对应请求权限
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),//
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean isGranted) {
                            if(isGranted) {
                                performAction();
                                //filescanner();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "You denied Using Camera",
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // activity_camera.xml 里要有 preview_view 等控件

        ///权限申请
        //申请在真正需要权限的时候之前
        if(ContextCompat.checkSelfPermission( this,
                android.Manifest.permission.CAMERA)==
                PackageManager.PERMISSION_GRANTED){
            performAction();
            //filescanner();
        }else if(shouldShowRequestPermissionRationale(
                android.Manifest.permission.CAMERA
        )){
            Toast.makeText(this,"This App needs CAMERA",Toast.LENGTH_LONG).show();
        }else{
            //真正申请权限
            mResquestLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void performAction(){
        //显示相机预览
        PreviewView previewView = findViewById(R.id.preview_view);
        LifecycleCameraController controller =
                new LifecycleCameraController(this);
        controller.bindToLifecycle(this);
        controller.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
        previewView.setController(controller);

        //使用中文识别
        TextRecognizer recognizer =
                TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());

        mGraphicOverlay = findViewById(R.id.graphic_overlay);

        //点击按钮拍照
        Button btn = findViewById(R.id.cameraX);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                       // !!! 真正有差异的代码从下一行开始

                ContentResolver resolver = getContentResolver();
                Uri imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                ContentValues newImageDetails = new ContentValues();
                newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, makeFileName());
                newImageDetails.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                newImageDetails.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/Camera");

                ImageCapture.OutputFileOptions options =
                        new ImageCapture.OutputFileOptions.Builder(resolver, imageCollection, newImageDetails)
                                .build();
                controller.takePicture(
                        options,
                        ContextCompat.getMainExecutor(textRecognize.this),
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(ImageCapture.@NonNull OutputFileResults outputFileResults) {
                                Uri savedUri = outputFileResults.getSavedUri();
                                if (savedUri != null) {
                                    Log.d("拍照", "保存成功: " + savedUri);
                                    ImageView imageView = findViewById(R.id.photo_result);
                                    imageView.setImageURI(savedUri);
                                    try {
                                        runTextRecognition(savedUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.e("拍照", "保存失败，URI为空");
                                }
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                Log.e("拍照", "拍照失败: " + exception.getMessage(), exception);
                            }
                        }
                );

            }
        });
    }

    private void runTextRecognition(Uri imageUri) throws IOException {
        Log.d("MLKit", "开始识别: " + imageUri);
        InputImage image = InputImage.fromFilePath(textRecognize.this, imageUri);
        TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build())
                .process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text texts) {

//                        String recognizedText = texts.getText();
//                        String imgPath = getRealPathFromUri(textRecognize.this,imageUri); //
//                        long timestamp = System.currentTimeMillis();
//
//                        OcrRecord record = new OcrRecord(imgPath, recognizedText, timestamp);
//                        AppDatabase.getInstance(textRecognize.this).ocrRecordDao().insert(record);

                        String recognizedText = texts.getText();
                        //直接将Uri存储为String,就不必那么麻烦还要把Uri求文件路径了
                        String imgPath = imageUri.toString();
                        long timestamp = System.currentTimeMillis();

                        OcrRecord record = new OcrRecord(imgPath, recognizedText, timestamp);

                        try {
                            AppDatabase.getInstance(textRecognize.this).ocrRecordDao().insert(record);
                            Toast.makeText(textRecognize.this, "插入数据库成功", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(textRecognize.this, "插入数据库失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        //processTextRecognitionResult(texts);
                        // 显示识别结果到界面
                        //TextView resultView = findViewById(R.id.detail_text);
                        //resultView.setText(texts.getText());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("MLKit", "识别失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    private void processTextRecognitionResult(Text texts) {
        Log.d("MLKit", "识别到文本块数量: " + texts.getTextBlocks().size());
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.isEmpty()) {
            Toast.makeText(this,"No text found",Toast.LENGTH_LONG).show();
            return;
        }
        mGraphicOverlay.clear();
        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    mGraphicOverlay.add(textGraphic);

                }
            }
        }
    }

    private String makeFileName(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf =  new SimpleDateFormat("yyMMdd_HHmmss_SSS");
        return "IMG_" + sdf.format(date)+".jpg";
    }

}