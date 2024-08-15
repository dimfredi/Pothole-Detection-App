package com.example.lakuva;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.location.LocationResult;

public interface LocationCallback {
    void onLocationRetrieved(double latitude, double longitude);
    void onLocationResult(LocationResult locationResult);

    // Nested interface for full-screen and notch control
    interface FullScreenInterface {
        default void enableFullScreenMode(Window window) {
            // Hide the status bar and navigation bar
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            // Apply fullscreen flag
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        default void disableNotch(Window window) {
            // Set window to avoid display cutout (notch)
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            window.setAttributes(layoutParams);
        }
    }
}
