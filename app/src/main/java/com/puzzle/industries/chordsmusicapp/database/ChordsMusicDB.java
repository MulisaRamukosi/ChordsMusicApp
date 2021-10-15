package com.puzzle.industries.chordsmusicapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.puzzle.industries.chordsmusicapp.database.dao.AlbumDao;
import com.puzzle.industries.chordsmusicapp.database.dao.ArtistDao;
import com.puzzle.industries.chordsmusicapp.database.dao.TrackDao;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackEntity;

@Database(entities = {
        ArtistEntity.class,
        TrackEntity.class,
        AlbumEntity.class
},
version = 1)
public abstract class ChordsMusicDB extends RoomDatabase {

    public abstract ArtistDao artistDao();
    public abstract TrackDao trackDao();
    public abstract AlbumDao albumDao();

}
