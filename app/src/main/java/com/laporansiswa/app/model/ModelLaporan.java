package com.laporansiswa.app.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Entity Room untuk satu buah laporan siswa.
 * <p>
 * Struktur tabel mengikuti pola repo referensi (Laporan-Masyarakat)
 * dengan tambahan kolom khas lingkungan sekolah: kelas, nis, terlapor,
 * flag anonim, dan waktu pembuatan laporan.
 */
@Entity(tableName = "tbl_laporan")
public class ModelLaporan implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    public int uid;

    @ColumnInfo(name = "kategori")
    public String kategori;

    /** Foto bukti dalam bentuk Base64. */
    @ColumnInfo(name = "image")
    public String image;

    @ColumnInfo(name = "nama")
    public String nama;

    @ColumnInfo(name = "kelas")
    public String kelas;

    @ColumnInfo(name = "nis")
    public String nis;

    @ColumnInfo(name = "telepon")
    public String telepon;

    /** Nama pihak yang dilaporkan, boleh kosong. */
    @ColumnInfo(name = "terlapor")
    public String terlapor;

    @ColumnInfo(name = "lokasi")
    public String lokasi;

    @ColumnInfo(name = "tanggal")
    public String tanggal;

    @ColumnInfo(name = "isi_laporan")
    public String isi_laporan;

    /** true = identitas pelapor disembunyikan. */
    @ColumnInfo(name = "anonim")
    public boolean anonim;

    /** Epoch millis saat laporan disimpan, untuk urutan riwayat. */
    @ColumnInfo(name = "dibuat_pada")
    public long dibuatPada;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getNis() {
        return nis;
    }

    public void setNis(String nis) {
        this.nis = nis;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getTerlapor() {
        return terlapor;
    }

    public void setTerlapor(String terlapor) {
        this.terlapor = terlapor;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getIsiLaporan() {
        return isi_laporan;
    }

    public void setIsiLaporan(String isi_laporan) {
        this.isi_laporan = isi_laporan;
    }

    public boolean isAnonim() {
        return anonim;
    }

    public void setAnonim(boolean anonim) {
        this.anonim = anonim;
    }

    public long getDibuatPada() {
        return dibuatPada;
    }

    public void setDibuatPada(long dibuatPada) {
        this.dibuatPada = dibuatPada;
    }
}
