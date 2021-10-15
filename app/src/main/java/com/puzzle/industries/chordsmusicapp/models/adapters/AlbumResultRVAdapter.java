package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.puzzle.industries.chordsmusicapp.databinding.ItemResultsAlbumBinding;
import com.puzzle.industries.chordsmusicapp.databinding.ItemResultsMusicBinding;
import com.puzzle.industries.chordsmusicapp.models.dataModels.AlbumDataStruct;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.remote.deezer.api.DeezerApiCall;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerAlbumDataModel;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerAlbumSongsDataModel;
import com.puzzle.industries.chordsmusicapp.remote.deezer.models.DeezerTrackDataModel;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;
import com.puzzle.industries.chordsmusicapp.remote.musicFinder.MusicFinderApi;
import com.puzzle.industries.chordsmusicapp.services.impl.DownloadManagerService;

import java.util.ArrayList;
import java.util.List;

public class AlbumResultRVAdapter extends BaseResultsRVAdapter<DeezerAlbumDataModel, AlbumResultRVAdapter.ViewHolder> {

    private List<AlbumDataStruct> albums = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemResultsAlbumBinding binding = ItemResultsAlbumBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Context ctx = holder.itemView.getContext();
        final AlbumDataStruct album = albums.get(position);
        Glide.with(holder.itemView.getContext()).load(album.getCover_big()).into(holder.mBinding.ivAlbumPic);
        holder.mBinding.tvName.setText(album.getTitle());
        holder.mBinding.tvDetails.setText(String.format("%s", album.getArtist().getName()));

        holder.mBinding.ivDownload.setOnClickListener(v -> {
            setAsLoading(true, holder);

            DeezerApiCall.getInstance().getAlbumSongsById(album.getId(), new ApiCallBack<DeezerAlbumSongsDataModel>() {
                @Override
                public void onSuccess(DeezerAlbumSongsDataModel deezerAlbumSongsDataModel) {
                    final DeezerTrackDataModel songs = deezerAlbumSongsDataModel.getTracks();

                    for (SongDataStruct song : songs.getData()){
                        new MusicFinderApi(song, new ApiCallBack<String>() {
                            @Override
                            public void onSuccess(String songUrl) {
                                DownloadManagerService.getInstance().downloadSong(song, songUrl);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                setAsLoading(false, holder);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(ctx, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    @Override
    public void setData(DeezerAlbumDataModel deezerAlbumDataModel) {
        albums = deezerAlbumDataModel.getData();
        notifyDataSetChanged();
    }

    @Override
    public void setAsLoading(boolean isLoading, ViewHolder viewHolder) {
        viewHolder.mBinding.ivDownload.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        viewHolder.mBinding.cpi.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ItemResultsAlbumBinding mBinding;

        public ViewHolder(@NonNull ItemResultsAlbumBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }
    }
}
