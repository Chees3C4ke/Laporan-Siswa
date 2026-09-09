package com.laporansiswa.app.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.laporansiswa.app.R;
import com.laporansiswa.app.ui.history.HistoryActivity;
import com.laporansiswa.app.ui.report.ReportActivity;
import com.laporansiswa.app.utils.Constant;
import com.laporansiswa.app.utils.StatusBarUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Halaman utama: daftar kartu kategori laporan + tombol riwayat.
 * <p>
 * Sekaligus mengambil lokasi terakhir perangkat (reverse geocoding)
 * supaya kolom "Lokasi Kejadian" di form sudah terisi otomatis.
 */
public class MainActivity extends AppCompatActivity {

    private static final String[] IZIN_LOKASI = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handlerUtama = new Handler(Looper.getMainLooper());

    private ActivityResultLauncher<String[]> launcherIzinLokasi;
    private FusedLocationProviderClient fusedLocationClient;

    private CardView cvPerundungan, cvFasilitas, cvPelanggaran, cvKeamanan, cvHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtil.setup(this);
        daftarLauncherIzin();
        setInitLayout();
        mintaIzinLokasi();
    }

    private void daftarLauncherIzin() {
        launcherIzinLokasi = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> ambilLokasiTerakhir());
    }

    private void setInitLayout() {
        cvPerundungan = findViewById(R.id.cvPerundungan);
        cvFasilitas = findViewById(R.id.cvFasilitas);
        cvPelanggaran = findViewById(R.id.cvPelanggaran);
        cvKeamanan = findViewById(R.id.cvKeamanan);
        cvHistory = findViewById(R.id.cvHistory);

        cvPerundungan.setOnClickListener(v -> bukaForm(Constant.KATEGORI_PERUNDUNGAN));
        cvFasilitas.setOnClickListener(v -> bukaForm(Constant.KATEGORI_FASILITAS));
        cvPelanggaran.setOnClickListener(v -> bukaForm(Constant.KATEGORI_PELANGGARAN));
        cvKeamanan.setOnClickListener(v -> bukaForm(Constant.KATEGORI_KEAMANAN));

        cvHistory.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HistoryActivity.class)));
    }

    private void bukaForm(String judul) {
        Intent intent = new Intent(MainActivity.this, ReportActivity.class);
        intent.putExtra(ReportActivity.DATA_TITLE, judul);
        startActivity(intent);
    }

    private void mintaIzinLokasi() {
        if (sudahPunyaIzinLokasi()) {
            ambilLokasiTerakhir();
        } else {
            launcherIzinLokasi.launch(IZIN_LOKASI);
        }
    }

    private boolean sudahPunyaIzinLokasi() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void ambilLokasiTerakhir() {
        if (!sudahPunyaIzinLokasi()) {
            return;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            isiAlamatDariKoordinat(location.getLatitude(), location.getLongitude());
                        }
                    })
                    .addOnFailureListener(this, e ->
                            // Lokasi gagal diambil: user tetap bisa mengetik lokasi manual di form.
                            Constant.lokasiPengaduan = null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void isiAlamatDariKoordinat(double latitude, double longitude) {
        executor.execute(() -> {
            String alamat = null;
            try {
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                if (addressList != null && !addressList.isEmpty()) {
                    alamat = addressList.get(0).getAddressLine(0);
                }
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }
            final String hasil = alamat;
            handlerUtama.post(() -> {
                if (hasil != null) {
                    Constant.lokasiPengaduan = hasil;
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }

}
