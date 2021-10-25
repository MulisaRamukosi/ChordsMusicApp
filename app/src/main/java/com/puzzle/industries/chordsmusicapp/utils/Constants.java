package com.puzzle.industries.chordsmusicapp.utils;

public interface Constants {

    String DEEZER_BASE_URL = "https://api.deezer.com/";
    String WEB_SITE_BASE_URL = "https://tubidy.mobi/search.php";

    String SCRIPT_SEND_QUERY = "SendQueryScript.js";
    String SCRIPT_OPEN_SONG_LINK = "OpenSongLink.js";
    String SCRIPT_ATTEMPT_DOWNLOAD = "AttemptDownload.js";
    String SCRIPT_DOWNLOAD = "Download.js";

    String FORMAT_DATE = "EEE MMM dd HH:mm:ss zzz yyyy";

    int STATE_FIRST_PAGE_LOADED = 0;
    int STATE_RESULTS_PAGE = 1;
    int STATE_SONG_PAGE = 2;
    int STATE_PRIOR_DOWNLOAD = 3;

    String URL_SONG = "SU";

    String KEY_DOWNLOAD_PROGRESS = "KDP";
    String KEY_SONG = "KS";

    String ACTION_IF = "KIF";
    String ACTION_MUSIC_ADDED_TO_QUEUE = "MQ";
    String ACTION_ABQ = "ABQ";
    String ACTION_AQ = "AQ";
    String KEY_MUSIC_UPDATE = "UM";
    String KEY_MUSIC_PROGRESS = "MP";
    //String ACTION_PLAY_PAUSE = "PP";
    //String KEY_SONG_ID = "KSI";
    //String ACTION_SET_LIST = "ASL";
    String KEY_SONG_LIST = "KSL";
    String KEY_ALBUM = "KA";
    //String ACTION_SEEK_TO = "AST";
    String KEY_SEEK = "K_S";
}
