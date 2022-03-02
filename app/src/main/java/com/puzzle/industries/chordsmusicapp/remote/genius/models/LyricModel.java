package com.puzzle.industries.chordsmusicapp.remote.genius.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LyricModel implements Parcelable {

    private final ArrayList<VerseModel> lyricVerses;

    protected LyricModel(Parcel in) {
        lyricVerses = in.createTypedArrayList(VerseModel.CREATOR);
    }

    public static final Creator<LyricModel> CREATOR = new Creator<LyricModel>() {
        @Override
        public LyricModel createFromParcel(Parcel in) {
            return new LyricModel(in);
        }

        @Override
        public LyricModel[] newArray(int size) {
            return new LyricModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(lyricVerses);
    }
}
