package com.puzzle.industries.chordsmusicapp.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumEntity;

import java.util.List;

@Dao
public interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(AlbumEntity album);

    @Query("delete from Album where Album.id = :id")
    int delete(int id);

    @Query("Select Album.id, Album.title, Album.cover_url, Album.artist_id, Artist.name from Album inner join Artist " +
            "on Album.artist_id = Artist.id order by Album.title asc")
    List<AlbumArtistEntity> getAllAlbums();


}
