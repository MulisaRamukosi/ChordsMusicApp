package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.activities.AlbumViewActivity;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaRVAdapter;
import com.puzzle.industries.chordsmusicapp.base.BaseViewHolder;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ItemAlbumBinding;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AlbumRVAdapter extends BaseMediaRVAdapter<ItemAlbumBinding, AlbumArtistEntity> {

    public AlbumRVAdapter(List<AlbumArtistEntity> mediaList) {
        this.mediaList = mediaList;
    }

    @NonNull
    @Override
    public BaseViewHolder<ItemAlbumBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemAlbumBinding binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BaseViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemAlbumBinding> holder, int position) {

        final AlbumArtistEntity album = mediaList.get(position);
        final Context ctx = holder.itemView.getContext();
        final boolean isCurrentlyPlaying = currentMediaItem != null && currentMediaItem.getId() == album.getId();
        final int txtColor = ContextCompat.getColor(ctx, isCurrentlyPlaying
                ? R.color.secondaryLightColor:
                R.color.primaryTextColor);

        Glide.with(ctx).load(album.getCover_url()).fallback(R.drawable.bg_album).into(holder.mBinding.ivAlbumPic);
        holder.mBinding.tvAlbumName.setText(album.getTitle());
        holder.mBinding.tvArtistName.setText(album.getName());
        holder.mBinding.tvAlbumName.setTextColor(txtColor);
        holder.mBinding.tvArtistName.setTextColor(txtColor);

        holder.mBinding.getRoot().setOnLongClickListener(view -> {
            final List<Integer> albumSongs = MUSIC_LIBRARY.getAlbumSongs(album.getId()).stream().mapToInt(TrackArtistAlbumEntity::getId).boxed().collect(Collectors.toList());
            callback.mediaItemLongClicked(album, albumSongs);
            return true;
        });

        holder.mBinding.getRoot().setOnClickListener(v -> {
            final Resources res = v.getResources();
            final Intent i = new Intent(v.getContext(), AlbumViewActivity.class);
            i.putExtra(Constants.KEY_ALBUM, album);

            final Pair<View, String> albumPic = Pair.create(holder.mBinding.ivAlbumPic, res.getString(R.string.trans_album_pic));
            final Pair<View, String> albumName = Pair.create(holder.mBinding.tvAlbumName, res.getString(R.string.trans_album_name));
            final Pair<View, String> artistName = Pair.create(holder.mBinding.tvArtistName, res.getString(R.string.trans_artist_name));

            final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) v.getContext(), albumPic, artistName, albumName
            );

            v.getContext().startActivity(i, optionsCompat.toBundle());
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void showSearchResults(String word) {
        if (word.isEmpty()) {
            mediaList.clear();
            notifyDataSetChanged();
            return;
        }

        mediaList.clear();
        mediaList.addAll(MUSIC_LIBRARY.getAlbums().stream().filter(albumArtistEntity -> meetsFilterRequirements(albumArtistEntity, word)).collect(Collectors.toList()));
        notifyDataSetChanged();
    }

    @Override
    protected boolean meetsFilterRequirements(AlbumArtistEntity albumArtistEntity, String word) {
        return albumArtistEntity.getTitle().toLowerCase().contains(word.toLowerCase()) || albumArtistEntity.getName().toLowerCase().contains(word.toLowerCase());
    }
}
