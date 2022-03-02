package com.puzzle.industries.chordsmusicapp.models.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

import com.puzzle.industries.chordsmusicapp.remote.genius.models.SearchResultModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.SongResultModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class SongInfoStruct implements Parcelable {

    public static final Creator<SongInfoStruct> CREATOR = new Creator<SongInfoStruct>() {
        @Override
        public SongInfoStruct createFromParcel(Parcel in) {
            return new SongInfoStruct(in);
        }

        @Override
        public SongInfoStruct[] newArray(int size) {
            return new SongInfoStruct[size];
        }
    };
    @Setter private SongResultModel songResult;
    private final SearchResultModel searchResult;

    protected SongInfoStruct(Parcel in) {
        songResult = in.readParcelable(SongResultModel.class.getClassLoader());
        searchResult = in.readParcelable(SearchResultModel.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(songResult, i);
        parcel.writeParcelable(searchResult, i);
    }
}
