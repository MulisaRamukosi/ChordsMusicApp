package com.puzzle.industries.chordsmusicapp.remote.genius.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.Getter;

@Getter
public class ArtistModel implements Parcelable {

    private final List<String> alternate_names;
    private final PlainDescriptionModel description;

    protected ArtistModel(Parcel in) {
        alternate_names = in.createStringArrayList();
        description = in.readParcelable(PlainDescriptionModel.class.getClassLoader());
    }

    public static final Creator<ArtistModel> CREATOR = new Creator<ArtistModel>() {
        @Override
        public ArtistModel createFromParcel(Parcel in) {
            return new ArtistModel(in);
        }

        @Override
        public ArtistModel[] newArray(int size) {
            return new ArtistModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(alternate_names);
        parcel.writeParcelable(description, i);
    }
}
