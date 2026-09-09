package com.pplgsmkn4.laporanmasyarakat.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.Window;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * Helper tampilan: status bar transparan dengan ikon gelap
 * (konten aplikasi berlatar terang).
 */
public class StatusBarUtil {

    private StatusBarUtil() {
        // utility class
    }

    public static void setup(Activity activity) {
        Window window = activity.getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        // Konten tidak boleh tertutup system bar.
        View root = window.getDecorView();
        root.setSystemUiVisibility(root.getSystemUiVisibility()
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

}
