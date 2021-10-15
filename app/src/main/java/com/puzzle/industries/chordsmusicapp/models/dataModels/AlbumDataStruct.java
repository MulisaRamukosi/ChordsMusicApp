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
    private final String artistName;
    private final String title;
    private final String cover_big;
    private final ArtistDataStruct artist;
    private Date release_date;

    protected AlbumDataStruct(Parcel in) {
        id = in.readInt();
        artistName = in.readString();
        title = in.readString();
        cover_big = in.readString();
        artist = in.readParcelable(ArtistDataStruct.class.getClassLoader());
        try {
            release_date = new SimpleDateFormat(Constants.FORMAT_DATE, Locale.getDefault()).parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        dest.writeString(artistName);
        dest.writeString(title);
        dest.writeString(cover_big);
        dest.writeParcelable(artist, flags);
        dest.writeString(release_date == null ? new Date().toString() : release_date.toString());
    }
}
