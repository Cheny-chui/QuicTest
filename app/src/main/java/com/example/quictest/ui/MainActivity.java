package com.example.quictest.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quictest.R;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    public enum TestMode{
        RTT, DOWNLOAD
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();

        setUpButton();

    }

    private void setUpToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(R.string.app_name);
    }

    private void setUpButton(){
        Button rttButton = findViewById(R.id.rtt_button);
        Button downloadButton = findViewById(R.id.download_button);
        rttButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                intent.putExtra("testMode",TestMode.RTT);
                startActivity(intent);
            }
        });
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TestActivity.class);
                intent.putExtra("testMode",TestMode.DOWNLOAD);
                startActivity(intent);
            }
        });
    }

}