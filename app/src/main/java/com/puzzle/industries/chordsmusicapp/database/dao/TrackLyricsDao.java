package com.puzzle.industries.chordsmusicapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.puzzle.industries.chordsmusicapp.database.entities.TrackInfoEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackLyricsEntity;

@Dao
public interface TrackLyricsDao {

    @Query("Select * from TrackLyrics where trackId = :id")
    TrackLyricsEntity getTrackLyricsById(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTrackLyrics(TrackLyricsEntity trackLyrics);

    @Query("delete from TrackLyrics where trackId = :id")
    void deleteTrackLyrics(int id);

}
