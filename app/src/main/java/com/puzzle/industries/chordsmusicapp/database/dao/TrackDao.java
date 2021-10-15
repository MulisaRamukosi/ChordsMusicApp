package com.puzzle.industries.chordsmusicapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackEntity;

import java.util.List;


@Dao
public interface TrackDao {


    @Query("Select * from Track where id = :id")
    List<TrackEntity> getTrackById(int id);

    @Query("select Track.id, Track.title, Track.location, Track.artist_id, Track.album_id, Artist.name, " +
            "Album.cover_url, Artist.picture_url from Track inner join Artist on Track.artist_id = Artist.id " +
            "inner join Album on Track.album_id = Album.id where Track.artist_id = :id order by Track.title asc")
    List<TrackArtistAlbumEntity> getArtistSongs(int id);

    @Query("select Track.id, Track.title, Track.location, Track.artist_id, Track.album_id, Artist.name, " +
            "Album.cover_url, Artist.picture_url from Track inner join Artist on Track.artist_id = Artist.id " +
            "inner join Album on Track.album_id = Album.id where Track.album_id = :id order by Track.disk_number asc")
    List<TrackArtistAlbumEntity> getAlbumSongs(int id);

    @Query("select Track.id, Track.title, Track.location, Track.artist_id, Track.album_id, Artist.name, " +
            "Album.cover_url, Artist.picture_url from Track inner join Artist on Track.artist_id = Artist.id " +
            "inner join Album on Track.album_id = Album.id order by Track.title asc")
    List<TrackArtistAlbumEntity> getAllTracks();

    @Query("delete from Track where Track.id = :id")
    void delete(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TrackEntity track);

    @Query("UPDATE Track SET location = :new_location WHERE id = :id")
    int updateSongLocation(int id, String new_location);
}
