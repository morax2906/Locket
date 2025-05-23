package com.tandev.locket.api.client;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginApiClient {
    private static Retrofit checkEmailRetrofit = null;
    private static Retrofit loginRetrofit = null;
    private static Retrofit refreshTokenRetrofit = null;
    private static final String CHECK_EMAIL_BASE_URL = "https://api.locketcamera.com/";
    private static final String LOGIN_BASE_URL = "https://www.googleapis.com/";
    private static final String REFRESH_TOKEN_BASE_URL = "https://securetoken.googleapis.com/";

    // check email
    public static Retrofit getCheckEmailClient() {
        if (checkEmailRetrofit == null) {
            checkEmailRetrofit = new Retrofit.Builder()
                    .baseUrl(CHECK_EMAIL_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return checkEmailRetrofit;
    }

    // Retrofit cho việc đăng nhập
    public static Retrofit getLoginClient() {
        if (loginRetrofit == null) {
            loginRetrofit = new Retrofit.Builder()
                    .baseUrl(LOGIN_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return loginRetrofit;
    }
    // Retrofit cho việc đăng nhập
    public static Retrofit getRefreshTokenClient() {
        if (refreshTokenRetrofit == null) {
            refreshTokenRetrofit = new Retrofit.Builder()
                    .baseUrl(REFRESH_TOKEN_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return refreshTokenRetrofit;
    }
}
