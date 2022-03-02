package com.puzzle.industries.chordsmusicapp.remote.genius.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import lombok.Getter;

@Getter
public class SongModel implements Parcelable {

    public static final Creator<SongModel> CREATOR = new Creator<SongModel>() {
        @Override
        public SongModel createFromParcel(Parcel in) {
            return new SongModel(in);
        }

        @Override
        public SongModel[] newArray(int size) {
            return new SongModel[size];
        }
    };

    private final String full_title;
    private final PlainDescriptionModel description;
    private final String release_date;
    private final ArrayList<SongArtistModel> featured_artists;
    private final ArrayList<SongArtistModel> producer_artists;

    protected SongModel(Parcel in) {
        full_title = in.readString();
        description = in.readParcelable(PlainDescriptionModel.class.getClassLoader());
        release_date = in.readString();
        featured_artists = in.createTypedArrayList(SongArtistModel.CREATOR);
        producer_artists = in.createTypedArrayList(SongArtistModel.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(full_title);
        parcel.writeParcelable(description, i);
        parcel.writeString(release_date);
        parcel.writeTypedList(featured_artists);
        parcel.writeTypedList(producer_artists);
    }
}
