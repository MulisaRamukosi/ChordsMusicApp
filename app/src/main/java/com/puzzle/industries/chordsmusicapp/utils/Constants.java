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

    int DEFAULT_ARTIST_ID = -1;

    String URL_SONG = "SU";

    String ACTION_DOWNLOAD_PROGRESS = "KIF";
    String ACTION_MUSIC_ADDED_TO_LIST = "MQ";
    String ACTION_ALBUM_ADDED_TO_LIST = "ABQ";
    String ACTION_ARTIST_ADDED_TO_LIST = "AQ";
    String ACTION_MUSIC_PROGRESS_UPDATE = "UM";
    String ACTION_MUSIC_DELETED = "AMD";
    String ACTION_ALBUM_DELETED = "AAD";
    String ACTION_ARTIST_DELETED = "AARD";
    String ACTION_PLAYLIST_DELETED = "APD";
    String ACTION_PLAYLIST_TRACK_DELETED = "APTD";
    String ACTION_PLAYLIST_TRACKS_DELETED = "APTSD";
    String ACTION_DOWNLOAD_STATE = "ADS";

    String KEY_DOWNLOAD_PROGRESS = "KDP";
    String KEY_SONG = "KS";
    String KEY_MUSIC_PROGRESS = "MP";
    String KEY_MEDIA_TYPE = "KMT";
    String KEY_ALBUM = "KA";
    String KEY_ARTIST = "KAR";
    String KEY_ARTIST_ID = "KAI";
    String KEY_MEDIA_ID = "KSID";
    String KEY_PLAYLIST = "KPL";
    String KEY_PLAYLIST_TRACKS = "KPLT";
    String KEY_DOWNLOAD_STATE = "KDS";
    String KEY_PLAYLIST_STATE = "KPS";
    String KEY_DISPLAY_CURRENT_PLAYLIST = "KDCP";
    String KEY_PLAYLIST_ID = "KPID";
    String KEY_PLAYLIST_TRACK_ID = "KPTID";
    String KEY_PLAYLIST_NAME = "KPN";
    String KEY_SELECT_REQUEST = "KSR";
    String KEY_ALBUMS = "KAS";
    String KEY_PLAYLIST_TRACK = "KPT";

    String KEY_WAS_UPDATED = "KWU";
}
