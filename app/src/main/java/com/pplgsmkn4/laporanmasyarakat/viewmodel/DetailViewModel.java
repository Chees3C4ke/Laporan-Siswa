package com.pplgsmkn4.laporanmasyarakat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pplgsmkn4.laporanmasyarakat.dao.DatabaseDao;
import com.pplgsmkn4.laporanmasyarakat.database.DatabaseClient;
import com.pplgsmkn4.laporanmasyarakat.model.ModelLaporan;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * ViewModel untuk halaman detail satu laporan.
 */
public class DetailViewModel extends AndroidViewModel {

    private final DatabaseDao databaseDao;

    public DetailViewModel(@NonNull Application application) {
        super(application);

        databaseDao = DatabaseClient.getInstance(application).getAppDatabase().databaseDao();
    }

    public LiveData<ModelLaporan> getLaporanById(int uid) {
        return databaseDao.getLaporanById(uid);
    }

    public void deleteDataById(final int uid) {
        Completable.fromAction(() -> databaseDao.deleteSingleLaporan(uid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

}
