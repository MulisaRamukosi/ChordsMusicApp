package com.puzzle.industries.chordsmusicapp.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.puzzle.industries.chordsmusicapp.database.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Entity(tableName = Constants.ENTITY_ALBUM)

public class AlbumEntity {

    @PrimaryKey
    private final int id;
    private final String title;
    private final String cover_url;
    private final String release_date;
    private final int artist_id;

}
