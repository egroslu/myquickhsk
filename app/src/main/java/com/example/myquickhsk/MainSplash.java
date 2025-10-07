package com.example.myquickhsk;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_splash);


        Button backButton = findViewById(R.id.backButton);

        Button resetDataButton = findViewById(R.id.reset_data_button);

        Button reviewButton = findViewById(R.id.review_button);
        Button dictionaryButton = findViewById(R.id.dictionary_button);


        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.backButton) {

                    finish();

                }

                else if (v.getId() == R.id.review_button) {

                    Intent intent = new Intent(MainSplash.this, FilterSearchSplash.class);
                    intent.putExtra("mode", "review");
                    startActivity(intent);

                }

                else if (v.getId() == R.id.dictionary_button) {

                    Intent intent = new Intent(MainSplash.this, FilterSearchSplash.class);
                    intent.putExtra("mode", "dictionary");
                    startActivity(intent);

                }

                else if (v.getId() == R.id.reset_data_button) {

                    Intent intent = new Intent(MainSplash.this, DeleteDatabase.class);
                    startActivity(intent);

                }

            }

        };


        backButton.setOnClickListener(listener);
        reviewButton.setOnClickListener(listener);
        dictionaryButton.setOnClickListener(listener);
        resetDataButton.setOnClickListener(listener);


    }

}
