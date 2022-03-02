package com.puzzle.industries.chordsmusicapp.base;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseResultsRVAdapter<T, F extends ViewBinding> extends RecyclerView.Adapter<BaseViewHolder<F>> {
    protected final List<T> mResults = new ArrayList<>();

    public void setData(List<T> results) {
        mResults.clear();
        mResults.addAll(results);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

}
