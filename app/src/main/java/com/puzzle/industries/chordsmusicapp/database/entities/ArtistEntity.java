package com.puzzle.industries.chordsmusicapp.database.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.puzzle.industries.chordsmusicapp.database.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Entity(tableName = Constants.ENTITY_ARTIST)
public class ArtistEntity implements Parcelable {

    public static final Creator<ArtistEntity> CREATOR = new Creator<ArtistEntity>() {
        @Override
        public ArtistEntity createFromParcel(Parcel in) {
            return new ArtistEntity(in);
        }

        @Override
        public ArtistEntity[] newArray(int size) {
            return new ArtistEntity[size];
        }
    };
    @PrimaryKey
    private final int id;
    private final String name;
    private final String picture_url;

    protected ArtistEntity(Parcel in) {
        id = in.readInt();
        name = in.readString();
        picture_url = in.readString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof ArtistEntity && id == ((ArtistEntity) obj).getId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(picture_url);
    }
}
