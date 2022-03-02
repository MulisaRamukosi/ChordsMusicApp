package com.puzzle.industries.chordsmusicapp.remote.genius.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;

@Getter
public class SongResponseModel implements Parcelable {

    public static final Creator<SongResponseModel> CREATOR = new Creator<SongResponseModel>() {
        @Override
        public SongResponseModel createFromParcel(Parcel in) {
            return new SongResponseModel(in);
        }

        @Override
        public SongResponseModel[] newArray(int size) {
            return new SongResponseModel[size];
        }
    };
    private final SongModel song;

    protected SongResponseModel(Parcel in) {
        song = in.readParcelable(SongModel.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(song, i);
    }
}
