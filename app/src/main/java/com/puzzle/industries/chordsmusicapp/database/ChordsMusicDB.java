package com.puzzle.industries.chordsmusicapp.database;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.puzzle.industries.chordsmusicapp.database.dao.AlbumDao;
import com.puzzle.industries.chordsmusicapp.database.dao.ArtistDao;
import com.puzzle.industries.chordsmusicapp.database.dao.PlaylistDao;
import com.puzzle.industries.chordsmusicapp.database.dao.PlaylistTrackDao;
import com.puzzle.industries.chordsmusicapp.database.dao.TrackDao;
import com.puzzle.industries.chordsmusicapp.database.dao.TrackInfoDao;
import com.puzzle.industries.chordsmusicapp.database.dao.TrackLyricsDao;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackInfoEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackLyricsEntity;

@Database(
        version = 4,
        entities = {
                ArtistEntity.class,
                TrackEntity.class,
                AlbumEntity.class,
                PlaylistEntity.class,
                PlaylistTrackEntity.class,
                TrackInfoEntity.class,
                TrackLyricsEntity.class
        }/*,
        autoMigrations = {
                @AutoMigration(
                        from = 3,
                        to = 4,
                        spec = ChordsMusicDB.AutoMig.class
                )
        }*/
)
public abstract class ChordsMusicDB extends RoomDatabase {

    public abstract ArtistDao artistDao();

    public abstract TrackDao trackDao();

    public abstract AlbumDao albumDao();

    public abstract PlaylistDao playlistDao();

    public abstract PlaylistTrackDao playlistTrackDao();

    public abstract TrackInfoDao trackInfoDao();

    public abstract TrackLyricsDao trackLyricsDao();

    /*static class AutoMig implements AutoMigrationSpec{
    }

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //database.execSQL("create table TrackInfo");
            database.execSQL("CREATE TABLE IF NOT EXISTS `TrackInfo` (`trackId` INTEGER NOT NULL, `info` TEXT, PRIMARY KEY(`trackId`))");
            database.execSQL("CREATE TABLE IF NOT EXISTS `TrackLyrics` (`trackId` INTEGER NOT NULL, `lyrics` TEXT, PRIMARY KEY(`trackId`))");
        }
    };*/

}

