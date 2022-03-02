package com.puzzle.industries.chordsmusicapp.remote.genius.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;

@Getter
public class PlainDescriptionModel implements Parcelable {

    public static final Creator<PlainDescriptionModel> CREATOR = new Creator<PlainDescriptionModel>() {
        @Override
        public PlainDescriptionModel createFromParcel(Parcel in) {
            return new PlainDescriptionModel(in);
        }

        @Override
        public PlainDescriptionModel[] newArray(int size) {
            return new PlainDescriptionModel[size];
        }
    };
    private final String plain;

    protected PlainDescriptionModel(Parcel in) {
        plain = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(plain);
    }
}
