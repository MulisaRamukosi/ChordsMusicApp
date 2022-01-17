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
@Entity(tableName = Constants.ENTITY_PLAYLIST)
public class PlaylistEntity implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @Setter private int id;
    private final String name;

    protected PlaylistEntity(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<PlaylistEntity> CREATOR = new Creator<PlaylistEntity>() {
        @Override
        public PlaylistEntity createFromParcel(Parcel in) {
            return new PlaylistEntity(in);
        }

        @Override
        public PlaylistEntity[] newArray(int size) {
            return new PlaylistEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof PlaylistEntity && ((PlaylistEntity) obj).id == id && ((PlaylistEntity) obj).name.equals(name);
    }
}
