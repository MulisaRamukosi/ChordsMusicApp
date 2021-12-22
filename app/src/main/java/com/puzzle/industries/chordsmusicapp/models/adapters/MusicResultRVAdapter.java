package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.puzzle.industries.chordsmusicapp.base.BaseResultsRVAdapter;
import com.puzzle.industries.chordsmusicapp.base.BaseViewHolder;
import com.puzzle.industries.chordsmusicapp.databinding.ItemResultsMusicBinding;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerTrackDataModel;
import com.puzzle.industries.chordsmusicapp.services.impl.DownloadManagerService;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;
import com.puzzle.industries.chordsmusicapp.remote.musicFinder.MusicFinderApi;

import java.util.ArrayList;
import java.util.List;

public class MusicResultRVAdapter extends BaseResultsRVAdapter<SongDataStruct, ItemResultsMusicBinding> {

    @NonNull
    @Override
    public BaseViewHolder<ItemResultsMusicBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemResultsMusicBinding binding = ItemResultsMusicBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new BaseViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemResultsMusicBinding> holder, int position) {
        final SongDataStruct song = mResults.get(position);
        holder.mBinding.tvName.setText(song.getSongName());
        holder.mBinding.tvDetails.setText(String.format("%s • %s", song.getArtist().getName(), song.getAlbum().getTitle()));

        holder.mBinding.ivDownload.setOnClickListener(v -> DownloadManagerService.getInstance().downloadSong(song));

    }
}
