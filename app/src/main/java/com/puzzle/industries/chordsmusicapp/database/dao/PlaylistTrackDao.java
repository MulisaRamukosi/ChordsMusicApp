package com.puzzle.industries.chordsmusicapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;

import java.util.List;

@Dao
public interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PlaylistTrackEntity playlist);

    @Query("select * from PlaylistTrack")
    List<PlaylistTrackEntity> getAll();

    @Delete
    int delete(PlaylistTrackEntity playlistTrack);

    @Delete
    int deleteMany(List<PlaylistTrackEntity> playlistTrackEntities);
}
