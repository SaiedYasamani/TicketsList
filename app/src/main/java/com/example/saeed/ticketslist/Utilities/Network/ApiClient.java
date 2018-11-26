package com.example.saeed.ticketslist.Utilities.Network;

import com.bumptech.glide.request.RequestCoordinator;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String TAG = ApiClient.class.getSimpleName();
    private static final Integer REQUEST_TIMEOUT = 5000;
    private static Retrofit retrofit = null;
    private static OkHttpClient client = null;

    public static Retrofit getClient()
    {
        if(client == null)
        {
            client = getOkHttpClient();
        }

        if(retrofit == null)
        {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("https://api.androidhive.info/json/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client);
            retrofit = builder.build();
        }
        return retrofit;
    }

    public static OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(REQUEST_TIMEOUT,TimeUnit.MILLISECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        Request origin = chain.request();
                        Request.Builder requestBuilder = origin.newBuilder()
                                .addHeader("Accept" , "application/json")
                                .addHeader("Request-Type" , "Android")
                                .addHeader("Content-Type" , "application/json");
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });
        return builder.build();
    }


}
