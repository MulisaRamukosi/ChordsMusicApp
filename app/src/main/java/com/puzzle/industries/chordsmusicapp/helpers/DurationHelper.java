package com.puzzle.industries.chordsmusicapp.helpers;

import java.util.Locale;

public class DurationHelper {



    public static long[] millisecondsToMinutesSeconds(long millis){
        long minutes = (millis / 1000)  / 60;
        long seconds = (int)((millis / 1000) % 60);

        return new long[]{minutes, seconds};
    }

    public static String minutesSecondsToString(long millis){
        long[] ms = millisecondsToMinutesSeconds(millis);
        return String.format(Locale.getDefault(), "%d:%d", ms[0], ms[1]);
    }

}
