package com.puzzle.industries.chordsmusicapp.models.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ArtistDataStruct implements Parcelable {

    private final int id;
    private final String name;
    private final String picture_big;

    protected ArtistDataStruct(Parcel in) {
        id = in.readInt();
        name = in.readString();
        picture_big = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(picture_big);
    }
}
