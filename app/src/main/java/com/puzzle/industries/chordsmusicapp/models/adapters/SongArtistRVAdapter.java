package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.activities.ArtistInfoViewActivity;
import com.puzzle.industries.chordsmusicapp.activities.ArtistViewActivity;
import com.puzzle.industries.chordsmusicapp.base.BaseViewHolder;
import com.puzzle.industries.chordsmusicapp.databinding.ItemSongArtistBinding;
import com.puzzle.industries.chordsmusicapp.helpers.ArtHelper;
import com.puzzle.industries.chordsmusicapp.helpers.ArtistInfoClickHelper;
import com.puzzle.industries.chordsmusicapp.helpers.UrlHelper;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.SongArtistModel;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.ArrayList;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SongArtistRVAdapter extends RecyclerView.Adapter<BaseViewHolder<ItemSongArtistBinding>> {

    private final ArrayList<SongArtistModel> artists;

    @NonNull
    @Override
    public BaseViewHolder<ItemSongArtistBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder<>(ItemSongArtistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemSongArtistBinding> holder, int position) {
        final SongArtistModel artist = artists.get(position);
        holder.mBinding.tvArtistName.setText(artist.getName());
        ArtHelper.displayArtistArtFromUrl(artist.getImage_url(), holder.mBinding.ivArtist);
        holder.mBinding.getRoot().setOnClickListener(
                ArtistInfoClickHelper.artistClickHandler(
                        holder.mBinding.ivArtist,
                        holder.mBinding.tvArtistName,
                        artist
                )
        );
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }
}
