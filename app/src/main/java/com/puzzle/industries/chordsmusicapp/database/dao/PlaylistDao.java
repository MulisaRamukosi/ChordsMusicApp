package com.puzzle.industries.chordsmusicapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;

import java.util.List;

@Dao
public interface PlaylistDao {

    @Insert
    long insert(PlaylistEntity playlist);

    @Query("select * from Playlist")
    List<PlaylistEntity> getAll();

    @Query("update Playlist set name = :name where id = :id")
    void updateName(int id, String name);

    @Delete
    int delete(PlaylistEntity playlist);

}
