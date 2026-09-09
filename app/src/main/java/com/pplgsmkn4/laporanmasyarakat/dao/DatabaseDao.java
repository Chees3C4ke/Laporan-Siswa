package com.pplgsmkn4.laporanmasyarakat.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pplgsmkn4.laporanmasyarakat.model.ModelLaporan;

import java.util.List;

/**
 * Data Access Object untuk tabel tbl_laporan.
 */
@Dao
public interface DatabaseDao {

    @Query("SELECT * FROM tbl_laporan ORDER BY dibuat_pada DESC")
    LiveData<List<ModelLaporan>> getAllLaporan();

    /**
     * Ambil laporan berdasarkan kategori; kirim null untuk semua kategori.
     */
    @Query("SELECT * FROM tbl_laporan WHERE (:kategori IS NULL OR kategori = :kategori) ORDER BY dibuat_pada DESC")
    LiveData<List<ModelLaporan>> getLaporanFiltered(String kategori);

    @Query("SELECT * FROM tbl_laporan WHERE uid = :uid")
    LiveData<ModelLaporan> getLaporanById(int uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(ModelLaporan... modelLaporan);

    @Query("DELETE FROM tbl_laporan")
    void deleteAllLaporan();

    @Query("DELETE FROM tbl_laporan WHERE uid = :uid")
    void deleteSingleLaporan(int uid);

}
