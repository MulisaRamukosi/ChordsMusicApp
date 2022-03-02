package com.puzzle.industries.chordsmusicapp.remote.genius.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;

@Getter
public class SongResultModel implements Parcelable {

    public static final Creator<SongResultModel> CREATOR = new Creator<SongResultModel>() {
        @Override
        public SongResultModel createFromParcel(Parcel in) {
            return new SongResultModel(in);
        }

        @Override
        public SongResultModel[] newArray(int size) {
            return new SongResultModel[size];
        }
    };
    private final SongResponseModel response;

    protected SongResultModel(Parcel in) {
        response = in.readParcelable(SongResponseModel.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(response, i);
    }
}
