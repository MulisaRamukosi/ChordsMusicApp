package com.puzzle.industries.chordsmusicapp.remote.genius.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;

@Getter
public class ArtistResultModel implements Parcelable {

    private final ArtistResponseModel response;

    protected ArtistResultModel(Parcel in) {
        response = in.readParcelable(ArtistResponseModel.class.getClassLoader());
    }

    public static final Creator<ArtistResultModel> CREATOR = new Creator<ArtistResultModel>() {
        @Override
        public ArtistResultModel createFromParcel(Parcel in) {
            return new ArtistResultModel(in);
        }

        @Override
        public ArtistResultModel[] newArray(int size) {
            return new ArtistResultModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(response, i);
    }
}
