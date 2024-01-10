package com.example.app2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        (new Handler()).postDelayed(this::waitFunc, 10000);
    }

    public void waitFunc() {
        Intent intent = new Intent(LoadingActivity.this, ResultActivity.class);
        startActivity(intent);
    }
}