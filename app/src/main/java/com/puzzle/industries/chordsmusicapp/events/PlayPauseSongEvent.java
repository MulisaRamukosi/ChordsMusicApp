package com.puzzle.industries.chordsmusicapp.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayPauseSongEvent {
    private final int id;
}
