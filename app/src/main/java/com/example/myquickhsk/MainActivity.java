package com.example.myquickhsk;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.view.View;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


public class MainActivity extends AppCompatActivity {

    private TextView welcomeText, welcomeTextSubscript;
    private Button helloButton;
    private ConstraintLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingOverlay = findViewById(R.id.welcome_loading_overlay);
        ProgressBar progressBar = findViewById(R.id.welcome_loading_progress_bar);
        TextView progressPercent = findViewById(R.id.welcome_progress_percent);

        welcomeText = findViewById(R.id.welcomeText);
        welcomeTextSubscript = findViewById(R.id.welcomeTextSubscript);
        helloButton = findViewById(R.id.helloButton);

        boolean alreadyLoaded = !MyQuickHSKApp.ccDict.isEmpty();
        loadingOverlay.setVisibility(alreadyLoaded ? View.GONE : View.VISIBLE);

        helloButton.setEnabled(alreadyLoaded);

        new Thread(() -> {

            boolean ok = MyQuickHSKApp.ensureDictLoaded(
                    getApplicationContext(),
                    progressBar,
                    progressPercent
            );

            runOnUiThread(() -> {

                loadingOverlay.setVisibility(View.GONE);
                helloButton.setEnabled(ok);

                if (!ok) {

                    welcomeText.setText("Load failed");
                    welcomeTextSubscript.setText("Check assets/cc-cedict.txt");

                }

            });

        }).start();


        helloButton.setOnClickListener(v -> {

            welcomeText.setBackgroundResource(R.drawable.rounded_bg);
            welcomeText.setText("WELCOME");
            helloButton.setText("你好");

            Intent intent = new Intent(MainActivity.this, MainSplash.class);
            Handler handler = new Handler(Looper.getMainLooper());

            handler.postDelayed(() -> {

                welcomeText.setText("欢迎");
                welcomeTextSubscript.setText("Huānyíng");

            }, 1500);

            handler.postDelayed(() -> startActivity(intent), 2500);

        });
    }

    @Override
    protected void onResume() {

        super.onResume();

        welcomeText.setText("");
        welcomeText.setBackground(null);
        welcomeTextSubscript.setText("");
        welcomeTextSubscript.setBackground(null);
        helloButton.setText("HELLO");

    }

}
