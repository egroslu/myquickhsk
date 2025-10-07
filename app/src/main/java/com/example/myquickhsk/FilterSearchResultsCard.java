package com.example.myquickhsk;

public class FilterSearchResultsCard {
    private String script;
    private String pinyin;
    private String previewDefinition;
    private String fullDefinition;

    public FilterSearchResultsCard(String script, String pinyin, String previewDefinition, String fullDefinition) {

        this.script = script;
        this.pinyin = pinyin;
        this.previewDefinition = previewDefinition;
        this.fullDefinition = fullDefinition;

    }

    public String getScript() { return script; }
    public String getPinyin() { return pinyin; }
    public String getPreviewDefinition() { return previewDefinition; }
    public String getFullDefinition() { return fullDefinition; }

}

