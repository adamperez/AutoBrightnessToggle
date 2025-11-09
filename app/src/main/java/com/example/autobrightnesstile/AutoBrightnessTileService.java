package com.example.autobrightnesstile;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class AutoBrightnessTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTileState();
    }

    @Override
    public void onClick() {
        super.onClick();

        // Check if we have permission to modify system settings
        if (!Settings.System.canWrite(this)) {
            // Open MainActivity to request permission
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityAndCollapse(intent);
            return;
        }

        // Update UI immediately for instant feedback
        Tile tile = getQsTile();
        if (tile != null) {
            // Toggle the visual state immediately
            boolean willBeActive = (tile.getState() != Tile.STATE_ACTIVE);
            tile.setState(willBeActive ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.setSubtitle(willBeActive ? "On" : "Off");
            tile.updateTile();
        }

        // Toggle auto brightness in background
        toggleAutoBrightness();

        // Verify the actual state after a short delay
        new android.os.Handler().postDelayed(this::updateTileState, 100);
    }

    private void toggleAutoBrightness() {
        try {
            int currentMode = Settings.System.getInt(
                    getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE
            );

            int newMode = (currentMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
                    ? Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                    : Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;

            Settings.System.putInt(
                    getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    newMode
            );
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateTileState() {
        Tile tile = getQsTile();
        if (tile == null) return;

        try {
            int mode = Settings.System.getInt(
                    getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE
            );

            boolean isAutoEnabled = (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);

            // Update tile state
            tile.setState(isAutoEnabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.setLabel("Auto Brightness");
            tile.setSubtitle(isAutoEnabled ? "On" : "Off");

            // Update icon (you'll need to create these drawable resources)
            tile.setIcon(Icon.createWithResource(this,
                    isAutoEnabled ? R.drawable.ic_brightness_auto : R.drawable.ic_brightness_manual));

            tile.updateTile();
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            tile.setState(Tile.STATE_UNAVAILABLE);
            tile.updateTile();
        }
    }
}