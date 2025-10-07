package com.example.myquickhsk;

import android.app.Application;
import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MyQuickHSKApp extends Application {

    public static final Map<String, CCEntry> ccDict = new HashMap<>();
    public static Set<String> hsk1Terms;
    public static Set<String> hsk2Terms;
    public static Set<String> hsk3Terms;
    public static Set<String> hsk4Terms;
    public static Set<String> hsk5Terms;
    public static Set<String> hsk6Terms;
    public static Set<String> hsk789Terms;

    private static boolean dictLoaded = false;

    @Override
    public void onCreate() {

        super.onCreate();

        hsk1Terms = loadHSKList(R.raw.hsk1);
        hsk2Terms = loadHSKList(R.raw.hsk2);
        hsk3Terms = loadHSKList(R.raw.hsk3);
        hsk4Terms = loadHSKList(R.raw.hsk4);
        hsk5Terms = loadHSKList(R.raw.hsk5);
        hsk6Terms = loadHSKList(R.raw.hsk6);
        hsk789Terms = loadHSKList(R.raw.hsk789);

    }

    private Set<String> loadHSKList(int resourceId) {

        Set<String> terms = new HashSet<>();

        try {

            InputStream is = getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;

            while ((line = reader.readLine()) != null) {

                line = line.trim();
                if (!line.isEmpty()) {
                    terms.add(line);
                }

            }

            reader.close();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return terms;

    }

    public static boolean ensureDictLoaded(Context context) {
        return ensureDictLoaded(context, null, null);
    }

    public static boolean ensureDictLoaded(Context context,
                                           @androidx.annotation.Nullable android.widget.ProgressBar progressBar,
                                           @androidx.annotation.Nullable android.widget.TextView percentView) {

        if (dictLoaded || !ccDict.isEmpty()) return true;

        boolean ok = loadCCEDICT(context.getApplicationContext(), progressBar, percentView);
        dictLoaded = ok && !ccDict.isEmpty();

        return dictLoaded;

    }

    private static boolean loadCCEDICT(Context context,
                                       @androidx.annotation.Nullable android.widget.ProgressBar progressBar,
                                       @androidx.annotation.Nullable android.widget.TextView percentView) {

        final int TOTAL_LINES = 123_596;
        final android.os.Handler main = new android.os.Handler(android.os.Looper.getMainLooper());

        try (InputStream is = context.getAssets().open("cc-cedict.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {

            ccDict.clear();
            int processed = 0;
            String line;

            if (progressBar != null) {

                main.post(() -> {

                    progressBar.setIndeterminate(false);
                    progressBar.setMax(100);
                    progressBar.setProgress(0);
                    if (percentView != null) percentView.setText("0%");

                });

            }

            while ((line = reader.readLine()) != null) {

                if (line.startsWith("#")) continue;

                String[] parts = line.split(" ", 3);
                if (parts.length < 3) continue;

                String hanzi = parts[1];
                String rest = parts[2];

                int pinyinStart = rest.indexOf('[');
                int pinyinEnd = rest.indexOf(']');
                if (pinyinStart == -1 || pinyinEnd == -1) continue;

                String pinyin = rest.substring(pinyinStart + 1, pinyinEnd);
                String definition = rest.substring(pinyinEnd + 1)
                        .replaceAll("/", "; ")
                        .trim();

                if (definition.startsWith(";")) definition = definition.substring(1).trim();
                definition = definition.replaceAll("[一-龯]+\\|[一-龯]+", "");
                definition = convertBracketedPinyin(definition);
                definition = definition.replaceAll(";\\s*$", "");

                ccDict.put(hanzi, new CCEntry(pinyin, definition));

                processed++;
                if (progressBar != null && (processed % 500 == 0)) {

                    final int pct = Math.min(100, (int) ((processed / (float) TOTAL_LINES) * 100));
                    main.post(() -> {

                        progressBar.setProgress(pct);
                        if (percentView != null) percentView.setText(pct + "%");

                    });

                }

            }

            if (progressBar != null) {

                main.post(() -> {

                    progressBar.setProgress(100);
                    if (percentView != null) percentView.setText("100%");

                });

            }

            return true;

        }

        catch (Exception e) {

            e.printStackTrace();

            return false;

        }

    }

    public static class CCEntry {

        private final String pinyin;
        private final String definition;
        private final String[] normTokens;

        public CCEntry(String pinyin, String definition) {

            this.pinyin = pinyin;
            this.definition = definition;
            this.normTokens = MyQuickHSKApp.normKeepLettersSpaces(pinyin).split("\\s+");

        }

        public String getPinyin() { return pinyin; }
        public String getDefinition() { return definition; }
        public String[] getNormTokens() { return normTokens; }
    }

    public static String convertPinyinNumbersToAccents(String pinyin) {

        String[][] toneMap = {

                {"a", "ā", "á", "ǎ", "à"},
                {"e", "ē", "é", "ě", "è"},
                {"i", "ī", "í", "ǐ", "ì"},
                {"o", "ō", "ó", "ǒ", "ò"},
                {"u", "ū", "ú", "ǔ", "ù"},
                {"ü", "ǖ", "ǘ", "ǚ", "ǜ"}

        };

        StringBuilder result = new StringBuilder();
        for (String syllable : pinyin.split(" ")) {

            if (syllable.length() == 0) continue;

            char toneChar = syllable.charAt(syllable.length() - 1);
            int tone = Character.isDigit(toneChar) ? Character.getNumericValue(toneChar) : 5;
            String base = Character.isDigit(toneChar) ? syllable.substring(0, syllable.length() - 1) : syllable;

            if (tone >= 1 && tone <= 4) {

                String targetVowel = null;
                for (String v : new String[]{"a", "e", "o", "i", "u", "ü"}) {

                    if (base.contains(v)) {

                        targetVowel = v;
                        break;

                    }

                }

                if (targetVowel != null) {

                    for (String[] map : toneMap) {

                        if (map[0].equals(targetVowel)) {

                            base = base.replaceFirst(targetVowel, map[tone]);
                            break;

                        }

                    }

                }

            }

            result.append(base).append(" ");

        }

        return result.toString().trim();

    }

    public static String convertBracketedPinyin(String text) {

        StringBuilder sb = new StringBuilder();

        int i = 0;
        while (i < text.length()) {

            int start = text.indexOf('[', i);

            if (start == -1) {

                sb.append(text.substring(i));
                break;

            }

            int end = text.indexOf(']', start);

            if (end == -1) {

                sb.append(text.substring(i));
                break;

            }

            sb.append(text, i, start + 1);
            String inside = text.substring(start + 1, end);
            String converted = convertPinyinNumbersToAccents(inside);
            sb.append(converted).append("]");
            i = end + 1;

        }

        return sb.toString();

    }

    public static String normalizePinyin(String s) {

        if (s == null) return "";

        String t = s.toLowerCase(Locale.ROOT);
        t = t.replace("u:", "ü").replace('v', 'ü');
        t = t.replaceAll("[1-5]", "");
        t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        t = t.replaceAll("[^a-z]", "");

        return t;

    }

    public static String normKeepLettersSpaces(String s) {

        if (s == null) return "";

        return s.toLowerCase()
                .replace('ü', 'v')
                .replace('Ü', 'v')
                .replaceAll("[^a-z\\s]+", " ")
                .replaceAll("\\s+", " ")
                .trim();

    }

}
