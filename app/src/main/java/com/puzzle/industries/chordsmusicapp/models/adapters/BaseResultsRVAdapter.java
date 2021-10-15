package com.puzzle.industries.chordsmusicapp.models.adapters;

import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseResultsRVAdapter<T, F extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<F> {
    public abstract void setData(T t);
    public abstract void setAsLoading(boolean isLoading, F f);
}
