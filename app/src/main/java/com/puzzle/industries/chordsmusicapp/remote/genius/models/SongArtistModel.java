package com.puzzle.industries.chordsmusicapp.remote.genius.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;

@Getter
public class SongArtistModel implements Parcelable {

    public static final Creator<SongArtistModel> CREATOR = new Creator<SongArtistModel>() {
        @Override
        public SongArtistModel createFromParcel(Parcel in) {
            return new SongArtistModel(in);
        }

        @Override
        public SongArtistModel[] newArray(int size) {
            return new SongArtistModel[size];
        }
    };

    private final int id;
    private final String name;
    private final String image_url;
    private final String header_image_url;
    private final String url;

    protected SongArtistModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        image_url = in.readString();
        header_image_url = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(image_url);
        parcel.writeString(header_image_url);
        parcel.writeString(url);
    }
}
