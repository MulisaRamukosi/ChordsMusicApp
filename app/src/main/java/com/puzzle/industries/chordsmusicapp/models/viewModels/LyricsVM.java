package com.puzzle.industries.chordsmusicapp.models.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.puzzle.industries.chordsmusicapp.Chords;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackArtistAlbumEntity;
import com.puzzle.industries.chordsmusicapp.database.entities.TrackLyricsEntity;
import com.puzzle.industries.chordsmusicapp.remote.genius.api.GeniusApiCall;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.AnnotationResultModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.LyricModel;
import com.puzzle.industries.chordsmusicapp.remote.genius.models.VerseModel;
import com.puzzle.industries.chordsmusicapp.services.impl.ExecutorServiceManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LyricsVM extends ViewModel {

    private MutableLiveData<LyricModel> lyricModel;

    public LiveData<LyricModel> getSongLyricsObservable(){
        if (lyricModel == null){
            lyricModel = new MutableLiveData<>();
        }

        return lyricModel;
    }

    public void downloadLyrics(TrackArtistAlbumEntity song, String lyricsUrl){
        fetchLyrics(song, lyricsUrl);
    }

    private void fetchLyrics(TrackArtistAlbumEntity song, String lyricsUrl) {
        ExecutorServiceManager.getInstance()
                .executeRunnableOnSingeThread(
                        () -> {
                            final TrackLyricsEntity trackLyrics = Chords.getDatabase()
                                    .trackLyricsDao().getTrackLyricsById(song.getId());

                            if (trackLyrics != null){
                                final LyricModel lyrics = new Gson().fromJson(trackLyrics.getLyrics(), LyricModel.class);
                                this.lyricModel.postValue(lyrics);
                            }
                            else{
                                downloadSongLyrics(song, lyricsUrl);
                            }
                        }
                );
    }

    private void downloadSongLyrics(TrackArtistAlbumEntity song, String lyricsUrl) {
        try {
            final LyricModel lyricModel = getSongLyrics(lyricsUrl);
            final String songLyrics = new Gson().toJson(lyricModel);
            Chords.getDatabase().trackLyricsDao().insertTrackLyrics(new TrackLyricsEntity(song.getId(), songLyrics));
            this.lyricModel.postValue(lyricModel);
        } catch (IOException e) {
            e.printStackTrace();
            this.lyricModel.postValue(null);
        }
    }


    private LyricModel getSongLyrics(String lyricsUrl) throws IOException {
        final Connection connection = Jsoup.connect(lyricsUrl);
        connection.userAgent("Mozilla/5.0");
        final Document doc = connection.get();
        final Element lyrics = doc.select("#lyrics-root").first();
        final ArrayList<VerseModel> tempLyricsVerses = new ArrayList<>();

        if (lyrics != null){
            final Elements elements = lyrics.children();
            for (Element element : elements){
                buildLyricsVerses(tempLyricsVerses, element, "");
            }
            final ArrayList<VerseModel> lyricVerses = generateCleanLyricsVerses(tempLyricsVerses);
            attachLyricsExplanation(lyricVerses);
            return new LyricModel(lyricVerses);
        }
        return null;
    }

    private void buildLyricsVerses(ArrayList<VerseModel> tempLyricsVerses, Element element, String annotationId) {
        final List<Node> elements = element.childNodes();
        if (elements.size() > 0){
            for (Node node : elements){
                if (node instanceof TextNode){
                    TextNode textNode = (TextNode) node;
                    tempLyricsVerses.add(new VerseModel(textNode.text(), annotationId));
                }
                else if (node instanceof Element){
                    Element childElement = (Element) node;
                    String newAnnotationId = annotationId;

                    if (childElement.tagName().equals("a")){
                        final String href = childElement.attr("href");
                        final String[] hrefVals = href.split("/");
                        newAnnotationId = hrefVals[1];
                    }
                    buildLyricsVerses(tempLyricsVerses, childElement, newAnnotationId);
                }
            }
        }
    }

    private ArrayList<VerseModel> generateCleanLyricsVerses(ArrayList<VerseModel> tempLyricsVerses){
        final ArrayList<VerseModel> lyricsVerses = new ArrayList<>();
        VerseModel temp = new VerseModel("", "");

        for (int i = 0; i < tempLyricsVerses.size() - 2; i++){
            final VerseModel verseModel = tempLyricsVerses.get(i);
            if (temp.getExplanation().equals(verseModel.getExplanation())){
                temp.setVerse(String.format("%s\n%s", temp.getVerse(), verseModel.getVerse()));
            }
            else{
                String verse = temp.getVerse();
                if (!verse.isEmpty()){
                    addVerseToList(temp, lyricsVerses);
                }
                temp = new VerseModel(verseModel.getVerse(), verseModel.getExplanation());
            }
        }

        addVerseToList(temp, lyricsVerses);


        return lyricsVerses;
    }

    private void addVerseToList(VerseModel temp, ArrayList<VerseModel> lyricsVerses) {
        String verse = temp.getVerse();
        if (!verse.isEmpty()){
            if (verse.contains("(") && verse.contains(")")) verse = fixWrongNewLineBetween("(", ")", verse);
            if (verse.contains("[") && verse.contains("]")) verse = fixWrongNewLineBetween("[", "]", verse);

            if (!verse.startsWith("[") && verse.contains("\n")){
                verse = verse.replace("[", "\n[");
            }

            temp.setVerse(verse);
            lyricsVerses.add(temp);
        }
    }

    private String fixWrongNewLineBetween(String startChars, String endChars, String verse){
        final int startIndex = verse.indexOf(startChars);
        final int endIndex = verse.indexOf(endChars);

        if (startIndex == -1 || endIndex == -1) return verse;

        final String subVerse = verse.substring(startIndex, endIndex);
        final String newVerse = subVerse.replace("\n", "");
        verse = verse.replace(subVerse, newVerse);

        final int newIndex = endIndex + 1;
        String okayString;
        if (newIndex < verse.length()) okayString = verse.substring(0, endIndex + 1);
        else return verse;

        //check if there are similar faults in the string
        String toBeModified = verse.substring(newIndex);
        return okayString + fixWrongNewLineBetween(startChars, endChars, toBeModified);

    }

    private void attachLyricsExplanation(ArrayList<VerseModel> lyricsVerses){
        for (VerseModel verseModel : lyricsVerses){
            if (verseModel.hasExplanation()){
                final AnnotationResultModel annotationResultModel = GeniusApiCall.getInstance().getVerseExplanation(verseModel.getExplanation());
                verseModel.setExplanation(annotationResultModel != null
                        ? annotationResultModel.getLyricsExplanation()
                        : "");
            }
        }
    }

}
