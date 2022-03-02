package com.puzzle.industries.chordsmusicapp.remote.genius.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VerseModel implements Parcelable {

    private String verse;
    private String explanation;

    protected VerseModel(Parcel in) {
        verse = in.readString();
        explanation = in.readString();
    }

    public static final Creator<VerseModel> CREATOR = new Creator<VerseModel>() {
        @Override
        public VerseModel createFromParcel(Parcel in) {
            return new VerseModel(in);
        }

        @Override
        public VerseModel[] newArray(int size) {
            return new VerseModel[size];
        }
    };

    public boolean hasExplanation(){
        return !explanation.isEmpty();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(verse);
        parcel.writeString(explanation);
    }
}
