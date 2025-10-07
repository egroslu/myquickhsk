package com.example.myquickhsk;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DeleteDatabase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_data);

        Button deleteButton = findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean deleted = deleteDatabase("main-db");

                if (deleted) {

                    Toast.makeText(getApplicationContext(),
                            "Database deleted successfully!",
                            Toast.LENGTH_SHORT).show();
                    finish();

                }

                else {

                    Toast.makeText(getApplicationContext(),
                            "Database not found / delete failed.",
                            Toast.LENGTH_SHORT).show();

                }

            }

        });

    }
}
