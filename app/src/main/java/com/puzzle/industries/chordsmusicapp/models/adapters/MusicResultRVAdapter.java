package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.puzzle.industries.chordsmusicapp.databinding.ItemResultsMusicBinding;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerTrackDataModel;
import com.puzzle.industries.chordsmusicapp.services.impl.DownloadManagerService;
import com.puzzle.industries.chordsmusicapp.services.impl.DownloadService;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;
import com.puzzle.industries.chordsmusicapp.remote.musicFinder.MusicFinderApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicResultRVAdapter extends BaseResultsRVAdapter<DeezerTrackDataModel, MusicResultRVAdapter.ViewHolder> {

    private List<SongDataStruct> mSongs = new ArrayList<>();


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemResultsMusicBinding binding = ItemResultsMusicBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Context ctx = holder.itemView.getContext();
        final SongDataStruct song = mSongs.get(position);
        final boolean isDownloading = DownloadManagerService.getInstance().isDownloading(song);

        setAsLoading(isDownloading, holder);
        holder.mBinding.tvName.setText(song.getSongName());
        holder.mBinding.tvDetails.setText(String.format("%s • %s", song.getArtist().getName(), song.getAlbum().getTitle()));

        holder.mBinding.ivDownload.setOnClickListener(v -> {
            setAsLoading(true, holder);

            new MusicFinderApi(song, new ApiCallBack<String>() {
                @Override
                public void onSuccess(String songUrl) {
                    setAsLoading(false, holder);
                    DownloadManagerService.getInstance().downloadSong(song, songUrl);
                }

                @Override
                public void onFailure(Throwable t) {
                    setAsLoading(false, holder);
                    Toast.makeText(ctx, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    @Override
    public void setData(DeezerTrackDataModel deezerTrackDataModel) {
        mSongs = deezerTrackDataModel.getData();
        notifyDataSetChanged();
    }

    @Override
    public void setAsLoading(boolean isLoading, ViewHolder viewHolder) {
        viewHolder.mBinding.ivDownload.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        viewHolder.mBinding.cpi.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ItemResultsMusicBinding mBinding;

        public ViewHolder(@NonNull ItemResultsMusicBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }
    }
}
