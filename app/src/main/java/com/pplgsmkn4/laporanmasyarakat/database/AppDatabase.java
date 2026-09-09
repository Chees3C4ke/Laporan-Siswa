package com.pplgsmkn4.laporanmasyarakat.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.pplgsmkn4.laporanmasyarakat.dao.DatabaseDao;
import com.pplgsmkn4.laporanmasyarakat.model.ModelLaporan;

/**
 * Definisi database Room aplikasi Laporan Siswa.
 */
@Database(entities = {ModelLaporan.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DatabaseDao databaseDao();

}
