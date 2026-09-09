package com.pplgsmkn4.laporanmasyarakat.utils;

/**
 * Kumpulan konstanta kategori laporan + lokasi hasil geocoding
 * yang dibagikan antar activity.
 */
public class Constant {

    /** Alamat terbaca manusia hasil reverse geocoding, diisi oleh MainActivity. */
    public static String lokasiPengaduan;

    public static final String KATEGORI_PERUNDUNGAN = "Laporan Perundungan";
    public static final String KATEGORI_FASILITAS = "Laporan Fasilitas Rusak";
    public static final String KATEGORI_PELANGGARAN = "Laporan Pelanggaran Tata Tertib";
    public static final String KATEGORI_KEAMANAN = "Laporan Keamanan & Keselamatan";

    /** Nilai tampilan untuk pelapor yang memilih mode anonim. */
    public static final String PELAPOR_ANONIM = "Pelapor Anonim";

    private Constant() {
        // utility class
    }

}
