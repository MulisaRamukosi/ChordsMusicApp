package com.puzzle.industries.chordsmusicapp.callbacks;

public interface RVAdapterItemClickCallback<T> {

    void itemClicked(T t);
    void itemLongClicked(T t);
    void neutralItemClick();
}
