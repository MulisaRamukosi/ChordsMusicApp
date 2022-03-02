package com.puzzle.industries.chordsmusicapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.gson.Gson;
import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseFragment;
import com.puzzle.industries.chordsmusicapp.bottom_sheets.NotCorrectSongInfoBottomSheet;
import com.puzzle.industries.chordsmusicapp.callbacks.SongInfoCallback;
import com.puzzle.industries.chordsmusicapp.database.ChordsMusicDB;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackInfoEntity;
import com.puzzle.industries.chordsmusicapp.databinding.FragmentSongInfoBinding;
import com.puzzle.industries.chordsmusicapp.helpers.ArtHelper;
import com.puzzle.industries.chordsmusicapp.helpers.ArtistInfoClickHelper;
import com.puzzle.industries.chordsmusicapp.helpers.UrlHelper;
import com.puzzle.industries.chordsmusicapp.models.adapters.SongArtistRVAdapter;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongInfoStruct;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.HitModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.SongArtistModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.SongModel;
import com.puzzle.industries.chordsmusicapp.services.impl.ExecutorServiceManager;
import com.puzzle.industries.chordsmusicapp.services.impl.MediaBroadCastService;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SongInfoFragment extends BaseFragment implements SongInfoCallback {

    private FragmentSongInfoBinding mBinding;
    private SongInfoStruct mSongInfo;
    private TrackArtistAlbumEntity mSong;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSongInfoBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private boolean initExtras(){
        final Bundle args = getArguments();
        if (args != null) {
            mSongInfo = getArguments().getParcelable(Constants.KEY_SONG_INFO);
            mSong = getArguments().getParcelable(Constants.KEY_SONG);
        }

        return mSongInfo != null && mSong != null;
    }

    private void init(){
        if (initExtras()){
            initSongInfo();
            mBinding.ibNotCorrectInfo.setOnClickListener(v -> displayNotCorrectSongBottomSheet());
        }
    }

    private void displayNotCorrectSongBottomSheet() {
        final NotCorrectSongInfoBottomSheet bottomSheet = new NotCorrectSongInfoBottomSheet(mSongInfo, this);
        bottomSheet.show(getChildFragmentManager(), "");
    }

    private void initSongInfo(){
        final SongModel song = mSongInfo.getSongResult().getResponse().getSong();
        final HitModel hitModel = mSongInfo.getSearchResult().getResponse().getHits().get(0).getResult();
        final SongArtistModel artist = hitModel.getPrimary_artist();
        final String description = song.getDescription().getPlain();

        mBinding.tvSongTitle.setText(song.getFull_title());
        mBinding.tvReleaseDate.setText(song.getRelease_date());

        mBinding.lArtist.tvArtistName.setText(artist.getName());
        mBinding.lArtist.getRoot().setOnClickListener(ArtistInfoClickHelper.artistClickHandler(
                mBinding.lArtist.ivArtist,
                mBinding.lArtist.tvArtistName,
                artist
        ));

        ArtHelper.displayArtistArtFromUrl(artist.getImage_url(), mBinding.lArtist.ivArtist);

        if (description.equals("?")) mBinding.tvDesc.setVisibility(View.GONE);
        else mBinding.tvDesc.setText(song.getDescription().getPlain());

        initArtistAdapter(mBinding.tvFeaturedArtists, song.getFeatured_artists(), mBinding.rvFeaturedArtists);
        initArtistAdapter(mBinding.tvProducers, song.getProducer_artists(), mBinding.rvProducers);
    }

    private void initArtistAdapter(TextView title, ArrayList<SongArtistModel> artists, RecyclerView rv){
        if (artists.isEmpty()) {
            rv.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
        }
        else{
            Collections.sort(artists, Comparator.comparing(SongArtistModel::getName));
            final SongArtistRVAdapter featuredArtistsAdapter = new SongArtistRVAdapter(artists);
            rv.setAdapter(featuredArtistsAdapter);
        }
    }

    @Override
    public void songInfoChanged(SongInfoStruct mSongInfo) {
        ExecutorServiceManager.getInstance().executeRunnableOnSingeThread(() -> {
            Chords.getDatabase().trackInfoDao().deleteTrackInfo(mSong.getId());
            Chords.getDatabase().trackLyricsDao().deleteTrackLyrics(mSong.getId());
            Chords.getDatabase().trackInfoDao().insertTrackInfo(new TrackInfoEntity(mSong.getId(), new Gson().toJson(mSongInfo)));
            final Bundle bundle = getArguments();
            if (bundle != null){
                bundle.putParcelable(Constants.KEY_SONG_INFO, mSongInfo);
            }
            this.mSongInfo = mSongInfo;
            requireActivity().runOnUiThread(this::initSongInfo);
            MediaBroadCastService.getInstance().songInfoStructChanged(mSongInfo);
        });
    }

    @Override
    public void songInfoChangeFailed() {
        showAlert("Failed to show update song info", true, getString(R.string.try_again), v -> displayNotCorrectSongBottomSheet());
    }
}
