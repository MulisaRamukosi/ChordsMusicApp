package com.puzzle.industries.chordsmusicapp.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.puzzle.industries.chordsmusicapp.database.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Entity(tableName = Constants.ENTITY_TRACK)
public class TrackEntity {

    @PrimaryKey
    private final int id;
    private final String title;
    private final int track_number;
    private final String release_date;
    private final String location;
    private final int artist_id;
    private final int album_id;

}
