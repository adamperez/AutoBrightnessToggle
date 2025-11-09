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

    private void requestWriteSettingsPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView instructions = findViewById(R.id.instructions);
        Button grantButton = findViewById(R.id.grantPermissionButton);


        if (Settings.System.canWrite(this)) {
            instructions.setText("Permission granted! You can now add the Auto Brightness tile to your Quick Settings.");
            grantButton.setEnabled(false);
            grantButton.setText("Permission Already Granted");
        } else {
            instructions.setText("This app needs permission to modify system settings to toggle Auto Brightness.\n\nTap the button below to grant permission.");
            grantButton.setOnClickListener(v -> requestWriteSettingsPermission());
        }
    }
}