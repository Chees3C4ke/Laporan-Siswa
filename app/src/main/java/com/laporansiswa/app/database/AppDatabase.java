package com.laporansiswa.app.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.laporansiswa.app.dao.DatabaseDao;
import com.laporansiswa.app.model.ModelLaporan;

/**
 * Definisi database Room aplikasi Laporan Siswa.
 */
@Database(entities = {ModelLaporan.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DatabaseDao databaseDao();

}
