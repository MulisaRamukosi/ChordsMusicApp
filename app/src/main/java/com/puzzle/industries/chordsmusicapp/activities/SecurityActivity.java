package com.puzzle.industries.chordsmusicapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.puzzle.industries.chordsmusicapp.BuildConfig;
import com.puzzle.industries.chordsmusicapp.base.BaseActivity;
import com.puzzle.industries.chordsmusicapp.databinding.ActivitySecurityBinding;
import com.puzzle.industries.chordsmusicapp.utils.CryptographyUtil;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class SecurityActivity extends BaseActivity {

    private ActivitySecurityBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isVerified()) {
            openSplashScreen();
        }

        mBinding = ActivitySecurityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        String secretWord = generateRandomWord();
        final Key key = Keys.hmacShaKeyFor(BuildConfig.SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        final String token = Jwts.builder().setSubject(secretWord)
                .signWith(key)
                .compact();

        try {
            final SecretKey secretKey = CryptographyUtil.generateKey(BuildConfig.SECRET_KEY);
            final byte[] encryptedToken = CryptographyUtil.encryptMsg(token, secretKey);
            mBinding.tvToken.setText(Arrays.toString(encryptedToken));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException
                | BadPaddingException | UnsupportedEncodingException | InvalidParameterSpecException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        mBinding.btn.setOnClickListener(v -> {
            final String answer = Objects.requireNonNull(mBinding.tilSearch.getEditText()).getText().toString().trim();
            if (answer.isEmpty()) mBinding.tilSearch.setError("Who the hell are you?");
            else {
                if (Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject().equals(answer)) {
                    setAsVerified();
                    openSplashScreen();
                } else {
                    mBinding.tilSearch.getEditText().setText("");
                    mBinding.tilSearch.setError("Who the hell are you?");
                }
            }
        });

    }

    private void setAsVerified() {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.edit().putBoolean("VERIFIED", true).apply();
    }

    private void openSplashScreen() {
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    private boolean isVerified() {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        return pref.getBoolean("VERIFIED", false);
    }

    private String generateRandomWord() {
        final int leftLimit = 97;
        final int rightLimit = 122;
        final int targetStringLength = 16;
        final Random random = new Random();
        final StringBuilder buffer = new StringBuilder(targetStringLength);

        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }
}
