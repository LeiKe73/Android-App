package com.example.testcamerax;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// OcrRecordDao.java
@Dao
public interface OcrRecordDao {
    @Insert
    void insert(OcrRecord record);

    @Query("SELECT * FROM ocr_records ORDER BY timestamp DESC")
    List<OcrRecord> getAllRecords();

    // 补充之前缺少的查询方法
    @Query("SELECT * FROM ocr_records WHERE id = :recordId")
    OcrRecord getRecordById(int recordId);

    @Delete
    void delete(OcrRecord record);
}