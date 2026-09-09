package com.pplgsmkn4.laporanmasyarakat.database;

import android.content.Context;

import androidx.room.Room;

/**
 * Singleton penyedia instance {@link AppDatabase}.
 */
public class DatabaseClient {

    private static final String NAMA_DATABASE = "laporan_siswa_db";

    private static DatabaseClient dcInstance;
    AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, NAMA_DATABASE)
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (dcInstance == null) {
            dcInstance = new DatabaseClient(context);
        }
        return dcInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

}
