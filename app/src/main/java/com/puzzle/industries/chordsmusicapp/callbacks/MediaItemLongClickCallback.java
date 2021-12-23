package com.puzzle.industries.chordsmusicapp.callbacks;

import java.util.List;

public interface MediaItemLongClickCallback<T> {

    void mediaItemLongClicked(T t, List<Integer> songIds);
}
