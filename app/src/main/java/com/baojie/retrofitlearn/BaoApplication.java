package com.baojie.retrofitlearn;

import android.app.Application;

import com.baojie.retrofitlearn.net.ApiClient;

/**
 * @Description: TODO
 * @Author baojie@qding.me
 * @Date 2018/8/21 下午2:26
 * @Version TODO
 */

public class BaoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initRetrofit();
    }

    private void initRetrofit() {
        ApiClient.create(C.BaseURL.WAN_ANDROID_BASE_URL);
        ApiClient.create(C.BaseURL.JINSHAN_BASE_URL);
    }
}
