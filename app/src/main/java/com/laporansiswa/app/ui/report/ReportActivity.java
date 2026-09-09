package com.laporansiswa.app.ui.report;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.laporansiswa.app.BuildConfig;
import com.laporansiswa.app.R;
import com.laporansiswa.app.utils.BitmapManager;
import com.laporansiswa.app.utils.Constant;
import com.laporansiswa.app.utils.StatusBarUtil;
import com.laporansiswa.app.viewmodel.InputDataViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Form pengaduan / laporan siswa.
 * <p>
 * Alur: pilih kategori dari halaman utama -> isi form -> unggah foto bukti
 * (galeri atau kamera) -> kirim -> tersimpan di Room.
 */
public class ReportActivity extends AppCompatActivity {

    public static final String DATA_TITLE = "TITLE";

    /** Sisi terpanjang bitmap yang di-decode, biar tidak OOM. */
    private static final int UKURAN_DECODE = 1200;

    private String strTitle;
    private String strBase64Photo;
    private File fileFotoKamera;

    private Toolbar toolbar;
    private TextView tvTitle;
    private ImageView imageLaporan;
    private LinearLayout layoutImage, layoutIdentitas;
    private SwitchMaterial switchAnonim;
    private ExtendedFloatingActionButton fabSend;
    private EditText inputNama, inputKelas, inputNis, inputTelepon,
            inputTerlapor, inputLokasi, inputTanggal, inputLaporan;

    private InputDataViewModel inputDataViewModel;

