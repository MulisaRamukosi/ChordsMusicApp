package com.puzzle.industries.chordsmusicapp.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public class BaseViewHolder<G extends ViewBinding> extends RecyclerView.ViewHolder {

    public final G mBinding;

    public BaseViewHolder(@NonNull G itemView) {
        super(itemView.getRoot());
        mBinding = itemView;
    }
}