package com.puzzle.industries.chordsmusicapp.database.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlbumArtistEntity implements Parcelable {
    private final int id;
    private final String title;
    private final String cover_url;
    private final int artist_id;
    private final String name;

    protected AlbumArtistEntity(Parcel in) {
        id = in.readInt();
        title = in.readString();
        cover_url = in.readString();
        artist_id = in.readInt();
        name = in.readString();
    }

    public static final Creator<AlbumArtistEntity> CREATOR = new Creator<AlbumArtistEntity>() {
        @Override
        public AlbumArtistEntity createFromParcel(Parcel in) {
            return new AlbumArtistEntity(in);
        }

        @Override
        public AlbumArtistEntity[] newArray(int size) {
            return new AlbumArtistEntity[size];
        }
    };

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof AlbumArtistEntity && id == ((AlbumArtistEntity) obj).getId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(cover_url);
        dest.writeInt(artist_id);
        dest.writeString(name);
    }
}
