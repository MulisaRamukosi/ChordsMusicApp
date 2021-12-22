package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.puzzle.industries.chordsmusicapp.base.BaseResultsRVAdapter;
import com.puzzle.industries.chordsmusicapp.base.BaseViewHolder;
import com.puzzle.industries.chordsmusicapp.databinding.ItemResultsAlbumBinding;
import com.puzzle.industries.chordsmusicapp.models.dataModels.AlbumDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.remote.deezer.api.DeezerApiCall;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerAlbumDataModel;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerAlbumSongsDataModel;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerTrackDataModel;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;
import com.puzzle.industries.chordsmusicapp.remote.musicFinder.MusicFinderApi;
import com.puzzle.industries.chordsmusicapp.services.IDownloadManagerService;
import com.puzzle.industries.chordsmusicapp.services.impl.DownloadManagerService;

import java.util.ArrayList;
import java.util.List;

public class AlbumResultRVAdapter extends BaseResultsRVAdapter<AlbumDataStruct, ItemResultsAlbumBinding> {

    @NonNull
    @Override
    public BaseViewHolder<ItemResultsAlbumBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemResultsAlbumBinding binding = ItemResultsAlbumBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new BaseViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemResultsAlbumBinding> holder, int position) {
        final Context ctx = holder.itemView.getContext();
        final AlbumDataStruct album = mResults.get(position);
        Glide.with(holder.itemView.getContext()).load(album.getCover_big()).into(holder.mBinding.ivAlbumPic);
        holder.mBinding.tvName.setText(album.getTitle());
        holder.mBinding.tvDetails.setText(String.format("%s", album.getArtist().getName()));

        holder.mBinding.ivDownload.setOnClickListener(v -> {
            setAsLoading(true, holder);

            DeezerApiCall.getInstance().getAlbumSongsById(album.getId(), new ApiCallBack<DeezerAlbumSongsDataModel>() {
                @Override
                public void onSuccess(DeezerAlbumSongsDataModel deezerAlbumSongsDataModel) {
                    setAsLoading(false, holder);
                    final DeezerTrackDataModel songs = deezerAlbumSongsDataModel.getTracks();
                    final IDownloadManagerService downloadManagerService = DownloadManagerService.getInstance();

                    for (SongDataStruct song : songs.getData()){
                        if (song.getAlbum() == null) song.setAlbum(album);
                        if (song.getArtist() == null || song.getArtist().getPicture_big() == null) song.setArtist(album.getArtist());
                        downloadManagerService.downloadSong(song);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    setAsLoading(false, holder);
                    Toast.makeText(ctx, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        });
    }

    private void setAsLoading(boolean isLoading, BaseViewHolder<ItemResultsAlbumBinding> viewHolder) {
        viewHolder.mBinding.ivDownload.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        viewHolder.mBinding.cpi.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }

}
