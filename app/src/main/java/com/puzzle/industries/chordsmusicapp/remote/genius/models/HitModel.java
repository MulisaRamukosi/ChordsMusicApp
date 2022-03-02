package com.puzzle.industries.chordsmusicapp.remote.genius.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class HitModel implements Parcelable {

    public static final Creator<HitModel> CREATOR = new Creator<HitModel>() {
        @Override
        public HitModel createFromParcel(Parcel in) {
            return new HitModel(in);
        }

        @Override
        public HitModel[] newArray(int size) {
            return new HitModel[size];
        }
    };
    @SerializedName("id")
    private final String songId;
    private final String full_title;
    @SerializedName("url")
    private final String lyricsUrl;
    private final String song_art_image_url;
    private final SongArtistModel primary_artist;

    protected HitModel(Parcel in) {
        songId = in.readString();
        full_title = in.readString();
        lyricsUrl = in.readString();
        song_art_image_url = in.readString();
        primary_artist = in.readParcelable(SongArtistModel.CREATOR.getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(songId);
        parcel.writeString(full_title);
        parcel.writeString(lyricsUrl);
        parcel.writeString(song_art_image_url);
        parcel.writeParcelable(primary_artist, i);
    }
}
