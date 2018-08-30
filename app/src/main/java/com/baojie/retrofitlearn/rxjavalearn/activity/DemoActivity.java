package com.baojie.retrofitlearn.rxjavalearn.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.baojie.retrofitlearn.C;
import com.baojie.retrofitlearn.R;
import com.baojie.retrofitlearn.bean.Translation;
import com.baojie.retrofitlearn.bean.Translation1;
import com.baojie.retrofitlearn.bean.Translation2;
import com.baojie.retrofitlearn.net.ApiClient;
import com.baojie.retrofitlearn.net.requestinterface.GetRequest_Interface;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * @Description: TODO
 * @Author baojie@qding.me
 * @Date 2018/8/23 上午10:17
 * @Version TODO
 */

public class DemoActivity extends AppCompatActivity{

    public static String TAG = "DemoActivity";

    // 设置变量 = 模拟轮询服务器次数
    private int i = 0 ;
    private GetRequest_Interface request;
    private Observable<Translation> observable;
    private int currentRetryCount = 0;
    private int maxConnectCount = 10;
    // 重试等待时间
    private int waitRetryTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        init();
        //mergeDemo();

        //zipDemo();

        //getData();

        //againDemo();

        changeAgain();

    }

    private void init() {
        GetRequest_Interface request = ApiClient.get(C.BaseURL.JINSHAN_BASE_URL).create
                (GetRequest_Interface.class);
        observable = request.getCall();
    }


    private void changeAgain() {
        // 注：主要异常才会回调retryWhen（）进行重试
        observable.retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> objectObservable) throws Exception {
                return objectObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    // 参数Observable<Throwable>中的泛型 = 上游操作符抛出的异常，可通过该条件来判断异常的类型
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        // 输出异常信息
                        Log.d(TAG,  "发生异常 = "+ throwable.toString());
                        if(throwable instanceof IOException){
                            Log.d(TAG,  "属于IO异常，需重试" );
                            /**
                             * 需求2：限制重试次数
                             * 即，当已重试次数 < 设置的重试次数，才选择重试
                             */
                            if (currentRetryCount < maxConnectCount){
                                currentRetryCount++;
                                Log.d(TAG,  "重试次数 = " + currentRetryCount);
                                /**
                                 * 需求2：实现重试
                                 * 通过返回的Observable发送的事件 = Next事件，从而使得retryWhen（）重订阅，最终实现重试功能
                                 *
                                 * 需求3：延迟1段时间再重试
                                 * 采用delay操作符 = 延迟一段时间发送，以实现重试间隔设置
                                 *
                                 * 需求4：遇到的异常越多，时间越长
                                 * 在delay操作符的等待时间内设置 = 每重试1次，增多延迟重试时间1s
                                 */
                                // 设置等待时间
                                waitRetryTime = 1000 + currentRetryCount* 1000;
                                Log.d(TAG,  "等待时间 =" + waitRetryTime);
                                return Observable.just(1).delay(waitRetryTime, TimeUnit.MILLISECONDS);
                            }else {
                                return Observable.error(new Throwable("重试次数已超过设置次数= "+ currentRetryCount));
                            }
                        }else {
                            return Observable.error(new Throwable("发生了非网络异常（非I/O异常）"));
                        }
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Translation>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Translation translation) {
                        // 接收服务器返回的数据
                        Log.d(TAG,  "发送成功");
                        translation.show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 获取停止重试的信息
                        Log.d(TAG,  e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void againDemo() {
        GetRequest_Interface request = ApiClient.get(C.BaseURL.JINSHAN_BASE_URL).create
                (GetRequest_Interface.class);

        final Observable<Translation> observable = request.getCall();
        observable.repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            // 在Function函数中，必须对输入的 Observable<Object>进行处理，此处使用flatMap操作符接收上游的数据
            @Override
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                // 将原始 Observable 停止发送事件的标识（Complete（） /  Error（））转换成1个 Object 类型数据传递给1个新被观察者（Observable）
                // 以此决定是否重新订阅 & 发送原来的 Observable，即轮询
                // 此处有2种情况：
                // 1. 若返回1个Complete（） /  Error（）事件，则不重新订阅 & 发送原来的 Observable，即轮询结束
                // 2. 若返回其余事件，则重新订阅 & 发送原来的 Observable，即继续轮询
                return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        if (i > 3){
                            return Observable.error(new Throwable("轮询结束"));
                        }
                        // 若轮询次数＜4次，则发送1Next事件以继续轮询
                        // 注：此处加入了delay操作符，作用 = 延迟一段时间发送（此处设置 = 2s），以实现轮询间间隔设置
                        return Observable.just(1).delay(3000, TimeUnit.MILLISECONDS);
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Translation>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Translation translation) {
                        // e.接收服务器返回的数据
                        translation.show() ;
                        i++;
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 获取轮询结束信息
                        Log.d(TAG,  e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getData() {
        final String memoryCache = null;
        final String diskCache = "从磁盘缓存中获取数据";
        //第一个observable，检查内存缓存
        Observable<String> memory = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 先判断内存缓存有无数据
                if (memoryCache != null){
                    // 若有该数据，则发送
                    e.onNext(memoryCache);
                }else {
                    // 若无该数据，则直接发送结束事件
                    e.onComplete();
                }
            }
        });

        //第二个observable，检查磁盘是否有数据
        Observable<String> disk = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (diskCache != null){
                    e.onNext(diskCache);
                }else {
                    e.onComplete();
                }
            }
        });

        //第三个observable我，获取网络请求
        Observable<String> network = Observable.just("网络数据");

        // 1. 通过concat（）合并memory、disk、network 3个被观察者的事件（即检查内存缓存、磁盘缓存 & 发送网络请求）
        //    并将它们按顺序串联成队列
        Observable.concat(memory, disk, network)
                // 2. 通过firstElement()，从串联队列中取出并发送第1个有效事件（Next事件），即依次判断检查memory、disk、network
                .firstElement()
        // a. firstElement()取出第1个事件 = memory，即先判断内存缓存中有无数据缓存；由于memoryCache = null，即内存缓存中无数据，所以发送结束事件（视为无效事件）
        // b. firstElement()继续取出第2个事件 = disk，即判断磁盘缓存中有无数据缓存：由于diskCache ≠ null，即磁盘缓存中有数据，所以发送Next事件（有效事件）
        // c. 即firstElement()已发出第1个有效事件（disk事件），所以停止判断。
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "finallData: "+ s);

                    }
                });
    }

    private void zipDemo() {
        GetRequest_Interface request = ApiClient.get(C.BaseURL.JINSHAN_BASE_URL).create
                (GetRequest_Interface.class);
        Observable<Translation1> observable1 = request.getCall1().subscribeOn(Schedulers.io());
        Observable<Translation2> observable2 = request.getCall2().subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<Translation1, Translation2, String>() {
            @Override
            public String apply(Translation1 translation1, Translation2 translation2) throws Exception {
                return translation1.getOut() + "&" + translation2.getOut();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        // 结合显示2个网络请求的数据结果
                        Log.d(TAG, "最终接收到的数据是：" + s);
                    }
                });
    }

    private void mergeDemo() {
        final StringBuilder result = new StringBuilder();
        result.append("数据来自= ");
        Observable<String> network = Observable.just("网络");
        Observable<String> localFile = Observable.just("本地");

        Observable.merge(network, localFile)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "数据源有：" + s);
                        result.append(s + "+");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "获取数据完成");
                        Log.d(TAG, result.toString());
                    }
                });
    }
}
