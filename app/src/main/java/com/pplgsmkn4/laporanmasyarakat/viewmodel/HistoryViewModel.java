package com.pplgsmkn4.laporanmasyarakat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.pplgsmkn4.laporanmasyarakat.dao.DatabaseDao;
import com.pplgsmkn4.laporanmasyarakat.database.DatabaseClient;
import com.pplgsmkn4.laporanmasyarakat.model.ModelLaporan;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * ViewModel untuk halaman riwayat laporan, termasuk filter kategori
 * dan aksi hapus (satuan / semua).
 */
public class HistoryViewModel extends AndroidViewModel {

    private final DatabaseDao databaseDao;

    /** null = tampilkan semua kategori. */
    private final MutableLiveData<String> filterKategori = new MutableLiveData<>(null);

    private final LiveData<List<ModelLaporan>> daftarLaporan;

    public HistoryViewModel(@NonNull Application application) {
        super(application);

        databaseDao = DatabaseClient.getInstance(application).getAppDatabase().databaseDao();
        daftarLaporan = Transformations.switchMap(filterKategori,
                kategori -> databaseDao.getLaporanFiltered(kategori));
    }

    public LiveData<List<ModelLaporan>> getDataLaporan() {
        return daftarLaporan;
    }

    public String getFilterKategori() {
        return filterKategori.getValue();
    }

    public void setFilterKategori(String kategori) {
        if (!Objects.equals(filterKategori.getValue(), kategori)) {
            filterKategori.setValue(kategori);
        }
    }

    public void deleteDataById(final int uid) {
        Completable.fromAction(() -> databaseDao.deleteSingleLaporan(uid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void deleteAllData() {
        Completable.fromAction(databaseDao::deleteAllLaporan)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

}
