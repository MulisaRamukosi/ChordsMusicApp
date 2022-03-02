package com.puzzle.industries.chordsmusicapp.database.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.puzzle.industries.chordsmusicapp.database.entities.TrackInfoEntity;

@Dao
public interface TrackInfoDao {

    @Query("Select * from TrackInfo where trackId = :id")
    TrackInfoEntity getTrackInfoById(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTrackInfo(TrackInfoEntity trackInfo);

    @Query("delete from TrackInfo where trackId = :id")
    void deleteTrackInfo(int id);

}
