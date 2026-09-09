package com.pplgsmkn4.laporanmasyarakat.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Konversi Bitmap <-> Base64 untuk penyimpanan foto bukti di Room,
 * plus pengecilan ukuran agar hemat memori & hemat tempat.
 */
public class BitmapManager {

    /** Sisi terpanjang bitmap setelah dikompres, dalam piksel. */
    private static final int UKURAN_MAKS = 1024;

    /** Kualitas kompresi JPEG (0-100). */
    private static final int KUALITAS_JPEG = 82;

    private BitmapManager() {
        // utility class
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        Bitmap scaled = skalaTurun(bitmap, UKURAN_MAKS);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, KUALITAS_JPEG, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String base64) {
        if (base64 == null || base64.isEmpty()) {
            return null;
        }
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /** Memperkecil bitmap proporsional supaya sisi terpanjang <= maxSize. */
    public static Bitmap skalaTurun(Bitmap src, int maxSize) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        if (width <= maxSize && height <= maxSize) {
            return src;
        }
        float ratio = Math.min((float) maxSize / width, (float) maxSize / height);
        int newWidth = Math.max(1, Math.round(width * ratio));
        int newHeight = Math.max(1, Math.round(height * ratio));
        return Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
    }

}
