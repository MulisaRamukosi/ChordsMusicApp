package com.puzzle.industries.chordsmusicapp.models.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;

@Getter
public class ArtistDataStruct implements Parcelable {

    public static final Creator<ArtistDataStruct> CREATOR = new Creator<ArtistDataStruct>() {
        @Override
        public ArtistDataStruct createFromParcel(Parcel in) {
            return new ArtistDataStruct(in);
        }

        @Override
        public ArtistDataStruct[] newArray(int size) {
            return new ArtistDataStruct[size];
        }
    };
    private final int id;
    private final String name;
    private final String picture_xl;

    protected ArtistDataStruct(Parcel in) {
        id = in.readInt();
        name = in.readString();
        picture_xl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(picture_xl);
    }
}
