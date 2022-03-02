package com.puzzle.industries.chordsmusicapp.models.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;

@Getter
public class AlbumDataStruct implements Parcelable {

    public static final Creator<AlbumDataStruct> CREATOR = new Creator<AlbumDataStruct>() {
        @Override
        public AlbumDataStruct createFromParcel(Parcel in) {
            return new AlbumDataStruct(in);
        }

        @Override
        public AlbumDataStruct[] newArray(int size) {
            return new AlbumDataStruct[size];
        }
    };
    private final int id;
    private final String title;
    private final String cover_xl;
    private final ArtistDataStruct artist;
    private final String release_date;

    protected AlbumDataStruct(Parcel in) {
        id = in.readInt();
        title = in.readString();
        cover_xl = in.readString();
        artist = in.readParcelable(ArtistDataStruct.class.getClassLoader());
        release_date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(cover_xl);
        dest.writeParcelable(artist, flags);
        dest.writeString(release_date);
    }
}
