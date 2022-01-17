package com.puzzle.industries.chordsmusicapp.services;

import java.util.List;

public interface IMediaOptionsService<MediaItem> {

    void showMediaOptionBottomSheet(MediaItem t, List<Integer> songIds);
}
