package com.puzzle.industries.chordsmusicapp.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.base.BaseActivity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivityArtistInfoBinding;
import com.puzzle.industries.chordsmusicapp.helpers.ArtHelper;
import com.puzzle.industries.chordsmusicapp.remote.genius.api.GeniusApiCall;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.ArtistResultModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.SongArtistModel;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.List;

public class ArtistInfoViewActivity extends BaseActivity {

    private ActivityArtistInfoBinding mBinding;
    private SongArtistModel mArtist;
    private boolean artistInfoCallGaveResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityArtistInfoBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        init();
    }

    private void init(){
        if (artistInfoAvailable()){
            initArtistInfo();
        }
    }

    private void initArtistInfo() {
        postponeEnterTransition();
        Glide.with(ArtistInfoViewActivity.this)
                .load(mArtist.getHeader_image_url())
                .fallback(R.drawable.bg_artist)
                .error(R.drawable.bg_artist)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        initArtistPic();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        initArtistPic();
                        return false;
                    }
                })
                .into(mBinding.ivHeader);


        mBinding.tvArtistName.setText(mArtist.getName());
        initExtraArtistInfo();
    }

    private void initArtistPic(){
        ArtHelper.displayArtistArtFromUrl(mArtist.getImage_url(), mBinding.ivArtist);
        scheduleStartPostponedTransition(mBinding.ivArtist);
    }

    private void initExtraArtistInfo(){
        mBinding.lpi.setVisibility(View.VISIBLE);
        artistInfoCallGaveResponse = false;

        GeniusApiCall.getInstance().getArtistInfo(mArtist.getId(), new ApiCallBack<ArtistResultModel>() {
            @Override
            public void onSuccess(ArtistResultModel artistResultModel) {
                artistInfoCallGaveResponse = true;
                mBinding.lpi.setVisibility(View.GONE);
                final String artistDescription = artistResultModel.getResponse().getArtist().getDescription().getPlain();
                final List<String> alternativeNames = artistResultModel.getResponse().getArtist().getAlternate_names();

                mBinding.tvDesc.setText(artistDescription.trim().equals("?") ? "description unknown" : artistDescription);
                mBinding.tvAlternateNames.setText(alternativeNames.isEmpty() ? "No alternate name(s)"
                        : alternativeNames.toString().replace("[", "").replace("]", ""));


            }

            @Override
            public void onFailure(Throwable t) {
                artistInfoCallGaveResponse = true;
                mBinding.lpi.setVisibility(View.GONE);
                showAlert("Failed to get artist info", getString(R.string.try_again), v -> initExtraArtistInfo());
            }
        });
    }

    private boolean artistInfoAvailable(){
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mArtist = bundle.getParcelable(Constants.KEY_ARTIST);
            return mArtist != null;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (artistInfoCallGaveResponse) mBinding.lpi.setVisibility(View.GONE);
    }
}
