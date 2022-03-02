package com.puzzle.industries.chordsmusicapp.database.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.puzzle.industries.chordsmusicapp.database.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Entity(tableName = Constants.ENTITY_PLAYLIST_TRACK)
public class PlaylistTrackEntity implements Parcelable {
    public static final Creator<PlaylistTrackEntity> CREATOR = new Creator<PlaylistTrackEntity>() {
        @Override
        public PlaylistTrackEntity createFromParcel(Parcel in) {
            return new PlaylistTrackEntity(in);
        }

        @Override
        public PlaylistTrackEntity[] newArray(int size) {
            return new PlaylistTrackEntity[size];
        }
    };
    private final int trackId;
    @PrimaryKey(autoGenerate = true)
    @Setter
    private int id;
    @Setter private int playlistId;

    protected PlaylistTrackEntity(Parcel in) {
        id = in.readInt();
        playlistId = in.readInt();
        trackId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(playlistId);
        parcel.writeInt(trackId);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof PlaylistTrackEntity) {
            final PlaylistTrackEntity playlistTrack = (PlaylistTrackEntity) obj;
            return playlistTrack.playlistId == playlistId && playlistTrack.trackId == trackId && playlistTrack.id == id;
        }
        return false;
    }
}
