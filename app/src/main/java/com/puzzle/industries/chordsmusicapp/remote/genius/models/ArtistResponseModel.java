package com.puzzle.industries.chordsmusicapp.remote.genius.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;

@Getter
public class ArtistResponseModel implements Parcelable {

    private final ArtistModel artist;

    protected ArtistResponseModel(Parcel in) {
        artist = in.readParcelable(ArtistModel.class.getClassLoader());
    }

    public static final Creator<ArtistResponseModel> CREATOR = new Creator<ArtistResponseModel>() {
        @Override
        public ArtistResponseModel createFromParcel(Parcel in) {
            return new ArtistResponseModel(in);
        }

        @Override
        public ArtistResponseModel[] newArray(int size) {
            return new ArtistResponseModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(artist, i);
    }
}