    private ActivityResultLauncher<Uri> launcherKamera;
    private ActivityResultLauncher<PickVisualMediaRequest> launcherGaleri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        StatusBarUtil.setup(this);
        daftarLauncher();
        setInitLayout();
        setAnonimToggle();
        setDatePicker();
        setSendLaporan();
    }

    private void daftarLauncher() {
        launcherKamera = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                berhasil -> {
                    if (berhasil != null && berhasil && fileFotoKamera != null) {
                        tampilkanFoto(fileFotoKamera);
                    }
                });

        launcherGaleri = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        File salinan = salinKeCache(uri);
                        if (salinan != null) {
                            tampilkanFoto(salinan);
                        } else {
                            Toast.makeText(this, "Gagal membaca foto dari galeri!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setInitLayout() {
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tvTitle);
        imageLaporan = findViewById(R.id.imageLaporan);
        layoutImage = findViewById(R.id.layoutImage);
        layoutIdentitas = findViewById(R.id.layoutIdentitas);
        switchAnonim = findViewById(R.id.switchAnonim);
        fabSend = findViewById(R.id.fabSend);
        inputNama = findViewById(R.id.inputNama);
        inputKelas = findViewById(R.id.inputKelas);
        inputNis = findViewById(R.id.inputNis);
        inputTelepon = findViewById(R.id.inputTelepon);
        inputTerlapor = findViewById(R.id.inputTerlapor);
        inputLokasi = findViewById(R.id.inputLokasi);
        inputTanggal = findViewById(R.id.inputTanggal);
        inputLaporan = findViewById(R.id.inputLaporan);

        // Ambil judul kategori dari halaman utama.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            strTitle = extras.getString(DATA_TITLE);
        }
        if (strTitle != null) {
            tvTitle.setText(strTitle);
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Lokasi kejadian terisi otomatis dari halaman utama bila tersedia.
        if (Constant.lokasiPengaduan != null && !Constant.lokasiPengaduan.isEmpty()) {
            inputLokasi.setText(Constant.lokasiPengaduan);
        }

        inputDataViewModel = new ViewModelProvider(this, ViewModelProvider
                .AndroidViewModelFactory.getInstance(getApplication()))
                .get(InputDataViewModel.class);

        layoutImage.setOnClickListener(v -> pilihSumberFoto());
    }

    private void pilihSumberFoto() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Unggah Foto Bukti Laporan");
        String[] pictureDialogItems = {"Pilih foto dari galeri", "Ambil foto lewat kamera"};

        pictureDialog.setItems(pictureDialogItems, (dialog, which) -> {
            if (which == 0) {
                // Photo Picker: tidak butuh permission storage sama sekali.
                launcherGaleri.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            } else {
                bukaKamera();
            }
        });
        pictureDialog.show();
    }

    private void bukaKamera() {
        try {
            fileFotoKamera = buatFileFoto();
            Uri uriFoto = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider", fileFotoKamera);
            launcherKamera.launch(uriFoto);
        } catch (IOException e) {
            Toast.makeText(this, "Gagal membuka kamera!", Toast.LENGTH_SHORT).show();
        }
    }

    /** File foto kamera disimpan di folder privat aplikasi (aman untuk semua API). */
    private File buatFileFoto() throws IOException {
        File direktori = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (direktori != null && !direktori.exists()) {
            direktori.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return File.createTempFile("Bukti_" + timeStamp + "_", ".jpg", direktori);
    }

    /** Menyalin foto pilihan galeri ke cache aplikasi supaya bisa di-decode tanpa permission. */
    private File salinKeCache(Uri uri) {
        File tujuan = new File(getCacheDir(), "pick_" + System.currentTimeMillis() + ".jpg");
        try (InputStream in = getContentResolver().openInputStream(uri);
             OutputStream out = new FileOutputStream(tujuan)) {
            if (in == null) {
                return null;
            }
            byte[] buffer = new byte[8192];
            int dibaca;
            while ((dibaca = in.read(buffer)) != -1) {
                out.write(buffer, 0, dibaca);
            }
            return tujuan;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void tampilkanFoto(File fileFoto) {
        Bitmap bitmap = decodeSampled(fileFoto, UKURAN_DECODE);
        if (bitmap == null) {
            Toast.makeText(this, "Foto tidak valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        Glide.with(this)
                .load(bitmap)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_image_upload)
                .into(imageLaporan);

        strBase64Photo = BitmapManager.bitmapToBase64(bitmap);
    }

    /** Decode bitmap dengan inSampleSize supaya hemat memori. */
    private Bitmap decodeSampled(File fileFoto, int ukuranMaks) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileFoto.getAbsolutePath(), options);

        int scale = 1;
        while ((options.outWidth / scale / 2) >= ukuranMaks
                && (options.outHeight / scale / 2) >= ukuranMaks) {
            scale *= 2;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        return BitmapFactory.decodeFile(fileFoto.getAbsolutePath(), options);
    }

    private void setAnonimToggle() {
        switchAnonim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layoutIdentitas.setVisibility(isChecked ? LinearLayout.GONE : LinearLayout.VISIBLE);
                if (isChecked) {
                    inputNama.setText("");
                    inputKelas.setText("");
                    inputNis.setText("");
                }
            }
        });
    }

    private void setDatePicker() {
        inputTanggal.setOnClickListener(view -> {
            Calendar tanggalKejadian = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
                tanggalKejadian.set(Calendar.YEAR, year);
                tanggalKejadian.set(Calendar.MONTH, monthOfYear);
                tanggalKejadian.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
                inputTanggal.setText(simpleDateFormat.format(tanggalKejadian.getTime()));
            };

            new DatePickerDialog(ReportActivity.this, date,
                    tanggalKejadian.get(Calendar.YEAR),
                    tanggalKejadian.get(Calendar.MONTH),
                    tanggalKejadian.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setSendLaporan() {
        fabSend.setOnClickListener(v -> {
            boolean anonim = switchAnonim.isChecked();

            String strNama = inputNama.getText().toString().trim();
            String strKelas = inputKelas.getText().toString().trim();
            String strNis = inputNis.getText().toString().trim();
            String strTelepon = inputTelepon.getText().toString().trim();
            String strTerlapor = inputTerlapor.getText().toString().trim();
            String strLokasi = inputLokasi.getText().toString().trim();
            String strTanggal = inputTanggal.getText().toString().trim();
            String strLaporan = inputLaporan.getText().toString().trim();

            if (strBase64Photo == null) {
                Toast.makeText(this, "Unggah dulu foto bukti laporannya!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (!anonim && (strNama.isEmpty() || strKelas.isEmpty())) {
                Toast.makeText(this, "Nama dan kelas wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (strTelepon.isEmpty() || strLokasi.isEmpty()
                    || strTanggal.isEmpty() || strLaporan.isEmpty()) {
                Toast.makeText(this, "Data tidak boleh ada yang kosong!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (anonim) {
                strNama = Constant.PELAPOR_ANONIM;
                strKelas = "-";
                strNis = "-";
            }

            inputDataViewModel.addLaporan(strTitle, strBase64Photo, strNama, strKelas, strNis,
                    strTelepon, strTerlapor, strLokasi, strTanggal, strLaporan, anonim);
            Toast.makeText(this, "Laporan Anda terkirim, tunggu info selanjutnya ya!",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
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
