package com.example.testcamerax;

import static com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
<<<<<<< HEAD

=======
import android.media.MediaScannerConnection;
>>>>>>> 99b6327acc0f8a2c76738bc2a23024024dbd19bb
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
<<<<<<< HEAD

import android.widget.TextView;
=======
>>>>>>> 99b6327acc0f8a2c76738bc2a23024024dbd19bb
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
<<<<<<< HEAD

import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;

=======
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
>>>>>>> 99b6327acc0f8a2c76738bc2a23024024dbd19bb
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
<<<<<<< HEAD

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
=======
>>>>>>> 99b6327acc0f8a2c76738bc2a23024024dbd19bb
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;
<<<<<<< HEAD
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;

=======
>>>>>>> 99b6327acc0f8a2c76738bc2a23024024dbd19bb
import org.jspecify.annotations.NonNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> 99b6327acc0f8a2c76738bc2a23024024dbd19bb


public class MainActivity extends AppCompatActivity {
    private static final String FULL_MODE = "FULL";
    private static final String BASE_MODE = "BASE";
    private static final String BASE_MODE_WITH_FILTER = "BASE_WITH_FILTER";
<<<<<<< HEAD
    // 在 Activity 类中添加以下成员变量
    private  GraphicOverlay mGraphicOverlay;


=======
    private String selectedMode = FULL_MODE;
>>>>>>> 99b6327acc0f8a2c76738bc2a23024024dbd19bb

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
                                filescanner();
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ///权限申请
        //申请在真正需要权限的时候之前
        if(ContextCompat.checkSelfPermission( this,
                Manifest.permission.CAMERA)==
        PackageManager.PERMISSION_GRANTED){
            performAction();
            filescanner();
        }else if(shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA
        )){
            Toast.makeText(this,"This App needs CAMERA",Toast.LENGTH_LONG).show();
        }else{
            //真正申请权限
            mResquestLauncher.launch(Manifest.permission.CAMERA);
        }
    }


    private void filescanner(){

        //调用文件扫描
        GmsDocumentScannerOptions.Builder builder =
                new GmsDocumentScannerOptions.Builder()
                        .setGalleryImportAllowed(true)
                        .setPageLimit(2)
                        .setResultFormats(RESULT_FORMAT_JPEG);// ,RESULT_FORMAT_PDF
        //模式切换
<<<<<<< HEAD
        String selectedMode = FULL_MODE;
=======
>>>>>>> 99b6327acc0f8a2c76738bc2a23024024dbd19bb
        switch (selectedMode) {
            case FULL_MODE:
                builder.setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL);
                break;
            case BASE_MODE:
                builder.setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_BASE);
                break;
            case BASE_MODE_WITH_FILTER:
                builder.setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_BASE_WITH_FILTER);
                break;
            default:
                Log.e("MainActivity", "Unknown selectedMode: " + selectedMode);
        }
        GmsDocumentScannerOptions options = builder.build();

        GmsDocumentScanner scanner = GmsDocumentScanning.getClient(options);
        ActivityResultLauncher<IntentSenderRequest> scannerLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartIntentSenderForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                GmsDocumentScanningResult ScanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.getData());
                                for (GmsDocumentScanningResult.Page page : ScanResult.getPages()) {
                                    //完成后保存图片为Uri
                                    Uri imageUri = page.getImageUri();

                                    Toast.makeText(
                                            MainActivity.this,
<<<<<<< HEAD
                                            "文件Uri:" + imageUri, // 显示完整路径
=======
                                            "文件Uri:" + imageUri.toString(), // 显示完整路径
>>>>>>> 99b6327acc0f8a2c76738bc2a23024024dbd19bb
                                            Toast.LENGTH_LONG
                                    ).show();

                                    saveImageToGallery(imageUri); // 迁移到相册

                                    ImageView imageView = findViewById(R.id.photo_result);
                                    imageView.setImageURI(imageUri);
                                }

                                GmsDocumentScanningResult.Pdf pdf = ScanResult.getPdf();
                                if (pdf != null) {
                                    Uri pdfUri = pdf.getUri();
                                    int pageCount = pdf.getPageCount();
                                }
                            }
                        });

        //点击按钮跳转到 文档扫描器
        Button btn = findViewById(R.id.scanner);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scanner.getStartScanIntent(MainActivity.this)
                        .addOnSuccessListener(intentSender ->
                                scannerLauncher.launch(new IntentSenderRequest.Builder(intentSender).build()))
                        .addOnFailureListener(e -> {
                            Log.e("DocScan", "扫描初始化失败", e);
                            Toast.makeText(MainActivity.this, "扫描功能不可用：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

        });
    }

    //文件迁移
    private Uri saveImageToGallery(Uri tempImageUri) {
        ContentResolver resolver = getContentResolver();

        // 1. 创建MediaStore记录
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, makeFileName());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/Camera"); // 存储到Pictures/Scans

        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if(imageUri != null){
        // 2. 复制临时文件到目标Uri
            try (InputStream is = resolver.openInputStream(tempImageUri);
                  OutputStream os = resolver.openOutputStream(imageUri)) {
                    byte[] buffer = new byte[1024];
                    int len;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
            } catch (IOException e) {
                  e.printStackTrace();
            }
        }
        return imageUri;
    }
    private void performAction(){
        //显示相机预览
        PreviewView previewView = findViewById(R.id.preview_view);
        LifecycleCameraController controller =
                new LifecycleCameraController(this);
        controller.bindToLifecycle(this);
        controller.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
        previewView.setController(controller);

<<<<<<< HEAD
        //使用中文识别
        TextRecognizer recognizer =
          TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());

        mGraphicOverlay = findViewById(R.id.graphic_overlay);

