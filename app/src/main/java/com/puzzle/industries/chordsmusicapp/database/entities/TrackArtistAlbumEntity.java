package com.puzzle.industries.chordsmusicapp.database.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrackArtistAlbumEntity implements Parcelable {

    private final int id;
    private final String title;
    private final int disk_number;
    private final String location;
    private final String name;
    private final int artist_id;
    private final int album_id;
    private final String cover_url;
    private final String picture_url;

    protected TrackArtistAlbumEntity(Parcel in) {
        id = in.readInt();
        title = in.readString();
        disk_number = in.readInt();
        location = in.readString();
        name = in.readString();
        artist_id = in.readInt();
        album_id = in.readInt();
        cover_url = in.readString();
        picture_url = in.readString();
    }

    public String getFileName(){
        final String[] path = location.split("/");
        return path[path.length - 1];
    }

    public static final Creator<TrackArtistAlbumEntity> CREATOR = new Creator<TrackArtistAlbumEntity>() {
        @Override
        public TrackArtistAlbumEntity createFromParcel(Parcel in) {
            return new TrackArtistAlbumEntity(in);
        }

        @Override
        public TrackArtistAlbumEntity[] newArray(int size) {
            return new TrackArtistAlbumEntity[size];
        }
    };

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof TrackArtistAlbumEntity && id == ((TrackArtistAlbumEntity) obj).id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeInt(disk_number);
        dest.writeString(location);
        dest.writeString(name);
        dest.writeInt(artist_id);
        dest.writeInt(album_id);
        dest.writeString(cover_url);
        dest.writeString(picture_url);
    }
}
