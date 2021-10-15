package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ItemAlbumBinding;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AlbumRVAdapter extends RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>{

    private final List<AlbumArtistEntity> mAlbums;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemAlbumBinding binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AlbumArtistEntity album = mAlbums.get(position);
        Glide.with(holder.itemView.getContext()).load(album.getCover_url()).into(holder.mBinding.ivAlbumPic);
        holder.mBinding.tvAlbumName.setText(album.getTitle());
        holder.mBinding.tvArtistName.setText(album.getName());

        //TODO: set click listener to album
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ItemAlbumBinding mBinding;

        public ViewHolder(@NonNull ItemAlbumBinding itemView) {
            super(itemView.getRoot());

            mBinding = itemView;
        }
    }
}