=======
>>>>>>> 99b6327acc0f8a2c76738bc2a23024024dbd19bb
        //点击按钮拍照
        Button btn = findViewById(R.id.cameraX);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                       // !!! 真正有差异的代码从下一行开始

                ContentResolver resolver = getContentResolver();
                Uri imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                ContentValues newImageDetails = new ContentValues();
                newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, makeFileName());
<<<<<<< HEAD
                newImageDetails.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                newImageDetails.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/Camera");


=======
                Uri newImageUri = resolver.insert(imageCollection, newImageDetails);// 这一行代码可选，得到新图像的Uri可以让你在拍照成功后使用newImageUri变量来访问图像


                //提示信息
                if (newImageUri == null) {
                    Toast.makeText(MainActivity.this, "创建文件失败", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Toast.makeText(
                            MainActivity.this,
                            "文件Uri:" + newImageUri.toString(), // 显示完整路径
                            Toast.LENGTH_LONG
                    ).show();
                }
>>>>>>> 99b6327acc0f8a2c76738bc2a23024024dbd19bb

                ImageCapture.OutputFileOptions options =
                        new ImageCapture.OutputFileOptions.Builder(resolver, imageCollection, newImageDetails)
                                .build();
<<<<<<< HEAD
                controller.takePicture(
                        new ImageCapture.OutputFileOptions.Builder(resolver, imageCollection, newImageDetails).build(),
                        ContextCompat.getMainExecutor(MainActivity.this),
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
    // 文字识别方法
    private void runTextRecognition(Uri imageUri) throws IOException {
            Log.d("MLKit", "开始识别: " + imageUri);
            InputImage image = InputImage.fromFilePath(MainActivity.this, imageUri);
            TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build())
                    .process(image)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text texts) {
                            processTextRecognitionResult(texts);
                            // 显示识别结果到界面
                            TextView resultView = findViewById(R.id.get_Text);
                            resultView.setText(texts.getText());
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

    // 处理识别结果（绘制文本框）
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
=======
                controller.takePicture(options,
                            ContextCompat.getMainExecutor(v.getContext()),
                            new ImageCapture.OnImageSavedCallback() {
                                @Override
                                public void onImageSaved(ImageCapture.@NonNull OutputFileResults outputFileResults) {
                                    // 使用实际保存的 Uri
                                    Uri savedUri = outputFileResults.getSavedUri();
                                    if (savedUri != null) {
                                        // 触发媒体扫描
                                        MediaScannerConnection.scanFile(
                                                MainActivity.this,
                                                new String[]{savedUri.getPath()},
                                                new String[]{"image/jpeg"},
                                                (path, uri) -> {
                                                    runOnUiThread(() -> {
                                                        ImageView imageView = findViewById(R.id.photo_result);
                                                        imageView.setImageURI(uri);
                                                    });
                                                }
                                        );
                                    }
                                }
                                @Override
                                public void onError(@NonNull ImageCaptureException exception) {
                                    Log.e("CameraX", "拍照失败: " + exception.getMessage());
                                }
                            });
                }
        });
    }

    private String makeFileName(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf =  new SimpleDateFormat("yyMMdd_HHmmss");
        return "IMG_" + sdf.format(date)+".jpg";
    }

>>>>>>> 99b6327acc0f8a2c76738bc2a23024024dbd19bb
}