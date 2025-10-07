package com.example.myquickhsk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Flashcard extends AppCompatActivity {

    private AppDatabase db;
    private TermMetadataDAO dao;
    private TextToSpeech tts;
    private Button flashcardTTSButton;

    private ArrayList<String> hanziList;
    private ArrayList<String> pinyinList;
    private ArrayList<String> defList;
    private int currentIndex = 0;

    private boolean isReviewMode;

    private TextView reviewPrompt, mainTermView, mainTermPinyinView, reviewCurrentIndex;

    private LinearLayout defRow1, defRow2, defRow3;
    private TextView reviewDefinition1, reviewDefinition2, reviewDefinition3;

    private String singleMainTerm;
    private ArrayList<String> defPoolList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard);

        Button backButton = findViewById(R.id.backButton);
        Button flashcardNotesEntryButton = findViewById(R.id.flashcard_notes_entry_button);
        Button flashcardFavoriteEntryButton = findViewById(R.id.flashcard_favorite_entry_button);
        flashcardTTSButton = findViewById(R.id.flashcard_TTS_button);

        mainTermView = findViewById(R.id.flashcard_main_term);
        mainTermPinyinView = findViewById(R.id.flashcard_main_term_pinyin_subscript);
        TextView definitionView = findViewById(R.id.flashcard_definition);

        reviewCurrentIndex = findViewById(R.id.review_current_index);
        reviewPrompt = findViewById(R.id.review_prompt);

        defRow1 = findViewById(R.id.review_def1_row);
        defRow2 = findViewById(R.id.review_def2_row);
        defRow3 = findViewById(R.id.review_def3_row);
        reviewDefinition1 = findViewById(R.id.review_definition1);
        reviewDefinition2 = findViewById(R.id.review_definition2);
        reviewDefinition3 = findViewById(R.id.review_definition3);

        isReviewMode = getIntent().getBooleanExtra("reviewMode", false);

        if (isReviewMode) {

            flashcardNotesEntryButton.setVisibility(View.GONE);
            flashcardFavoriteEntryButton.setVisibility(View.GONE);
            definitionView.setVisibility(View.GONE);

            reviewCurrentIndex.setVisibility(View.VISIBLE);
            reviewPrompt.setVisibility(View.VISIBLE);
            reviewDefinition1.setVisibility(View.VISIBLE);
            reviewDefinition2.setVisibility(View.VISIBLE);
            reviewDefinition3.setVisibility(View.VISIBLE);

            ConstraintLayout reviewOverlay = findViewById(R.id.review_definition_overlay);
            TextView reviewFullText = findViewById(R.id.review_full_text);
            Button reviewOverlayClose = findViewById(R.id.review_overlay_close);

            reviewOverlayClose.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    reviewOverlay.setVisibility(View.GONE);
                }

            });

            ImageButton expand1 = findViewById(R.id.review_expand1);
            ImageButton expand2 = findViewById(R.id.review_expand2);
            ImageButton expand3 = findViewById(R.id.review_expand3);

            expand1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    reviewFullText.setText(reviewDefinition1.getText().toString());
                    reviewOverlay.setVisibility(View.VISIBLE);

                }

            });

            expand2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    reviewFullText.setText(reviewDefinition2.getText().toString());
                    reviewOverlay.setVisibility(View.VISIBLE);

                }

            });

            expand3.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    reviewFullText.setText(reviewDefinition3.getText().toString());
                    reviewOverlay.setVisibility(View.VISIBLE);

                }

            });

            hanziList = getIntent().getStringArrayListExtra("reviewHanzi");
            pinyinList = getIntent().getStringArrayListExtra("reviewPinyin");
            defList = getIntent().getStringArrayListExtra("reviewDef");
            defPoolList = getIntent().getStringArrayListExtra("reviewDefPool");
            currentIndex = getIntent().getIntExtra("currentIndex", 0);

            db = Room.databaseBuilder(
                    getApplicationContext(),
                    AppDatabase.class,
                    "main-db"
            ).build();

            dao = db.termMetadataDAO();

            showCard(currentIndex);

        }

        else {

            defRow1.setVisibility(View.GONE);
            defRow2.setVisibility(View.GONE);
            defRow3.setVisibility(View.GONE);

            singleMainTerm = getIntent().getStringExtra("hanzi");
            String mainTermPinyin = getIntent().getStringExtra("pinyin");
            String definition = getIntent().getStringExtra("definition");

            mainTermView.setText(singleMainTerm);
            mainTermPinyinView.setText(mainTermPinyin);
            definitionView.setText(definition);

        }

        tts = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {

                int result = tts.setLanguage(Locale.CHINA);

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(Locale.US);
                }

            }

        });

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "main-db"
        ).build();

        dao = db.termMetadataDAO();

        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });

        flashcardNotesEntryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!isReviewMode && singleMainTerm != null) {

                    Intent intent = new Intent(Flashcard.this, Notes.class);
                    intent.putExtra("hanzi", singleMainTerm);
                    startActivity(intent);

                }

            }

        });

        flashcardFavoriteEntryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!isReviewMode && singleMainTerm != null) {

                    new Thread(() -> {

                        TermMetadata metadata = dao.getByHanziSync(singleMainTerm);

                        if (metadata == null) {

                            metadata = new TermMetadata();
                            metadata.hanzi = singleMainTerm;
                            metadata.isFavorite = true;
                            dao.insert(metadata);

                        }

                        else {

                            metadata.isFavorite = !metadata.isFavorite;
                            dao.update(metadata);

                        }

                    }).start();

                }

            }

        });

        flashcardTTSButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (tts != null) {

                    tts.setSpeechRate(0.7f);
                    String text = mainTermView.getText().toString();

                    if (text != null && !text.isEmpty()) {
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                }

            }

        });

    }

    private void showCard(int index) {

        if (index >= hanziList.size()) {

            mainTermView.setText("🎉 Done!");
            mainTermPinyinView.setText("");
            reviewCurrentIndex.setText(hanziList.size() + "/" + hanziList.size());
            flashcardTTSButton.setVisibility(View.GONE);
            reviewPrompt.setVisibility(View.GONE);
            defRow1.setVisibility(View.GONE);
            defRow2.setVisibility(View.GONE);
            defRow3.setVisibility(View.GONE);

            mainTermView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    finish();
                }

            }, 1500);

            return;

        }

        String hanzi = hanziList.get(index);
        String pinyin = pinyinList.get(index);
        String correctDef = defList.get(index);

        mainTermView.setText(hanzi);
        mainTermPinyinView.setText(pinyin);
        reviewCurrentIndex.setText((index + 1) + "/" + hanziList.size());

        List<String> options = new ArrayList<>();
        options.add(correctDef);

        List<String> distractors = new ArrayList<>(defPoolList);
        distractors.remove(correctDef);
        Collections.shuffle(distractors);

        for (int i = 0; i < 2 && i < distractors.size(); i++) {
            options.add(distractors.get(i));
        }

        Collections.shuffle(options);

        reviewDefinition1.setText(options.size() > 0 ? options.get(0) : "");
        reviewDefinition2.setText(options.size() > 1 ? options.get(1) : "");
        reviewDefinition3.setText(options.size() > 2 ? options.get(2) : "");

        reviewDefinition1.setBackgroundColor(Color.parseColor("#55000000"));
        reviewDefinition2.setBackgroundColor(Color.parseColor("#55000000"));
        reviewDefinition3.setBackgroundColor(Color.parseColor("#55000000"));

        View.OnClickListener choiceListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                TextView chosen = (TextView) v;

                if (chosen.getText().toString().equals(correctDef)) {
                    chosen.setBackgroundColor(Color.parseColor("#228B22"));
                }

                else {
                    chosen.setBackgroundColor(Color.parseColor("#B22222"));
                }

                new Thread(() -> {

                    TermMetadata meta = dao.getByHanziSync(hanzi);

                    if (meta == null) {

                        meta = new TermMetadata();
                        meta.hanzi = hanzi;
                        meta.practiceCount = 1;
                        dao.insert(meta);

                    }

                    else {

                        meta.practiceCount += 1;
                        dao.update(meta);

                    }

                }).start();

                chosen.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        reviewDefinition1.setBackgroundColor(Color.TRANSPARENT);
                        reviewDefinition2.setBackgroundColor(Color.TRANSPARENT);
                        reviewDefinition3.setBackgroundColor(Color.TRANSPARENT);
                        currentIndex++;
                        showCard(currentIndex);

                    }

                }, 800);

            }

        };

        reviewDefinition1.setOnClickListener(choiceListener);
        reviewDefinition2.setOnClickListener(choiceListener);
        reviewDefinition3.setOnClickListener(choiceListener);

    }

    @Override
    protected void onDestroy() {

        if (tts != null) {

            tts.stop();
            tts.shutdown();

        }

        super.onDestroy();

    }

}
