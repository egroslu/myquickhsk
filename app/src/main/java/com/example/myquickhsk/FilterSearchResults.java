package com.example.myquickhsk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FilterSearchResults extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_search_results);

        RecyclerView recyclerView = findViewById(R.id.filter_search_results_card_recycler);
        View loadingOverlay = findViewById(R.id.loadingOverlay);

        final String practiceFilter = getIntent().getStringExtra("practiceFilter") == null ?
                "none" : getIntent().getStringExtra("practiceFilter");

        final String searchMode = getIntent().getStringExtra("searchMode") == null ?
                "chinese" : getIntent().getStringExtra("searchMode");

        final ArrayList<String> HSK_levels = getIntent().getStringArrayListExtra("HSK_levels") == null ?
                new ArrayList<>() : getIntent().getStringArrayListExtra("HSK_levels");

        final String searchTerm = getIntent().getStringExtra("searchTerm") == null ?
                "" : getIntent().getStringExtra("searchTerm");

        final boolean favoritesOnly = getIntent().getBooleanExtra("favoritesOnly", false);
        final boolean exactOnly = getIntent().getBooleanExtra("exactOnly", false);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "main-db"
        ).build();

        TermMetadataDAO dao = db.termMetadataDAO();

        List<FilterSearchResultsCard> cards = new ArrayList<>();
        FilterSearchAdapter adapter = new FilterSearchAdapter(cards);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(v -> {

            int position = (int) v.getTag();
            FilterSearchResultsCard clickedCard = adapter.getItem(position);

            Intent intent = new Intent(FilterSearchResults.this, Flashcard.class);
            intent.putExtra("hanzi", clickedCard.getScript());
            intent.putExtra("pinyin", clickedCard.getPinyin());
            intent.putExtra("definition", clickedCard.getFullDefinition());
            startActivity(intent);

        });

        new Thread(() -> {

            List<FilterSearchResultsCard> results = new ArrayList<>();

            if (favoritesOnly && HSK_levels.isEmpty() && searchTerm.isEmpty()) {

                List<TermMetadata> favs = dao.getAllFavoritesSync();
                for (TermMetadata metadata : favs) {
                    MyQuickHSKApp.CCEntry entry = MyQuickHSKApp.ccDict.get(metadata.hanzi);
                    if (entry == null) continue;

                    String pinyin = MyQuickHSKApp.convertPinyinNumbersToAccents(entry.getPinyin());
                    String def = entry.getDefinition();
                    String displayDef = def.length() > 50 ? def.substring(0, 50) + "..." : def;

                    results.add(new FilterSearchResultsCard(metadata.hanzi, pinyin, displayDef, def));

                }

            }

            else {

                for (String level : HSK_levels) {

                    Iterable<String> sourceTerms = new ArrayList<>();
                    switch (level) {

                        case "HSK1": sourceTerms = MyQuickHSKApp.hsk1Terms; break;
                        case "HSK2": sourceTerms = MyQuickHSKApp.hsk2Terms; break;
                        case "HSK3": sourceTerms = MyQuickHSKApp.hsk3Terms; break;
                        case "HSK4": sourceTerms = MyQuickHSKApp.hsk4Terms; break;
                        case "HSK5": sourceTerms = MyQuickHSKApp.hsk5Terms; break;
                        case "HSK6": sourceTerms = MyQuickHSKApp.hsk6Terms; break;
                        case "HSK7": sourceTerms = MyQuickHSKApp.hsk789Terms; break;
                        case "HSK10+": sourceTerms = MyQuickHSKApp.ccDict.keySet();
                            runOnUiThread(() -> loadingOverlay.setVisibility(View.VISIBLE));
                            break;

                    }

                    for (String term : sourceTerms) {

                        MyQuickHSKApp.CCEntry entry = MyQuickHSKApp.ccDict.get(term);
                        if (entry == null) continue;

                        TermMetadata metadata = dao.getByHanziSync(term);
                        if (favoritesOnly && (metadata == null || !metadata.isFavorite)) continue;
                        if (practiceFilter.equals("unpracticed") && metadata != null && metadata.practiceCount > 0) continue;

                        boolean matches = entryMatches(term, entry, searchTerm, searchMode, exactOnly);
                        if (!matches) continue;

                        String pinyin = MyQuickHSKApp.convertPinyinNumbersToAccents(entry.getPinyin());
                        String def = entry.getDefinition();
                        String displayDef = def.length() > 50 ? def.substring(0, 50) + "..." : def;

                        results.add(new FilterSearchResultsCard(term, pinyin, displayDef, def));

                    }

                }

            }

            if (practiceFilter.equals("unpracticed")) {

                Iterator<FilterSearchResultsCard> iterator = results.iterator();

                while (iterator.hasNext()) {

                    FilterSearchResultsCard card = iterator.next();
                    TermMetadata meta = dao.getByHanziSync(card.getScript());

                    if (meta != null && meta.practiceCount > 0) {
                        iterator.remove();
                    }

                }

            }

            runOnUiThread(() -> {

                cards.clear();
                cards.addAll(results);
                adapter.notifyDataSetChanged();
                loadingOverlay.setVisibility(View.GONE);

            });

        }).start();

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

    }

    private static boolean hasAccentedPinyin(String s) {
        return s.matches(".*[āáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜ].*");
    }

    private boolean matchesPinyinPartial(String[] entryTokens, String queryRaw) {

        String[] queryTokens = MyQuickHSKApp.normKeepLettersSpaces(queryRaw).split("\\s+");
        if (queryTokens.length == 0) return true;

        int i = 0, j = 0;
        while (i < queryTokens.length && j < entryTokens.length) {

            if (entryTokens[j].startsWith(queryTokens[i])) {

                i++;
                j++;

            }

            else {
                j++;
            }

        }

        return i == queryTokens.length;

    }

    private boolean matchesPinyinExact(String[] entryTokens, String queryRaw) {

        String[] queryTokens = MyQuickHSKApp.normKeepLettersSpaces(queryRaw).split("\\s+");

        if (queryTokens.length == 0) return false;
        if (queryTokens.length != entryTokens.length) return false;

        for (int i = 0; i < queryTokens.length; i++) {
            if (!entryTokens[i].equals(queryTokens[i])) return false;
        }

        return true;

    }

    private boolean entryMatches(String term,
                                 MyQuickHSKApp.CCEntry entry,
                                 String searchTerm,
                                 String searchMode,
                                 boolean exactOnly) {

        String pinyin = MyQuickHSKApp.convertPinyinNumbersToAccents(entry.getPinyin());
        String def = entry.getDefinition();

        if (searchMode.equals("chinese")) {

            if (searchTerm.isEmpty() || term.contains(searchTerm)) return true;

            else if (hasAccentedPinyin(searchTerm)) {

                String normEntryAccented = pinyin.toLowerCase().replaceAll("\\s+", "");
                String normQueryAccented = searchTerm.toLowerCase().replaceAll("\\s+", "");
                return normEntryAccented.contains(normQueryAccented);

            }

            else if (searchTerm.matches(".*[a-zA-ZüÜ].*")) {

                return exactOnly
                        ? matchesPinyinExact(entry.getNormTokens(), searchTerm)
                        : matchesPinyinPartial(entry.getNormTokens(), searchTerm);

            }

        }

        else if (searchMode.equals("english")) {
            return searchTerm.isEmpty() || def.toLowerCase().contains(searchTerm.toLowerCase());
        }

        return false;

    }

}
