package com.pplgsmkn4.laporanmasyarakat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.pplgsmkn4.laporanmasyarakat.dao.DatabaseDao;
import com.pplgsmkn4.laporanmasyarakat.database.DatabaseClient;
import com.pplgsmkn4.laporanmasyarakat.model.ModelLaporan;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * ViewModel untuk menyimpan laporan baru dari form pengaduan.
 */
public class InputDataViewModel extends AndroidViewModel {

    private final DatabaseDao databaseDao;

    public InputDataViewModel(@NonNull Application application) {
        super(application);

        databaseDao = DatabaseClient.getInstance(application).getAppDatabase().databaseDao();
    }

    public void addLaporan(final String kategori, final String image, final String nama,
                           final String kelas, final String nis, final String telepon,
                           final String terlapor, final String lokasi, final String tanggal,
                           final String isiLaporan, final boolean anonim) {
        Completable.fromAction(() -> {
                    ModelLaporan modelLaporan = new ModelLaporan();
                    modelLaporan.kategori = kategori;
                    modelLaporan.image = image;
                    modelLaporan.nama = nama;
                    modelLaporan.kelas = kelas;
                    modelLaporan.nis = nis;
                    modelLaporan.telepon = telepon;
                    modelLaporan.terlapor = terlapor;
                    modelLaporan.lokasi = lokasi;
                    modelLaporan.tanggal = tanggal;
                    modelLaporan.isi_laporan = isiLaporan;
                    modelLaporan.anonim = anonim;
                    modelLaporan.dibuatPada = System.currentTimeMillis();
                    databaseDao.insertData(modelLaporan);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

}
