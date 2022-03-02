package com.puzzle.industries.chordsmusicapp.remote.genius.models;

public class AnnotationResultModel {

    private AnnotationResponseModel response;

    public String getLyricsExplanation(){
        return response.getAnnotation().getBody().getPlain();
    }
}
