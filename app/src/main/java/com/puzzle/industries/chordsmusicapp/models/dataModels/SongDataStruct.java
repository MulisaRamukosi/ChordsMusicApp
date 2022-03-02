package com.puzzle.industries.chordsmusicapp.models.dataModels;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SongDataStruct implements Parcelable {

    public static final Creator<SongDataStruct> CREATOR = new Creator<SongDataStruct>() {
        @Override
        public SongDataStruct createFromParcel(Parcel in) {
            return new SongDataStruct(in);
        }

        @Override
        public SongDataStruct[] newArray(int size) {
            return new SongDataStruct[size];
        }
    };
    private final int id;
    @SerializedName("title")
    private final String songName;
    private final String release_date;
    @Setter private ArtistDataStruct artist;
    @Setter private AlbumDataStruct album;
    @Setter private int track_position;

    public SongDataStruct(int id, String songName, ArtistDataStruct artist, AlbumDataStruct album, int track_position, String release_date) {
        this.id = id;
        this.songName = songName;
        this.artist = artist;
        this.album = album;
        this.track_position = track_position;
        this.release_date = release_date;
    }

    protected SongDataStruct(Parcel in) {
        id = in.readInt();
        songName = in.readString();
        artist = in.readParcelable(ArtistDataStruct.class.getClassLoader());
        album = in.readParcelable(AlbumDataStruct.class.getClassLoader());
        track_position = in.readInt();
        release_date = in.readString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof SongDataStruct && id == ((SongDataStruct) obj).id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(songName);
        dest.writeParcelable(artist, flags);
        dest.writeParcelable(album, flags);
        dest.writeInt(track_position);
        dest.writeString(release_date);
    }
}
