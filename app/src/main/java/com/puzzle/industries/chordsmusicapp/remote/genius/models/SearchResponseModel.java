package com.puzzle.industries.chordsmusicapp.remote.genius.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import lombok.Getter;

@Getter
public class SearchResponseModel implements Parcelable {

    public static final Creator<SearchResponseModel> CREATOR = new Creator<SearchResponseModel>() {
        @Override
        public SearchResponseModel createFromParcel(Parcel in) {
            return new SearchResponseModel(in);
        }

        @Override
        public SearchResponseModel[] newArray(int size) {
            return new SearchResponseModel[size];
        }
    };
    private final ArrayList<HitsModel> hits;

    protected SearchResponseModel(Parcel in) {
        hits = in.createTypedArrayList(HitsModel.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(hits);
    }
}
