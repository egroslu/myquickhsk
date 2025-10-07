package com.example.myquickhsk;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class FilterSearchSplash extends AppCompatActivity {

    class TermTriple {

        String hanzi;
        String pinyin;
        String def;
        TermTriple(String h, String p, String d) {

            hanzi = h;
            pinyin = p;
            def = d;

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_search_splash);


        Button backButton = findViewById(R.id.backButton);

        SwitchMaterial unpracticedSwitch = findViewById(R.id.unpracticed_switch);

        SwitchMaterial favoritesSwitch = findViewById(R.id.favorites_switch);

        CheckBox hsk1Box = findViewById(R.id.hsk1_checkbox);
        CheckBox hsk2Box = findViewById(R.id.hsk2_checkbox);
        CheckBox hsk3Box = findViewById(R.id.hsk3_checkbox);
        CheckBox hsk4Box = findViewById(R.id.hsk4_checkbox);
        CheckBox hsk5Box = findViewById(R.id.hsk5_checkbox);
        CheckBox hsk6Box = findViewById(R.id.hsk6_checkbox);
        CheckBox hsk7Box = findViewById(R.id.hsk7_checkbox);
        CheckBox hsk10Box = findViewById(R.id.hsk10_checkbox);

        RadioGroup searchModeGroup = findViewById(R.id.search_mode_group);
        CheckBox exactMatchCheck = findViewById(R.id.exact_match_check);

        String mode = getIntent().getStringExtra("mode");
        EditText searchBar = findViewById(R.id.search_bar);
        Spinner termsCount = findViewById(R.id.terms_count_spinner);

        Button allDictionaryButton = findViewById(R.id.set_terms_button);

        ArrayList<String> termsCountOptions = new ArrayList<>();
        termsCountOptions.add("5 TERMS");
        termsCountOptions.add("10 TERMS");
        termsCountOptions.add("20 TERMS");
        termsCountOptions.add("50 TERMS");
        termsCountOptions.add("100 TERMS");

        ArrayAdapter<String> termsCountOptionsAdapter = new ArrayAdapter<>(this, R.layout.formatted_spinner_item, termsCountOptions);
        termsCountOptionsAdapter.setDropDownViewResource(R.layout.formatted_spinner_dropdown_item);
        termsCount.setAdapter(termsCountOptionsAdapter);


        if ("review".equals(mode)) {

            searchBar.setVisibility(View.GONE);
            termsCount.setVisibility(View.VISIBLE);
            hsk10Box.setVisibility(View.GONE);
            searchModeGroup.setVisibility(View.GONE);
            exactMatchCheck.setVisibility(View.GONE);

        }

        else {

            searchBar.setVisibility(View.VISIBLE);
            termsCount.setVisibility(View.GONE);

        }


        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.backButton) {

                    finish();

                }


                else if (v.getId() == R.id.set_terms_button) {

                    ArrayList<String> selectedLevels = new ArrayList<>();
                    if (hsk1Box.isChecked()) selectedLevels.add("HSK1");
                    if (hsk2Box.isChecked()) selectedLevels.add("HSK2");
                    if (hsk3Box.isChecked()) selectedLevels.add("HSK3");
                    if (hsk4Box.isChecked()) selectedLevels.add("HSK4");
                    if (hsk5Box.isChecked()) selectedLevels.add("HSK5");
                    if (hsk6Box.isChecked()) selectedLevels.add("HSK6");
                    if (hsk7Box.isChecked()) selectedLevels.add("HSK7");
                    if (hsk10Box.isChecked()) selectedLevels.add("HSK10+");

                    boolean favoritesOnly = favoritesSwitch.isChecked();
                    boolean unpracticedOnly = unpracticedSwitch.isChecked();

                    if ("review".equals(mode)) {
                        AppDatabase db = Room.databaseBuilder(
                                getApplicationContext(),
                                AppDatabase.class,
                                "main-db"
                        ).build();
                        TermMetadataDAO dao = db.termMetadataDAO();

                        Intent intent = new Intent(FilterSearchSplash.this, Flashcard.class);
                        intent.putExtra("practiceFilter", unpracticedOnly ? "unpracticed" : "none");
                        intent.putExtra("reviewMode", true);

                        String selected = termsCount.getSelectedItem().toString();
                        int count = 5;
                        if (selected.startsWith("10 T")) count = 10;
                        else if (selected.startsWith("20")) count = 20;
                        else if (selected.startsWith("50")) count = 50;
                        else if (selected.startsWith("100")) count = 100;
                        final int finalCount = count;

                        new Thread(() -> {
                            ArrayList<TermTriple> eligibleTerms = new ArrayList<>();

                            for (Map.Entry<String, MyQuickHSKApp.CCEntry> mapEntry : MyQuickHSKApp.ccDict.entrySet()) {
                                String hanzi = mapEntry.getKey();
                                MyQuickHSKApp.CCEntry entry = mapEntry.getValue();

                                boolean include = false;
                                if (selectedLevels.contains("HSK1") && MyQuickHSKApp.hsk1Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK2") && MyQuickHSKApp.hsk2Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK3") && MyQuickHSKApp.hsk3Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK4") && MyQuickHSKApp.hsk4Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK5") && MyQuickHSKApp.hsk5Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK6") && MyQuickHSKApp.hsk6Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK7") && MyQuickHSKApp.hsk789Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK10+")) include = true;

                                if (!include) continue;

                                TermMetadata meta = dao.getByHanziSync(hanzi);
                                if (favoritesOnly && (meta == null || !meta.isFavorite)) continue;
                                int countPracticed = meta == null ? 0 : meta.practiceCount;
                                if (unpracticedOnly && countPracticed > 0) continue;

                                eligibleTerms.add(new TermTriple(hanzi, entry.getPinyin(), entry.getDefinition()));
                            }

                            Collections.shuffle(eligibleTerms);
                            ArrayList<String> sampleHanzi = new ArrayList<>();
                            ArrayList<String> samplePinyin = new ArrayList<>();
                            ArrayList<String> sampleDef   = new ArrayList<>();

                            for (int i = 0; i < Math.min(finalCount, eligibleTerms.size()); i++) {
                                TermTriple t = eligibleTerms.get(i);
                                sampleHanzi.add(t.hanzi);
                                samplePinyin.add(MyQuickHSKApp.convertPinyinNumbersToAccents(t.pinyin));
                                sampleDef.add(t.def);
                            }

                            ArrayList<String> defPool = new ArrayList<>();
                            for (Map.Entry<String, MyQuickHSKApp.CCEntry> mapEntry : MyQuickHSKApp.ccDict.entrySet()) {
                                String hanzi = mapEntry.getKey();
                                MyQuickHSKApp.CCEntry entry = mapEntry.getValue();

                                boolean include = false;
                                if (selectedLevels.contains("HSK1") && MyQuickHSKApp.hsk1Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK2") && MyQuickHSKApp.hsk2Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK3") && MyQuickHSKApp.hsk3Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK4") && MyQuickHSKApp.hsk4Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK5") && MyQuickHSKApp.hsk5Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK6") && MyQuickHSKApp.hsk6Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK7") && MyQuickHSKApp.hsk789Terms.contains(hanzi)) include = true;
                                if (selectedLevels.contains("HSK10+")) include = true;

                                if (!include) continue;

                                defPool.add(entry.getDefinition());
                            }

                            Collections.shuffle(defPool);
                            int poolSize = Math.min(defPool.size(), finalCount * 3);
                            ArrayList<String> finalDefPool = new ArrayList<>(defPool.subList(0, poolSize));

                            db.close();

                            runOnUiThread(() -> {
                                intent.putStringArrayListExtra("reviewHanzi", sampleHanzi);
                                intent.putStringArrayListExtra("reviewPinyin", samplePinyin);
                                intent.putStringArrayListExtra("reviewDef", sampleDef);
                                intent.putStringArrayListExtra("reviewDefPool", finalDefPool);
                                intent.putExtra("currentIndex", 0);
                                startActivity(intent);
                            });
                        }).start();
                    }


                    else if ("dictionary".equals(mode)) {

                        Intent intent = new Intent(FilterSearchSplash.this, FilterSearchResults.class);

                        int checkedId = searchModeGroup.getCheckedRadioButtonId();
                        String searchMode = "chinese";

                        if (checkedId == R.id.search_english) {
                            searchMode = "english";
                        }

                        intent.putExtra("practiceFilter", unpracticedOnly ? "unpracticed" : "none");
                        intent.putExtra("searchMode", searchMode);
                        intent.putExtra("searchTerm", searchBar.getText().toString());
                        intent.putStringArrayListExtra("HSK_levels", selectedLevels);
                        intent.putExtra("favoritesOnly", favoritesOnly);
                        intent.putExtra("exactOnly", exactMatchCheck.isChecked());

                        startActivity(intent);

                    }

                }

            }

        };


        backButton.setOnClickListener(listener);
        allDictionaryButton.setOnClickListener(listener);


    }

}
