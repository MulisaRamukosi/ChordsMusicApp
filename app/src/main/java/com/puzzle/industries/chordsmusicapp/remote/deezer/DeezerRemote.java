package com.puzzle.industries.chordsmusicapp.remote.deezer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeezerRemote {

    private static Retrofit sRetrofit;

    public static <T> T getClient(Class<T> cls) {
        if (sRetrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.connectTimeout(30, TimeUnit.SECONDS);

            builder.addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader("Accept", "application/json")
                        .build();
                return chain.proceed(request);
            });

            OkHttpClient client = builder.build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();

            sRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.DEEZER_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return sRetrofit.create(cls);
    }
}
