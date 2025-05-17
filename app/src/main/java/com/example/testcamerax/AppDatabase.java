package com.example.testcamerax;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
// AppDatabase.java
@Database(entities = {OcrRecord.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    // 获取DAO的抽象方法
    public abstract OcrRecordDao ocrRecordDao();

    // 单例获取（线程安全）
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "ocr_database")
                            .fallbackToDestructiveMigration() // 可选，开发期加速
                            .allowMainThreadQueries() // 如果你没有用线程执行，必须允许主线程
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
