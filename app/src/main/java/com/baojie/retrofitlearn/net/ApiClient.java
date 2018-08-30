package com.baojie.retrofitlearn.net;

import com.baojie.retrofitlearn.C;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Description: TODO
 * @Author baojie@qding.me
 * @Date 2018/8/21 下午2:27
 * @Version TODO
 */

public class ApiClient {

    private static ConcurrentHashMap<String, Retrofit> retrofitFactory = new ConcurrentHashMap<>();

    private ApiClient() {
    }

    private ApiClient(String baseUrl) {
        for (Map.Entry<String, Retrofit> retrofitEntry: retrofitFactory.entrySet()){
            if (retrofitEntry.getKey().equals(baseUrl)){
                return;
            }
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        retrofitFactory.put(baseUrl, retrofit);

    }

    //获取okHttpClient
    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//        builder.addInterceptor(addHttpInterceptor());
        return builder.build();
    }

    private Interceptor addHttpInterceptor() {

        return new Interceptor(){
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                Request build = builder.addHeader("Content-type", "application/json; charset=utf-8").build();
                chain.proceed(build);
                return null;
            }
        };
    }

    public static void create(@C.BaseURL String baseUrl){
        new ApiClient(baseUrl);
    }

    public static Retrofit get(String baseUrl){
        return retrofitFactory.get(baseUrl);
    }

    public static Retrofit getDefault(){
        return retrofitFactory.get(C.BaseURL.WAN_ANDROID_BASE_URL);
    }
}
