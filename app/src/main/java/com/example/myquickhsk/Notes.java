package com.example.myquickhsk;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.room.Room;

public class Notes extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_activity);

        String headerHanzi = getIntent().getStringExtra("hanzi");
        TextView headerHanziView = findViewById(R.id.notes_header_hanzi);
        headerHanziView.setText(headerHanzi);

        EditText notesEditText = findViewById(R.id.notes_edit_text);

        Button backButton = findViewById(R.id.backButton);
        Button saveButton = findViewById(R.id.save_button);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "main-db"
        ).build();

        TermMetadataDAO dao = db.termMetadataDAO();

        dao.getByHanzi(headerHanzi).observe(this, new Observer<TermMetadata>() {

            @Override
            public void onChanged(TermMetadata metadata) {

                if (metadata != null) {
                    notesEditText.setText(metadata.notes);
                }

            }

        });

        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.backButton) {
                    finish();
                }

                else if (v.getId() == R.id.save_button) {

                    String newNotes = notesEditText.getText().toString();
                    TermMetadata metadata = new TermMetadata();
                    metadata.hanzi = headerHanzi;
                    metadata.notes = newNotes;

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            db.termMetadataDAO().insert(metadata);
                        }

                    }).start();

                }

            }

        };

        backButton.setOnClickListener(listener);
        saveButton.setOnClickListener(listener);

    }

}
