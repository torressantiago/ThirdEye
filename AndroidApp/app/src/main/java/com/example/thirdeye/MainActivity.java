package com.example.thirdeye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    // Tag pour les messages dans les logs
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start foreground service that will schedule the various Jobs.
        startService(new Intent(this, ForegroundService.class));
    }
}