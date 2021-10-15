package com.puzzle.industries.chordsmusicapp.events;

import android.os.Parcel;
import android.os.Parcelable;

import com.puzzle.industries.chordsmusicapp.database.entities.AlbumArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.ArtistEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class SongInfoProgressEvent implements Parcelable {

    private final TrackArtistAlbumEntity currentSong;
    private final ArtistEntity currentArtist;
    private final AlbumArtistEntity currentAlbum;
    private final int currProgressInMilis;
    private final int songDurationInMilis;
    @Setter private boolean isPlaying;

    protected SongInfoProgressEvent(Parcel in) {
        currentSong = in.readParcelable(TrackArtistAlbumEntity.class.getClassLoader());
        currentArtist = in.readParcelable(ArtistEntity.class.getClassLoader());
        currentAlbum = in.readParcelable(AlbumArtistEntity.class.getClassLoader());
        currProgressInMilis = in.readInt();
        songDurationInMilis = in.readInt();
        isPlaying = in.readByte() == 1;
    }

    public static final Creator<SongInfoProgressEvent> CREATOR = new Creator<SongInfoProgressEvent>() {
        @Override
        public SongInfoProgressEvent createFromParcel(Parcel in) {
            return new SongInfoProgressEvent(in);
        }

        @Override
        public SongInfoProgressEvent[] newArray(int size) {
            return new SongInfoProgressEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(currentSong, flags);
        dest.writeParcelable(currentArtist, flags);
        dest.writeParcelable(currentAlbum, flags);
        dest.writeInt(currProgressInMilis);
        dest.writeInt(songDurationInMilis);
        dest.writeByte((byte) (isPlaying ? 1 : 0));
    }
}
