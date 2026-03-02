package org.uqm.game;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.libsdl.app.SDLActivity;
import org.libsdl.app.SDLSurface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Main activity for Ur-Quan Masters Android port.
 * Extends SDL2's SDLActivity for native rendering, adds touch overlay.
 */
public class UQMActivity extends SDLActivity {
    private static final String TAG = "UQM";
    private TouchOverlayView mTouchOverlay;

    // Static reference for JNI callbacks from native code.
    private static UQMActivity sInstance;

    // Overlay mode constants matching android_overlay.h
    private static final int OVERLAY_MODE_COMBAT = 0;
    private static final int OVERLAY_MODE_MENU   = 1;
    private static final int OVERLAY_MODE_FLIGHT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sInstance = this;
        // Extract content assets before SDL init (it needs them)
        extractAssets();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sInstance == this) {
            sInstance = null;
        }
    }

    /**
     * Called from native code (JNI) to change the overlay mode.
     * Runs on the SDL thread, so we post to the UI thread.
     *
     * @param mode       0 = COMBAT, 1 = MENU, 2 = FLIGHT
     * @param deleteFlag true to show the DELETE button (MENU mode only)
     */
    public static void nativeSetOverlayMode(int mode, boolean deleteFlag) {
        final UQMActivity act = sInstance;
        if (act == null || act.mTouchOverlay == null) return;

        final TouchOverlayView.OverlayMode overlayMode;
        switch (mode) {
            case OVERLAY_MODE_COMBAT: overlayMode = TouchOverlayView.OverlayMode.COMBAT; break;
            case OVERLAY_MODE_FLIGHT: overlayMode = TouchOverlayView.OverlayMode.FLIGHT; break;
            default:                  overlayMode = TouchOverlayView.OverlayMode.MENU;   break;
        }

        act.runOnUiThread(() -> act.mTouchOverlay.setMode(overlayMode, deleteFlag));
    }

    /**
     * Called by SDLActivity after the SDL surface is created.
     * We add our transparent touch overlay on top.
     */
    @Override
    protected void onResume() {
        super.onResume();
        enableImmersiveMode();
        // Disable accelerometer — SDLSurface enables it by default and
        // SDL feeds tilt data to the game, which interprets it as
        // directional input (phantom down-press when phone tilts).
        // Post to run after SDL's handleNativeState() re-enables it.
        if (mSurface != null) {
            mSurface.post(() -> {
                mSurface.enableSensor(android.hardware.Sensor.TYPE_ACCELEROMETER, false);
            });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    /**
     * Block system-generated DPAD key events from reaching SDL.
     * Our TouchOverlayView injects direction input via direct JNI calls
     * (SDLActivity.onNativeKeyDown/Up), which bypass dispatchKeyEvent.
     * Without this filter, Android's focus navigation, IME, accessibility,
     * or virtual controller can inject phantom DPAD events that our overlay
     * cannot release, causing stuck keys on menu screens.
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP ||
            keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
            keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
            keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ||
            keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            return true; // consume silently — overlay handles directions
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setContentView(View view) {
        // SDLActivity calls this with the SDL surface.
        // We wrap it in a FrameLayout and overlay our touch controls.
        FrameLayout container = new FrameLayout(this);
        container.addView(view, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mTouchOverlay = new TouchOverlayView(this);
        container.addView(mTouchOverlay, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        super.setContentView(container);
    }

    /**
     * Tell SDL where the game native library is and what it's called.
     */
    @Override
    protected String[] getLibraries() {
        return new String[]{
                "SDL2",
                "main"   // Our game .so built by CMake
        };
    }

    /**
     * Pass content dir path as the first argument to SDL_main / main().
     */
    @Override
    protected String[] getArguments() {
        String filesDir = getFilesDir().getAbsolutePath();
        return new String[]{
                "--contentdir=" + filesDir + "/content",
                "--configdir=" + filesDir + "/config",
                "--res=320x240",
                "-f"
        };
    }

    private void enableImmersiveMode() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    /**
     * Extract game content from APK assets to internal storage on first run.
     */
    private void extractAssets() {
        File contentDir = new File(getFilesDir(), "content");
        File marker = new File(contentDir, ".extracted_v0.8.0");

        if (marker.exists()) {
            Log.i(TAG, "Content already extracted");
            return;
        }

        Log.i(TAG, "Extracting game content...");
        contentDir.mkdirs();

        // Create standard directories
        new File(getFilesDir(), "config").mkdirs();
        new File(getFilesDir(), "save").mkdirs();
        new File(getFilesDir(), "save/teams").mkdirs();

        try {
            AssetManager am = getAssets();
            copyAssetDir(am, "content", contentDir.getAbsolutePath());

            // Create version marker
            marker.createNewFile();
            Log.i(TAG, "Content extraction complete");
        } catch (IOException e) {
            Log.e(TAG, "Failed to extract assets", e);
        }
    }

    private void copyAssetDir(AssetManager am, String assetPath, String destPath)
            throws IOException {
        String[] items = am.list(assetPath);
        if (items == null || items.length == 0) {
            // It's a file — copy it
            copyAssetFile(am, assetPath, destPath);
            return;
        }

        // It's a directory — recurse
        new File(destPath).mkdirs();
        for (String item : items) {
            copyAssetDir(am, assetPath + "/" + item, destPath + "/" + item);
        }
    }

    private void copyAssetFile(AssetManager am, String assetPath, String destPath)
            throws IOException {
        InputStream in = am.open(assetPath);
        OutputStream out = new FileOutputStream(destPath);
        byte[] buf = new byte[8192];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
