package com.puzzle.industries.chordsmusicapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;

import java.util.List;


@Dao
public interface ArtistDao {

    @Query("Select * from Artist order by Artist.name asc")
    List<ArtistEntity> getAllArtists();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ArtistEntity artist);

    @Query("Delete from Artist where Artist.id = :id")
    void delete(int id);


}
