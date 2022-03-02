package com.puzzle.industries.chordsmusicapp.remote.genius.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;

@Getter
public class HitsModel implements Parcelable {

    public static final Creator<HitsModel> CREATOR = new Creator<HitsModel>() {
        @Override
        public HitsModel createFromParcel(Parcel in) {
            return new HitsModel(in);
        }

        @Override
        public HitsModel[] newArray(int size) {
            return new HitsModel[size];
        }
    };
    private final HitModel result;

    protected HitsModel(Parcel in) {
        result = in.readParcelable(HitModel.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(result, i);
    }
}
