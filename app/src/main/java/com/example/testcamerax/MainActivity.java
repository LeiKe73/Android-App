package com.example.testcamerax;

import static com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.content.res.TypedArray;
import android.database.Cursor;
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
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;

import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;

import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;

import org.jspecify.annotations.NonNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    private static final String FULL_MODE = "FULL";
    private static final String BASE_MODE = "BASE";
    private static final String BASE_MODE_WITH_FILTER = "BASE_WITH_FILTER";
    // 在 Activity 类中添加以下成员变量
    private CustomAdapter adapter;
    private  RecyclerView recyclerView;
    private AppDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });


//        // 底部导航点击处理
//        findViewById(R.id.btn_left).setOnClickListener(v -> switchView(0));
//        findViewById(R.id.btn_right).setOnClickListener(v -> switchView(1));
//        findViewById(R.id.btn_camera).setOnClickListener(v -> {
//            if (photoInterface != null) {
//                photoInterface.onTakePhoto();
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.text_recognition);
        db = AppDatabase.getInstance(this);
        recyclerView = findViewById(R.id.recycler_view1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomAdapter(this,List.of());
        recyclerView.setAdapter(adapter);

        Button textRecognize =findViewById(R.id.text_recognize);
        textRecognize.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this,textRecognize.class);
            startActivity(intent);
        });

        loadDataFromDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        Executors.newSingleThreadExecutor().execute(() -> {
//            OcrRecord testRecord = new OcrRecord();
//            testRecord.setImagePath("content://some/test/uri");
//            testRecord.setRecognizedText("测试识别内容");
//            testRecord.setTimestamp(System.currentTimeMillis());
//
//            db.ocrRecordDao().insert(testRecord);
//
//            List<OcrRecord> records = db.ocrRecordDao().getAllRecords();
//
//            runOnUiThread(() -> {
//                adapter.updateData(records);
//            });
//        });
    }

    //从数据库中加载数据
    private void loadDataFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<OcrRecord> allRecords = db.ocrRecordDao().getAllRecords(); // 你需实现该方法
            Log.d("DB_DEBUG", "数据库查询到 " + allRecords.size() + " 条数据");
            runOnUiThread(() -> {
                adapter.updateData(allRecords);
                Toast.makeText(MainActivity.this, "加载了 " + allRecords.size() + " 条数据", Toast.LENGTH_SHORT).show();
            });
        });
    }
    private void filescanner(){

        //调用文件扫描
        GmsDocumentScannerOptions.Builder builder =
                new GmsDocumentScannerOptions.Builder()
                        .setGalleryImportAllowed(true)
                        .setPageLimit(2)
                        .setResultFormats(RESULT_FORMAT_JPEG);// ,RESULT_FORMAT_PDF
        //模式切换
        String selectedMode = FULL_MODE;
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
                                            "文件Uri:" + imageUri, // 显示完整路径
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
    private String makeFileName(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf =  new SimpleDateFormat("yyMMdd_HHmmss_SSS");
        return "IMG_" + sdf.format(date)+".jpg";
    }
}