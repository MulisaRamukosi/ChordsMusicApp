package com.puzzle.industries.chordsmusicapp.utils;

import com.puzzle.industries.chordsmusicapp.Chords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ScriptLoaderUtils {

    private static final ScriptLoaderUtils instance = new ScriptLoaderUtils();

    public static ScriptLoaderUtils getInstance() {
        return instance;
    }

    public String getScript(String name) throws IOException {
        InputStream scriptStream = Chords.getAppContext().getAssets().open(name);
        InputStreamReader scriptStreamReader = new InputStreamReader(scriptStream);
        BufferedReader reader = new BufferedReader(scriptStreamReader);

        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append('\n');
        }

        reader.close();
        scriptStreamReader.close();
        scriptStream.close();

        return stringBuilder.toString();
    }
}