package com.baojie.retrofitlearn.net.requestinterface;

import com.baojie.retrofitlearn.bean.Translation;
import com.baojie.retrofitlearn.bean.Translation1;
import com.baojie.retrofitlearn.bean.Translation2;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * @Description: TODO
 * @Author baojie@qding.me
 * @Date 2018/8/22 上午9:16
 * @Version TODO
 */

public interface GetRequest_Interface {

    @GET("ajax.php?a=fy&f=auto&t=auto&w=hello%20world")
    Observable<Translation> getCall();

    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20register")
    Observable<Translation1> getCall1();

    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20login")
    Observable<Translation2> getCall2();
}
