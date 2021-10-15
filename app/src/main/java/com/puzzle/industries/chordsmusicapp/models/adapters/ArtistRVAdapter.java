package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ItemArtistBinding;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ArtistRVAdapter extends RecyclerView.Adapter<ArtistRVAdapter.ViewHolder> {

    private final List<ArtistEntity> mArtists;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemArtistBinding binding = ItemArtistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewGroup.LayoutParams lp = holder.mBinding.getRoot().getLayoutParams();
        if (lp instanceof FlexboxLayoutManager.LayoutParams) {
            FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) lp;
            flexboxLp.setFlexGrow(1.0f);
        }

        final ArtistEntity artist = mArtists.get(position);
        Glide.with(holder.itemView.getContext()).load(artist.getPicture_url()).into(holder.mBinding.ivArtistArt);
        holder.mBinding.tvArtist.setText(artist.getName());

        //TODO:set click listener for artist
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ItemArtistBinding mBinding;

        public ViewHolder(@NonNull ItemArtistBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }
    }
}
