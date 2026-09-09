package com.laporansiswa.app.ui.detail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.laporansiswa.app.R;
import com.laporansiswa.app.model.ModelLaporan;
import com.laporansiswa.app.utils.BitmapManager;
import com.laporansiswa.app.utils.KategoriHelper;
import com.laporansiswa.app.utils.StatusBarUtil;
import com.laporansiswa.app.viewmodel.DetailViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Halaman detail satu laporan: foto bukti + seluruh data pengaduan,
 * lengkap dengan tombol hapus laporan.
 */
public class DetailActivity extends AppCompatActivity {

    public static final String DATA_UID = "UID";

    private DetailViewModel detailViewModel;
    private int uidLaporan;

    private Toolbar toolbar;
    private TextView tvTitle, tvKategori, tvNama, tvNis, tvTelepon, tvTerlapor,
            tvLokasi, tvTanggal, tvIsiLaporan, tvWaktuKirim;
    private LinearLayout layoutKategori;
    private ImageView imageLaporan;
    private MaterialButton btnHapus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        StatusBarUtil.setup(this);
        setToolbar();
        setInitLayout();
        setViewModel();
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setInitLayout() {
        tvTitle = findViewById(R.id.tvTitle);
        tvKategori = findViewById(R.id.tvKategori);
        tvNama = findViewById(R.id.tvNama);
        tvNis = findViewById(R.id.tvNis);
        tvTelepon = findViewById(R.id.tvTelepon);
        tvTerlapor = findViewById(R.id.tvTerlapor);
        tvLokasi = findViewById(R.id.tvLokasi);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvIsiLaporan = findViewById(R.id.tvIsiLaporan);
        tvWaktuKirim = findViewById(R.id.tvWaktuKirim);
        layoutKategori = findViewById(R.id.layoutKategori);
        imageLaporan = findViewById(R.id.imageLaporan);
        btnHapus = findViewById(R.id.btnHapus);

        uidLaporan = getIntent().getIntExtra(DATA_UID, -1);
        tvTitle.setText(R.string.detail_laporan);

        btnHapus.setOnClickListener(v -> konfirmasiHapus());
    }

    private void setViewModel() {
        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        if (uidLaporan == -1) {
            finish();
            return;
        }

        detailViewModel.getLaporanById(uidLaporan).observe(this, data -> {
            if (data == null) {
                // Laporan sudah dihapus dari halaman lain.
                finish();
                return;
            }
            bindData(data);
        });
    }

    private void bindData(ModelLaporan data) {
        layoutKategori.setBackgroundColor(KategoriHelper.warnaKategori(this, data.getKategori()));
        tvKategori.setText(data.getKategori());

        if (data.isAnonim()) {
            tvNama.setText(R.string.pelapor_anonim);
            tvNis.setText(getString(R.string.label_nis, "-"));
        } else {
            tvNama.setText(getString(R.string.format_nama_kelas, data.getNama(), data.getKelas()));
            String nis = data.getNis() == null || data.getNis().isEmpty() ? "-" : data.getNis();
            tvNis.setText(getString(R.string.label_nis, nis));
        }

        tvTelepon.setText(getString(R.string.label_telepon, data.getTelepon()));
        String terlapor = data.getTerlapor() == null || data.getTerlapor().isEmpty()
                ? getString(R.string.tidak_disebutkan)
                : data.getTerlapor();
        tvTerlapor.setText(getString(R.string.label_terlapor, terlapor));
        tvLokasi.setText(getString(R.string.label_lokasi, data.getLokasi()));
        tvTanggal.setText(getString(R.string.label_tanggal, data.getTanggal()));
        tvIsiLaporan.setText(data.getIsiLaporan());

        SimpleDateFormat formatWaktu = new SimpleDateFormat("d MMMM yyyy, HH:mm", Locale.getDefault());
        tvWaktuKirim.setText(getString(R.string.label_dikirim,
                formatWaktu.format(new Date(data.getDibuatPada()))));

        Bitmap bitmap = BitmapManager.base64ToBitmap(data.getImage());
        if (bitmap != null) {
            Glide.with(this)
                    .load(bitmap)
                    .placeholder(R.drawable.ic_image_upload)
                    .into(imageLaporan);
        } else {
            imageLaporan.setImageResource(R.drawable.ic_image_upload);
        }
    }

    private void konfirmasiHapus() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Hapus laporan ini?");
        alertDialogBuilder.setPositiveButton("Ya, Hapus", (dialogInterface, i) -> {
            detailViewModel.deleteDataById(uidLaporan);
            Toast.makeText(this, "Yeay! Laporan sudah dihapus", Toast.LENGTH_SHORT).show();
            finish();
        });
        alertDialogBuilder.setNegativeButton("Batal", (dialogInterface, i) -> dialogInterface.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
