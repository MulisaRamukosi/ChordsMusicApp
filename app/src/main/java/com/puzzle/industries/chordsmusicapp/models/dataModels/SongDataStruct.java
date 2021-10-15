package com.puzzle.industries.chordsmusicapp.models.dataModels;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.Getter;

@Getter
public class SongDataStruct implements Parcelable {

    private final int id;
    private final long duration;

    @SerializedName("title")
    private final String songName;

    private final ArtistDataStruct artist;
    private final AlbumDataStruct album;

    private final String songGenre;
    private final String songYear;
    private final int disk_number;
    private Date release_date;

    private final long downloadId;

    protected SongDataStruct(Parcel in) {
        id = in.readInt();
        duration = in.readLong();
        songName = in.readString();
        artist = in.readParcelable(ArtistDataStruct.class.getClassLoader());
        album = in.readParcelable(AlbumDataStruct.class.getClassLoader());
        songGenre = in.readString();
        songYear = in.readString();
        disk_number = in.readInt();
        try {
            release_date = new SimpleDateFormat(Constants.FORMAT_DATE, Locale.getDefault()).parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        downloadId = in.readLong();
    }

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
        dest.writeLong(duration);
        dest.writeString(songName);
        dest.writeParcelable(artist, flags);
        dest.writeParcelable(album, flags);
        dest.writeString(songGenre);
        dest.writeString(songYear);
        dest.writeInt(disk_number);
        dest.writeString(release_date == null ? new Date().toString() : release_date.toString());
        dest.writeLong(downloadId);
    }
}
