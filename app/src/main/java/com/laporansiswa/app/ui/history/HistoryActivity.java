package com.laporansiswa.app.ui.history;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.laporansiswa.app.R;
import com.laporansiswa.app.model.ModelLaporan;
import com.laporansiswa.app.ui.detail.DetailActivity;
import com.laporansiswa.app.utils.Constant;
import com.laporansiswa.app.utils.StatusBarUtil;
import com.laporansiswa.app.viewmodel.HistoryViewModel;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Daftar semua laporan yang pernah dikirim dari perangkat ini.
 * - Ketuk kartu untuk melihat detail laporan.
 * - Chip di bawah toolbar untuk memfilter berdasarkan kategori.
 * - Menu untuk export CSV dan hapus semua riwayat.
 */
public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.HistoryAdapterCallback {

    private static final String MIME_CSV = "text/csv";

    private final List<ModelLaporan> modelLaporanList = new ArrayList<>();

    private HistoryAdapter historyAdapter;
    private HistoryViewModel historyViewModel;
    private Toolbar toolbar;
    private RecyclerView rvHistory;
    private TextView tvNotFound;
    private ChipGroup chipGroupFilter;
    private ActivityResultLauncher<String> launcherExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        StatusBarUtil.setup(this);
        daftarLauncherExport();
        setToolbar();
        setInitLayout();
        setFilterChip();
        setViewModel();
    }

    private void daftarLauncherExport() {
        launcherExport = registerForActivityResult(
                new ActivityResultContracts.CreateDocument(MIME_CSV),
                uri -> {
                    if (uri != null) {
                        tulisCsv(uri);
                    }
                });
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
        rvHistory = findViewById(R.id.rvHistory);
        tvNotFound = findViewById(R.id.tvNotFound);
        chipGroupFilter = findViewById(R.id.chipGroupFilter);

        tvNotFound.setVisibility(View.GONE);

        historyAdapter = new HistoryAdapter(this, modelLaporanList, this);
        rvHistory.setHasFixedSize(true);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(historyAdapter);
    }

    private void setFilterChip() {
        findViewById(R.id.chipSemua).setOnClickListener(v ->
                historyViewModel.setFilterKategori(null));
        findViewById(R.id.chipPerundungan).setOnClickListener(v ->
                historyViewModel.setFilterKategori(Constant.KATEGORI_PERUNDUNGAN));
        findViewById(R.id.chipFasilitas).setOnClickListener(v ->
                historyViewModel.setFilterKategori(Constant.KATEGORI_FASILITAS));
        findViewById(R.id.chipPelanggaran).setOnClickListener(v ->
                historyViewModel.setFilterKategori(Constant.KATEGORI_PELANGGARAN));
        findViewById(R.id.chipKeamanan).setOnClickListener(v ->
                historyViewModel.setFilterKategori(Constant.KATEGORI_KEAMANAN));
    }

    private void setViewModel() {
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        historyViewModel.getDataLaporan().observe(this, modelLaporan -> {
            List<ModelLaporan> data = modelLaporan == null ? new ArrayList<>() : modelLaporan;
            if (data.isEmpty()) {
                tvNotFound.setVisibility(View.VISIBLE);
                rvHistory.setVisibility(View.GONE);
                tvNotFound.setText(historyViewModel.getFilterKategori() == null
                        ? R.string.belum_ada_laporan
                        : R.string.belum_ada_laporan_filter);
            } else {
                tvNotFound.setVisibility(View.GONE);
                rvHistory.setVisibility(View.VISIBLE);
            }
            historyAdapter.setDataAdapter(data);
            invalidateOptionsMenu();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean adaIsi = !modelLaporanList.isEmpty();
        MenuItem itemHapus = menu.findItem(R.id.menu_hapus_semua);
        MenuItem itemExport = menu.findItem(R.id.menu_export);
        if (itemHapus != null) {
            itemHapus.setVisible(adaIsi);
        }
        if (itemExport != null) {
            itemExport.setVisible(adaIsi);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onItemClicked(ModelLaporan modelLaporan) {
        Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.DATA_UID, modelLaporan.getUid());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.menu_hapus_semua) {
            konfirmasiHapusSemua();
            return true;
        } else if (itemId == R.id.menu_export) {
            mulaiExport();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void konfirmasiHapusSemua() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Hapus SEMUA riwayat laporan di perangkat ini?");
        alertDialogBuilder.setPositiveButton("Ya, Hapus Semua", (dialogInterface, i) ->
                historyViewModel.deleteAllData());
        alertDialogBuilder.setNegativeButton("Batal", (dialogInterface, i) -> dialogInterface.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // ---------------------------------------------------------------- export

    private void mulaiExport() {
        if (modelLaporanList.isEmpty()) {
            Toast.makeText(this, R.string.export_tidak_ada, Toast.LENGTH_SHORT).show();
            return;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmm", Locale.US).format(new Date());
        launcherExport.launch("laporan-siswa-" + timeStamp + ".csv");
    }

    /** Menulis seluruh laporan yang sedang tampil ke file CSV pilihan user. */
    private void tulisCsv(Uri uri) {
        SimpleDateFormat formatWaktu =
                new SimpleDateFormat("d MMMM yyyy HH:mm", Locale.getDefault());
        StringBuilder sb = new StringBuilder();
        sb.append("Kategori,Nama Pelapor,Kelas,NIS,Telepon,Terlapor,"
                + "Lokasi Kejadian,Tanggal Kejadian,Isi Laporan,Anonim,Waktu Dikirim\n");

        for (ModelLaporan data : modelLaporanList) {
            sb.append(csvEsc(data.getKategori())).append(',')
                    .append(csvEsc(data.getNama())).append(',')
                    .append(csvEsc(data.getKelas())).append(',')
                    .append(csvEsc(data.getNis())).append(',')
                    .append(csvEsc(data.getTelepon())).append(',')
                    .append(csvEsc(data.getTerlapor())).append(',')
                    .append(csvEsc(data.getLokasi())).append(',')
                    .append(csvEsc(data.getTanggal())).append(',')
                    .append(csvEsc(data.getIsiLaporan())).append(',')
                    .append(csvEsc(data.isAnonim() ? "Ya" : "Tidak")).append(',')
                    .append(csvEsc(formatWaktu.format(new Date(data.getDibuatPada()))))
                    .append('\n');
        }

        try (OutputStream out = getContentResolver().openOutputStream(uri);
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            if (out == null) {
                throw new java.io.IOException("Stream kosong");
            }
            // BOM supaya kolom tidak berantakan saat dibuka di Microsoft Excel.
            out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            writer.write(sb.toString());
            writer.flush();
            Toast.makeText(this, R.string.export_sukses, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.export_gagal, Toast.LENGTH_SHORT).show();
        }
    }

    private String csvEsc(String value) {
        String v = value == null ? "" : value;
        v = v.replace("\"", "\"\"").replace("\r", " ").replace("\n", " ");
        return "\"" + v + "\"";
    }

}
