package com.example.autobrightnesstile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_WRITE_SETTINGS = 1001;
    private Button toggleButton;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView instructions = findViewById(R.id.instructions);
        Button grantButton = findViewById(R.id.grantPermissionButton);
        toggleButton = findViewById(R.id.toggleBrightnessButton);
        statusText = findViewById(R.id.statusText);

        if (Settings.System.canWrite(this)) {
            instructions.setText("Permission granted! You can now add the Auto Brightness tile to your Quick Settings.");
            grantButton.setEnabled(false);
            grantButton.setText("Permission Already Granted");

            // Enable the toggle button
            toggleButton.setEnabled(true);
            toggleButton.setOnClickListener(v -> toggleAutoBrightness());
            updateBrightnessStatus();
        } else {
            instructions.setText("This app needs permission to modify system settings to toggle Auto Brightness.\n\nTap the button below to grant permission.");
            grantButton.setOnClickListener(v -> requestWriteSettingsPermission());
            toggleButton.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Settings.System.canWrite(this)) {
            updateBrightnessStatus();
        }
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

            updateBrightnessStatus();
            Toast.makeText(this, "Auto Brightness " + (newMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error toggling brightness", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBrightnessStatus() {
        try {
            int mode = Settings.System.getInt(
                    getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE
            );

            boolean isAutoEnabled = (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
            statusText.setText("Auto Brightness is currently: " + (isAutoEnabled ? "ON" : "OFF"));
            toggleButton.setText(isAutoEnabled ? "Turn OFF" : "Turn ON");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            statusText.setText("Unable to read brightness status");
        }
    }

    private void requestWriteSettingsPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(this)) {
                Toast.makeText(this, "Permission granted! Add the tile from Quick Settings.", Toast.LENGTH_LONG).show();
                recreate(); // Refresh the activity
            } else {
                Toast.makeText(this, "Permission denied. The tile won't work without this permission.", Toast.LENGTH_LONG).show();
            }
        }
    }
}