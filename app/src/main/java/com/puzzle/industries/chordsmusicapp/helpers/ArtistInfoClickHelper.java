package com.puzzle.industries.chordsmusicapp.helpers;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.google.android.material.imageview.ShapeableImageView;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.activities.ArtistInfoViewActivity;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.SongArtistModel;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

public class ArtistInfoClickHelper {

    public static View.OnClickListener artistClickHandler(ShapeableImageView iv, TextView tv, SongArtistModel artist){
        return v -> {
            final Resources res = v.getResources();
            final Intent i = new Intent(v.getContext(), ArtistInfoViewActivity.class);
            final Pair<View, String> albumPic = Pair.create(iv, res.getString(R.string.trans_artist_pic));
            final Pair<View, String> albumName = Pair.create(tv, res.getString(R.string.trans_artist_name));
            final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) v.getContext(), albumPic, albumName
            );

            i.putExtra(Constants.KEY_ARTIST, artist);
            v.getContext().startActivity(i, optionsCompat.toBundle());
        };
    }

}
