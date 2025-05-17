package com.example.testcamerax;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;

    private TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_page); // 你需要创建这个布局文件

        imageView = findViewById(R.id.detail_image);
        textView = findViewById(R.id.detail_text);
        titleView = findViewById(R.id.detail_title);

        int recordId = getIntent().getIntExtra("RECORD_ID", -1);
        if (recordId != -1) {
            // 查询数据库（子线程）
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                OcrRecord record = db.ocrRecordDao().getRecordById(recordId);

                runOnUiThread(() -> {
                    if (record != null) {
                        // 显示图片和识别内容
                        Uri uri = record.getImageUri();
                        if (uri != null) {
                            Glide.with(this)
                                    .load(uri)
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .error(R.drawable.ic_launcher_foreground)
                                    .into(imageView);
                        }
                        textView.setText(record.getRecognizedText());

                        String title = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                .format(new java.util.Date(record.getTimestamp()));
                        titleView.setText(title);
                    } else {
                        Toast.makeText(this, "未找到记录", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        } else {
            textView.setText("无效的记录 ID");
        }

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

    }
}
