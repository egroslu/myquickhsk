package com.example.myquickhsk;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TermMetadata.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TermMetadataDAO termMetadataDAO();

}
