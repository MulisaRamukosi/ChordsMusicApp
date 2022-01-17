package com.puzzle.industries.chordsmusicapp.models.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import lombok.Getter;

@Getter
public class AlbumDataStruct implements Parcelable {

    private final int id;
    private final String title;
    private final String cover_big;
    private final ArtistDataStruct artist;
    private final String release_date;

    protected AlbumDataStruct(Parcel in) {
        id = in.readInt();
        title = in.readString();
        cover_big = in.readString();
        artist = in.readParcelable(ArtistDataStruct.class.getClassLoader());
        release_date = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(cover_big);
        dest.writeParcelable(artist, flags);
        dest.writeString(release_date);
    }
}
