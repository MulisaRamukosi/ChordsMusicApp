package com.puzzle.industries.chordsmusicapp.remote.musicFinder;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.R;
import com.puzzle.industries.chordsmusicapp.models.dataModels.SongDataStruct;
import com.puzzle.industries.chordsmusicapp.remote.interfaces.ApiCallBack;
import com.puzzle.industries.chordsmusicapp.utils.Constants;
import com.puzzle.industries.chordsmusicapp.utils.ScriptLoaderUtils;

import java.io.IOException;
import java.util.Locale;


public class MusicFinderApi extends WebViewClient implements RetryPolicyListener{

    private final String[] mOffensiveWords;
    private final WebView mWebView;
    private final ScriptLoaderUtils mScriptLoader;
    private final ApiCallBack<String> mCallBack;
    private final SongDataStruct mSong;
    private final IRetryPolicy mRetryPolicy;

    public static final String TAG = "MUSIC_FINDER_API";
    private final int[] STATE_TRACK = new int[]{0};

    @SuppressLint("SetJavaScriptEnabled")
    public MusicFinderApi(SongDataStruct songDataStruct, ApiCallBack<String> callBack) {
        Log.d(TAG, "about to init");
        mOffensiveWords = Chords.getAppContext().getResources().getStringArray(R.array.badWords);
        mCallBack = callBack;
        mWebView = new WebView(Chords.getAppContext());
        mScriptLoader = ScriptLoaderUtils.getInstance();
        mSong = songDataStruct;
        mRetryPolicy = new RetryPolicy(30, 3, this);

        mWebView.setWebViewClient(this);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl(Constants.WEB_SITE_BASE_URL);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBlockNetworkLoads(false);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            mRetryPolicy.stopRetryPolicy();
            callBack.onSuccess(url);
        });

        mRetryPolicy.startRetryPolicy();
        Log.d(TAG, "init complete");

    }

    private String applyBadWordsFix(String query){
        for (String badWord : mOffensiveWords){
            if (query.toLowerCase().contains(badWord)){
                query = query.toLowerCase();
                query = query.replace(badWord, badWord.substring(0, badWord.length() - 1));
            }
        }
        return query;
    }

    private void sendSearchQuery() throws IOException {
        String query = String.format(Locale.US, "%s %s audio", mSong.getArtist().getName(), mSong.getSongName());
        query = applyBadWordsFix(query);
        String searchScript = mScriptLoader.getScript(Constants.SCRIPT_SEND_QUERY);
        searchScript = String.format(searchScript, query);
        mWebView.evaluateJavascript(searchScript, null);
        STATE_TRACK[0] += 1;
        Log.d(TAG, searchScript);
    }

    private void openSongLink() throws IOException{
        final String openLinkScript = mScriptLoader.getScript(Constants.SCRIPT_OPEN_SONG_LINK);
        Log.d(TAG, openLinkScript);
        mWebView.evaluateJavascript(openLinkScript, value -> STATE_TRACK[0] += 1);
    }

    private void attemptToDownloadSong() throws IOException{
        final String attemptDownload = mScriptLoader.getScript(Constants.SCRIPT_ATTEMPT_DOWNLOAD);
        Log.d(TAG, attemptDownload);
        mWebView.evaluateJavascript(attemptDownload, value -> STATE_TRACK[0] += 1);
    }

    private void downloadSong() throws IOException{
        String downloadSongScript = mScriptLoader.getScript(Constants.SCRIPT_DOWNLOAD);
        Log.d(TAG, downloadSongScript);
        mWebView.evaluateJavascript(downloadSongScript, value -> STATE_TRACK[0] += 1);

    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        Log.d(TAG, "on Received http error");
        stopRetryAttemptsIfFinished();
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        Log.d(TAG, "on Received ssl error");
        stopRetryAttemptsIfFinished();
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Log.d(TAG, "on Received error");
        stopRetryAttemptsIfFinished();
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        Log.d(TAG, "Current URL: " + url);
        final int currentState = STATE_TRACK[0];

        try {
            switch (currentState){
                case Constants.STATE_FIRST_PAGE_LOADED: sendSearchQuery(); break;
                case Constants.STATE_RESULTS_PAGE: openSongLink(); break;
                case Constants.STATE_SONG_PAGE: attemptToDownloadSong(); break;
                case Constants.STATE_PRIOR_DOWNLOAD: downloadSong(); break;
            }
        }catch (IOException e){
            mRetryPolicy.stopRetryPolicy();
            mCallBack.onFailure(e);
        }

    }

    private void stopRetryAttemptsIfFinished(){
        if (mRetryPolicy.allAttemptsUsed()){
            mRetryPolicy.stopRetryPolicy();
            mCallBack.onFailure(new Throwable(String.format("Failed to get the url of %s", mSong.getSongName())));
        }
    }

    @Override
    public void onRetryPolicy() {
        STATE_TRACK[0] = 0;
        mRetryPolicy.stopRetryPolicy();
        mRetryPolicy.startRetryPolicy();
        mWebView.loadUrl(Constants.WEB_SITE_BASE_URL);
    }

    @Override
    public void retryAttemptsFinished() {
        mCallBack.onFailure(new Throwable(String.format("Failed to get the url of %s", mSong.getSongName())));
    }
}
