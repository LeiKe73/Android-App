package com.example.testcamerax;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "ocr_records")
public class OcrRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "image_path")
    private String imagePath;

    @ColumnInfo(name = "recognized_text")
    private String recognizedText;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    // Room 需要的无参构造方法（添加 @Ignore 如果使用其他主构造方法）
    public OcrRecord() {
    }

    // 用于手动创建的构造方法
    @Ignore
    public OcrRecord(String imagePath, String recognizedText, long timestamp) {
        this.imagePath = imagePath;
        this.recognizedText = recognizedText;
        this.timestamp = timestamp;
    }

    //--- Getter 方法 ---
    public int getId() { return id; }
    public String getImagePath() { return imagePath; }
    public String getRecognizedText() { return recognizedText; }
    public long getTimestamp() { return timestamp; }

    //--- Setter 方法（必须提供）---
    public void setId(int id) { this.id = id; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setRecognizedText(String recognizedText) { this.recognizedText = recognizedText; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // 非数据库字段的辅助方法（添加 @Ignore 注解）
    @androidx.room.Ignore
    public Uri getImageUri() {
        return Uri.parse(imagePath);
    }
}