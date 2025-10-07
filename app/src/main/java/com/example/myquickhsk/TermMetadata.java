package com.example.myquickhsk;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "term_metadata")
public class TermMetadata {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    public String hanzi;

    public boolean isFavorite;
    public int practiceCount;
    public String notes;

    public TermMetadata() {
        hanzi = "";
    }

}
