package com.puzzle.industries.chordsmusicapp.models.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.puzzle.industries.chordsmusicapp.base.BaseViewHolder;
import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.PlaylistTrackEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.databinding.ItemMusicBinding;
import com.puzzle.industries.chordsmusicapp.services.IMusicLibraryService;
import com.puzzle.industries.chordsmusicapp.services.impl.MusicLibraryService;

import java.util.List;
import java.util.Set;

import lombok.Getter;

@SuppressLint("NotifyDataSetChanged")
public class PlaylistTracksRVAdapter extends RecyclerView.Adapter<BaseViewHolder<ItemMusicBinding>> {

    @Getter private final List<PlaylistTrackEntity> playlistTracks;
    private final IMusicLibraryService MUSIC_LIBRARY = MusicLibraryService.getInstance();

    public PlaylistTracksRVAdapter(List<PlaylistTrackEntity> playlistTracks){
        this.playlistTracks = playlistTracks;
    }

    @NonNull
    @Override
    public BaseViewHolder<ItemMusicBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemMusicBinding musicBinding = ItemMusicBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BaseViewHolder<>(musicBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemMusicBinding> holder, int position) {
        final PlaylistTrackEntity playlistTrack = playlistTracks.get(position);
        final TrackArtistAlbumEntity track = MUSIC_LIBRARY.getSongById(playlistTrack.getTrackId());

        if (track != null){
            final ArtistEntity artist = MUSIC_LIBRARY.getArtistById(track.getArtist_id());
            final AlbumArtistEntity album = MUSIC_LIBRARY.getAlbumById(track.getAlbum_id());

            holder.mBinding.tvName.setText(track.getTitle());
            holder.mBinding.tvDetails.setText(String.format("%s • %s", artist.getName(), album.getTitle()));
        }
    }

    public void addNewSongs(List<Integer> songIds){
        if (songIds.isEmpty()) return;
        boolean changesOccurred = false;
        for (final PlaylistTrackEntity playlistTrack : playlistTracks){
            if (songIds.contains(playlistTrack.getTrackId())){
                songIds.remove(Integer.valueOf(playlistTrack.getTrackId()));
            }
            else{
                changesOccurred = true;
                playlistTracks.remove(playlistTrack);
            }
        }

        if (changesOccurred){
            notifyDataSetChanged();
        }

        final int lastKnownSize = playlistTracks.size();
        for (int songId : songIds){
            playlistTracks.add(new PlaylistTrackEntity(0, 0, songId));
        }

        if (lastKnownSize != playlistTracks.size()){
            notifyItemRangeChanged(lastKnownSize, getItemCount());
        }
    }

    @Override
    public int getItemCount() {
        return playlistTracks.size();
    }

    public void removeSongs(Set<PlaylistTrackEntity> mRemovedSongs) {
        if(this.playlistTracks.removeAll(mRemovedSongs)){
            notifyDataSetChanged();
        }
    }
}
