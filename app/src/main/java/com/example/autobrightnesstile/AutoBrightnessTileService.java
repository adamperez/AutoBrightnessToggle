package com.example.autobrightnesstile;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class AutoBrightnessTileService extends TileService {

    private final android.os.Handler handler = new android.os.Handler();
    private Runnable pendingUpdate;

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

        // Toggle auto brightness first
        toggleAutoBrightness();

        // Then update UI to match
        updateTileState();
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