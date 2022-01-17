package com.puzzle.industries.chordsmusicapp.database;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RenameColumn;
import androidx.room.RoomDatabase;
import androidx.room.migration.AutoMigrationSpec;

import com.puzzle.industries.chordsmusicapp.database.dao.AlbumDao;
import com.puzzle.industries.chordsmusicapp.database.dao.ArtistDao;
import com.puzzle.industries.chordsmusicapp.database.dao.PlaylistDao;
import com.puzzle.industries.chordsmusicapp.database.dao.PlaylistTrackDao;
import com.puzzle.industries.chordsmusicapp.database.dao.TrackDao;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackEntity;

@Database(
        version = 3,
        entities = {
                ArtistEntity.class,
                TrackEntity.class,
                AlbumEntity.class,
                PlaylistEntity.class,
                PlaylistTrackEntity.class
        },
        autoMigrations = {
                @AutoMigration(
                        from = 2,
                        to = 3
                )
        }
)
public abstract class ChordsMusicDB extends RoomDatabase {

    public abstract ArtistDao artistDao();
    public abstract TrackDao trackDao();
    public abstract AlbumDao albumDao();
    public abstract PlaylistDao playlistDao();
    public abstract PlaylistTrackDao playlistTrackDao();

}
