package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseResultsRVAdapter;
import com.puzzle.industries.chordsmusicapp.base.BaseViewHolder;
import com.puzzle.industries.chordsmusicapp.callbacks.SongAddedToDownloadQueueCallback;
import com.puzzle.industries.chordsmusicapp.databinding.ItemResultsMusicBinding;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.services.IDownloadManagerService;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.DownloadManagerService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

public class MusicResultRVAdapter extends BaseResultsRVAdapter<SongDataStruct, ItemResultsMusicBinding> {

    private final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();
    private final IDownloadManagerService DOWNLOAD_MANAGER = DownloadManagerService.getInstance();

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
        final int txtColor = ContextCompat.getColor(holder.itemView.getContext(), MUSIC_LIBRARY.getAlbumById(song.getAlbum().getId()) != null
                ? R.color.secondaryLightColor:
                R.color.primaryTextColor);

        holder.mBinding.tvName.setText(song.getSongName());
        holder.mBinding.tvDetails.setText(String.format("%s • %s", song.getArtist().getName(), song.getAlbum().getTitle()));
        holder.mBinding.tvDetails.setTextColor(txtColor);

        if (MUSIC_LIBRARY.containsSong(song.getId()) || DOWNLOAD_MANAGER.containsSong(song.getId())){
            holder.mBinding.ivDownload.setVisibility(View.GONE);
            holder.mBinding.cpi.setVisibility(View.GONE);
        }
        else{
            holder.mBinding.ivDownload.setVisibility(View.VISIBLE);

            holder.mBinding.ivDownload.setOnClickListener(v -> {
                holder.mBinding.cpi.setVisibility(View.VISIBLE);
                holder.mBinding.ivDownload.setVisibility(View.GONE);

                DownloadManagerService.getInstance().downloadSong(song, new SongAddedToDownloadQueueCallback() {
                    @Override
                    public void success() {
                        holder.mBinding.cpi.setVisibility(View.GONE);
                    }

                    @Override
                    public void failed() {
                        holder.mBinding.ivDownload.setVisibility(View.VISIBLE);
                        holder.mBinding.cpi.setVisibility(View.GONE);
                    }
                });
            });
        }
    }
}
