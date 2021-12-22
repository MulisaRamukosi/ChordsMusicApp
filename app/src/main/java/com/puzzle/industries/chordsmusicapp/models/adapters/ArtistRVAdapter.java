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
import com.google.android.flexbox.FlexboxLayoutManager;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.activities.ArtistViewActivity;
import com.puzzle.industries.chordsmusicapp.base.BaseMediaRVAdapter;
import com.puzzle.industries.chordsmusicapp.base.BaseViewHolder;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ItemArtistBinding;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ArtistRVAdapter extends BaseMediaRVAdapter<ItemArtistBinding, ArtistEntity> {


    public ArtistRVAdapter(List<ArtistEntity> mediaList) {
        this.mediaList = mediaList;
    }

    @NonNull
    @Override
    public BaseViewHolder<ItemArtistBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemArtistBinding binding = ItemArtistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BaseViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemArtistBinding> holder, int position) {
        final Context ctx = holder.itemView.getContext();
        final ViewGroup.LayoutParams lp = holder.mBinding.getRoot().getLayoutParams();
        final ArtistEntity artist = mediaList.get(position);
        final boolean isCurrentlyPlaying = currentMediaItem != null && currentMediaItem.getId() == artist.getId();
        final int txtColor = ContextCompat.getColor(ctx, isCurrentlyPlaying
                ? R.color.secondaryLightColor:
                R.color.primaryTextColor);

        if (lp instanceof FlexboxLayoutManager.LayoutParams) {
            FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) lp;
            flexboxLp.setFlexGrow(1.0f);
        }

        if (artist.getPicture_url() != null){
            Glide.with(ctx).load(artist.getPicture_url()).into(holder.mBinding.ivArtistArt);
        }
        else{
            //TODO: attempt to get artistImage from deezer
            Glide.with(ctx).load(ContextCompat.getDrawable(ctx, R.drawable.bg_artist)).into(holder.mBinding.ivArtistArt);
        }

        holder.mBinding.tvArtist.setText(artist.getName());
        holder.mBinding.tvArtist.setTextColor(txtColor);

        holder.mBinding.getRoot().setOnLongClickListener(view -> {
            final List<Integer> artistSongs = MUSIC_LIBRARY.getArtistSongs(artist.getId()).stream().mapToInt(TrackArtistAlbumEntity::getId).boxed().collect(Collectors.toList());
            callback.mediaItemLongClicked(artist, artistSongs);
            return true;
        });

        holder.mBinding.getRoot().setOnClickListener(v -> {
            final Resources res = v.getResources();
            final Intent i = new Intent(v.getContext(), ArtistViewActivity.class);
            final Pair<View, String> albumPic = Pair.create(holder.mBinding.ivArtistArt, res.getString(R.string.trans_artist_pic));
            final Pair<View, String> albumName = Pair.create(holder.mBinding.tvArtist, res.getString(R.string.trans_artist_name));
            final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) v.getContext(), albumPic, albumName
            );

            i.putExtra(Constants.KEY_ARTIST, artist);
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
        mediaList.addAll(MUSIC_LIBRARY.getArtists().stream().filter(artistEntity -> meetsFilterRequirements(artistEntity, word)).collect(Collectors.toList()));
    }

    @Override
    protected boolean meetsFilterRequirements(ArtistEntity artistEntity, String word) {
        return artistEntity.getName().toLowerCase().contains(word.toLowerCase());
    }
}
