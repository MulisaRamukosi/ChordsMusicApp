package com.puzzle.industries.chordsmusicapp.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.puzzle.industries.chordsmusicapp.database.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Entity(tableName = Constants.ENTITY_TRACK_LYRICS)
public class TrackLyricsEntity {

    @PrimaryKey
    private final int trackId;
    private final String lyrics;

}
