package by.tux.weatherbot.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;

public class ApiClient {
    private static OkHttpClient okHttpClient;

    public static void init() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
//                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .build();
        }
    }
    public static String Get(String url)  {
        init();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        }
        catch (IOException e) {
            e.printStackTrace();
            return "fail";
        }
    }
}
