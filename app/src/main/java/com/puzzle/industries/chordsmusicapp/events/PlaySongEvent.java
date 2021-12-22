package com.puzzle.industries.chordsmusicapp.events;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlaySongEvent {
    private final int id;
    private final List<Integer> playList;
}
