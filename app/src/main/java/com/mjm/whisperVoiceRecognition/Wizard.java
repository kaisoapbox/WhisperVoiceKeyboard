package com.mjm.whisperVoiceRecognition;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.WhisperVoiceKeyboard.R;

public class Wizard extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard);

        Button btnEnableInputMethod = findViewById(R.id.btnEnableInputMethod);
        Button btnGrantMicrophonePermission = findViewById(R.id.btnGrantMicrophonePermission);
        Button btnConfigureSettings = findViewById(R.id.btnConfigurePreferences);
        Button btnExit = findViewById(R.id.btnExit);

        btnEnableInputMethod.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            startActivity(intent);
        });

        btnGrantMicrophonePermission.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(Wizard.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        });

        btnConfigureSettings.setOnClickListener(v -> {
            Intent i = new Intent(Wizard.this, Preferences.class);
            startActivity(i);
        });

        btnExit.setOnClickListener(v -> {
            Toast.makeText(this, R.string.wizard_completed, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}