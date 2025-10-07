package com.example.myquickhsk;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TermMetadataDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TermMetadata metadata);

    @Update
    void update(TermMetadata metadata);

    @Query("SELECT * FROM term_metadata WHERE hanzi = :hanzi LIMIT 1")
    TermMetadata getByHanziSync(String hanzi);

    @Query("SELECT * FROM term_metadata WHERE hanzi = :hanzi LIMIT 1")
    LiveData<TermMetadata> getByHanzi(String hanzi);

    @Query("SELECT * FROM term_metadata WHERE hanzi = :hanzi AND isFavorite = 1 LIMIT 1")
    LiveData<TermMetadata> getFavoriteByHanzi(String hanzi);

    @Query("SELECT * FROM term_metadata WHERE isFavorite = 1")
    List<TermMetadata> getAllFavoritesSync();

}
