package com.laporansiswa.app.utils;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.laporansiswa.app.R;

/**
 * Pemetaan kategori laporan ke warna tema supaya tampilan
 * kartu riwayat konsisten dengan kartu di halaman utama.
 */
public class KategoriHelper {

    private KategoriHelper() {
        // utility class
    }

    /** Warna resolved (int) untuk header kartu riwayat sesuai kategori. */
    public static int warnaKategori(Context context, String kategori) {
        int colorRes;
        if (Constant.KATEGORI_PERUNDUNGAN.equals(kategori)) {
            colorRes = R.color.perundungan;
        } else if (Constant.KATEGORI_FASILITAS.equals(kategori)) {
            colorRes = R.color.fasilitas;
        } else if (Constant.KATEGORI_PELANGGARAN.equals(kategori)) {
            colorRes = R.color.pelanggaran;
        } else if (Constant.KATEGORI_KEAMANAN.equals(kategori)) {
            colorRes = R.color.keamanan;
        } else {
            colorRes = R.color.primary;
        }
        return ContextCompat.getColor(context, colorRes);
    }

}
