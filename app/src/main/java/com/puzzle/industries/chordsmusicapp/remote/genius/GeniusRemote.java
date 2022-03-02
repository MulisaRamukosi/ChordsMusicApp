package com.puzzle.industries.chordsmusicapp.remote.genius;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.puzzle.industries.chordsmusicapp.BuildConfig;
import com.puzzle.industries.chordsmusicapp.utils.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeniusRemote {

    private static Retrofit sRetrofit;

    public static <T> T getClient(Class<T> cls) {
        if (sRetrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.connectTimeout(30, TimeUnit.SECONDS);

            builder.addInterceptor(new GeniusRequestInterceptor());

            OkHttpClient client = builder.build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();

            sRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.GENIUS_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return sRetrofit.create(cls);
    }

    private static class GeniusRequestInterceptor implements Interceptor {
        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", String.format("Bearer %s", BuildConfig.GENIUS_SECRET_KEY))
                    .build();
            return chain.proceed(request);
        }
    }
}
