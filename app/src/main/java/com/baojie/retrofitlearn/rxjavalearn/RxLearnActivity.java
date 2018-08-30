package com.baojie.retrofitlearn.rxjavalearn;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.baojie.retrofitlearn.C;
import com.baojie.retrofitlearn.R;
import com.baojie.retrofitlearn.bean.Translation;
import com.baojie.retrofitlearn.bean.Translation1;
import com.baojie.retrofitlearn.bean.Translation2;
import com.baojie.retrofitlearn.net.ApiClient;
import com.baojie.retrofitlearn.net.requestinterface.GetRequest_Interface;
import com.baojie.retrofitlearn.rxjavalearn.activity.DemoActivity;
import com.baojie.retrofitlearn.rxjavalearn.activity.FilterActivity;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * @Description: TODO
 * @Author baojie@qding.me
 * @Date 2018/8/21 下午4:26
 * @Version TODO
 */

public class RxLearnActivity extends AppCompatActivity {

    private static String TAG = "RxLearnActivity";
    private Observer<Integer> integerObserver;
    private Observer<Long> longObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initObserver();

        //first();
        //second();
        //three();
        //four();
        //five();
        //flapMapFun();
        //bufferFun();
        //TwoNetTogeter();
        //concatFun();
        //mergeFun();
        //concatErrorFun();
        //zipFun();
        //combineLatestFun();
        //reduceFun();
        //collectFun();
        //startWithFun();
        //countFun();

        init();

        //doFun();
        //onerrorReturnFun();

        //retryFun();
        //retryWhenFun();
        //repeatFun();

        threadFun();

    }

    private void threadFun() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, " 被观察者 Observable的工作线程是: " + Thread.currentThread().getName());
                e.onNext(1);
                e.onComplete();
            }
        });

        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "开始采用subscribe连接");
                Log.d(TAG, " 观察者 Observer的工作线程是: " + Thread.currentThread().getName());
                // 打印验证
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "对Next事件"+ integer +"作出响应"  );
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        };

        //observable.subscribe(observer);

        // Observable.subscribeOn（Schedulers.Thread）：指定被观察者 发送事件的线程（传入RxJava内置的线程类型）
        // Observable.observeOn（Schedulers.Thread）：指定观察者 接收 & 响应事件的线程（传入RxJava内置的线程类型）
        //1. 若Observable.subscribeOn（）多次指定被观察者 生产事件的线程，则只有第一次指定有效，其余的指定线程无效
        //2. 若Observable.observeOn（）多次指定观察者 接收 & 响应事件的线程，则每次指定均有效，即每指定一次，就会进行一次线程的切换
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void repeatFun() {

    }

    private void retryWhenFun() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new Exception("happend error"));
                e.onNext(4);
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                return null;
            }
        }).subscribe(integerObserver);
    }

    private void retryFun() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Throwable("发生错误了"));
                e.onNext(3);
            }
        }).retry(3)
                .subscribe(integerObserver);
    }

    private void onerrorReturnFun() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Throwable("发生错误了"));
            }
        }).onErrorReturn(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable throwable) throws Exception {
                // 捕捉错误异常
                Log.e(TAG, "在onErrorReturn处理了错误: " + throwable.toString());
                return 666;
                // 发生错误事件后，发送一个"666"事件，最终正常结束
            }
        }).subscribe(integerObserver);
    }

    private void doFun() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onError(new Throwable("发生错误"));
            }
            // 1. 当Observable每发送1次数据事件就会调用1次
        }).doOnEach(new Consumer<Notification<Integer>>() {
            @Override
            public void accept(Notification<Integer> integerNotification) throws Exception {
                Log.d(TAG, "doOnEach: " + integerNotification.getValue());
            }
            //2. 执行Next事件前调用
        }).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "doOnNext: " + integer);
            }
            // 3. 执行Next事件后调用
        }).doAfterNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "doAfterNext: " + integer);
            }
            // 4. Observable正常发送事件完毕后调用
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                Log.e(TAG, "doOnComplete: ");
            }
            // 5. Observable发送错误事件时调用
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.d(TAG, "doOnError: " + throwable.getMessage());
            }
            // 6. 观察者订阅时调用
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                Log.e(TAG, "doOnSubscribe: ");
            }
            // 7. Observable发送事件完毕后调用，无论正常发送完毕 / 异常终止
        }).doAfterTerminate(new Action() {
            @Override
            public void run() throws Exception {
                Log.e(TAG, "doAfterTerminate: ");
            }
            // 8. 最后执行
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                Log.e(TAG, "doFinally: ");
            }
        }).subscribe(integerObserver);
    }

    private void init() {
        findViewById(R.id.tv_learn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RxLearnActivity.this, DemoActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.tv_learn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RxLearnActivity.this, FilterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void countFun() {
        Observable.just(1, 2, 3, 4)
                .count()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.d(TAG, "发送事件的数量：" + aLong);
                    }
                });
    }

    private void startWithFun() {
        //在一个被观察者发送事件前，追加发送一些数据 / 一个新的被观察者
        Observable.just(4, 5, 6)
                .startWith(0)
                .startWithArray(1, 2, 3)
                .subscribe(integerObserver);

        Observable.just(4, 5, 6)
                .startWith(Observable.just(1, 2, 3))
                .subscribe(integerObserver);
    }

    private void collectFun() {
        //将被观察者Observable发送的数据事件收集到一个数据结构里
        Observable.just(1, 2, 3, 4, 5, 6)
                // 1. 创建数据结构（容器），用于收集被观察者发送的数据
                .collect(new Callable<ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> call() throws Exception {
                        return new ArrayList<>();
                    }
                    // 2. 对发送的数据进行收集
                }, new BiConsumer<ArrayList<Integer>, Integer>() {
                    @Override
                    public void accept(ArrayList<Integer> integers, Integer integer) throws Exception {
                        integers.add(integer);
                    }
                }).subscribe(new Consumer<ArrayList<Integer>>() {
            @Override
            public void accept(ArrayList<Integer> integers) throws Exception {
                Log.d(TAG, "本次发送的数据是：" + integers);
            }
        });
    }

    private void reduceFun() {
        //把被观察者需要发送的事件聚合成1个事件 & 发送
        Observable.just(1, 2, 3, 4)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        Log.e(TAG, "本次计算的数据是： " + integer + " 乘 " + integer2);
                        return integer * integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "最终计算结果是：" + integer);
            }
        });
    }

    private void combineLatestFun() {
        //当两个Observables中的任何一个发送了数据后，将先发送了数据的Observables 的最新（最后）一个数据 与 另外一个Observable发送的每个数据结合，最终基于该函数的结果发送数据
        //与Zip（）的区别：Zip（） = 按个数合并，即1对1合并；CombineLatest（） = 按时间合并，即在同一个时间点上合并
        Observable.combineLatest(Observable.just(1L, 2L, 3L),
                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),
                new BiFunction<Long, Long, Long>() {
                    @Override
                    public Long apply(Long aLong, Long aLong2) throws Exception {
                        // aLong = 第1个Observable发送的最新（最后）1个数据
                        // aLong2 = 第2个Observable发送的每1个数据
                        Log.e(TAG, "合并的数据是： " + aLong + " " + aLong2);

                        return aLong + aLong2;
                    }
                }).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "合并的结果：" + aLong);
            }
        });
    }

    private void zipFun() {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "被观察者1发送了事件1");
                e.onNext(1);
                Thread.sleep(1000);

                Log.d(TAG, "被观察者1发送了事件2");
                e.onNext(2);
                Thread.sleep(1000);

                Log.d(TAG, "被观察者1发送了事件3");
                e.onNext(3);
                Thread.sleep(1000);

                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, "被观察者2发送了事件A");
                e.onNext("A");
                Thread.sleep(500);

                Log.d(TAG, "被观察者2发送了事件B");
                e.onNext("B");
                Thread.sleep(500);

                Log.d(TAG, "被观察者2发送了事件C");
                e.onNext("C");
                Thread.sleep(500);

                Log.d(TAG, "被观察者2发送了事件D");
                e.onNext("D");
                Thread.sleep(500);

                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread());
        // 假设不作线程控制，则该两个被观察者会在同一个线程中工作，即发送事件存在先后顺序，而不是同时发送

        Observable.zip(observable2, observable1, new BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer integer) throws Exception {
                return integer + s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "最终接收到的事件 =  " + s);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");

            }
        });
    }

    private void concatErrorFun() {
        //测试结果：第1个被观察者的Error事件将在第2个被观察者发送完事件后再继续发送
        Observable.concatArrayDelayError(Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new NullPointerException());
                e.onComplete();
            }
        }), Observable.just(4, 5, 6))
                .subscribe(integerObserver);
    }

    private void mergeFun() {
        //组合多个被观察者一起发送数据，合并后 按时间线并行执行
        //二者区别：组合被观察者的数量，即merge（）组合被观察者数量≤4个，而mergeArray（）则可＞4个
        //区别上述concat（）操作符：同样是组合多个被观察者一起发送数据，但concat（）操作符合并后是按发送顺序串行执行
        Observable.merge(Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),
                Observable.intervalRange(2, 3, 1, 1, TimeUnit.SECONDS))
                .subscribe(longObserver);
    }

    private void concatFun() {
        //组合多个被观察者一起发送数据，合并后 按发送顺序串行执行
        //二者区别：组合被观察者的数量，即concat（）组合被观察者数量≤4个，而concatArray（）则可＞4个
        Observable.concat(Observable.just(1, 2, 3),
                Observable.just(4, 5, 6))
                .subscribe(integerObserver);

        Observable.concatArray(Observable.just(1, 2, 3),
                Observable.just(4, 5, 6),
                Observable.just(7, 8, 9),
                Observable.just(10, 11, 12),
                Observable.just(13, 14, 15))
                .subscribe(integerObserver);
    }

    private void TwoNetTogeter() {
        GetRequest_Interface request = ApiClient.get(C.BaseURL.JINSHAN_BASE_URL).create
                (GetRequest_Interface.class);

        Observable<Translation1> observable1 = request.getCall1();
        final Observable<Translation2> observable2 = request.getCall2();

        observable1.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Translation1>() {
                    @Override
                    public void accept(Translation1 translation1) throws Exception {
                        Log.d(TAG, "第一次网络请求成功");
                        translation1.show();
                        // 对第1次网络请求返回的结果进行操作 = 显示翻译结果
                    }
                })
                // （新被观察者，同时也是新观察者）切换到IO线程去发起登录请求
                // 特别注意：因为flatMap是对初始被观察者作变换，所以对于旧被观察者，它是新观察者，所以通过observeOn切换线程
                // 但对于初始观察者，它则是新的被观察者
                .observeOn(Schedulers.io())
                .flatMap(new Function<Translation1, ObservableSource<Translation2>>() {
                    @Override
                    public ObservableSource<Translation2> apply(Translation1 translation1) throws Exception {
                        return observable2;
                    }
                })
                // （初始观察者）切换到主线程 处理网络请求2的结果
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Translation2>() {
                    @Override
                    public void accept(Translation2 translation2) throws Exception {
                        Log.d(TAG, "第2次网络请求成功");
                        translation2.show();
                        // 对第2次网络请求返回的结果进行操作 = 显示翻译结果
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "登录失败");
                    }
                });
    }

    private void bufferFun() {
        Observable.just(1, 2, 3, 4, 5)
                .buffer(3, 2)
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        Log.d(TAG, " 缓存区里的事件数量 = " + integers.size());
                        for (Integer value : integers) {
                            Log.d(TAG, " 事件 = " + value);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void flapMapFun() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("我是事件 " + integer + "拆分后的子事件" + i);
                    // 通过flatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                    // 最终合并，再发送给被观察者
                }

                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    private void five() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "使用 Map变换操作符 将事件" + integer + "的参数从 整型" + integer + " 变换成 字符串类型" + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    private void four() {
        Observable.interval(3, 3, TimeUnit.SECONDS)
                //每次发送数字前执行一次网络请求事项轮训的效果
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.d(TAG, "第 " + aLong + " 次轮询");
                        getRequest();

                    }
                }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                //只调用一次
            }

            @Override
            public void onNext(Long aLong) {
                Log.d(TAG, "interval value" + aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "interval对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "interval对Complete事件作出响应");
            }
        });
    }

    private void getRequest() {
        //创建请求接口的实例
        GetRequest_Interface request = ApiClient.get(C.BaseURL.JINSHAN_BASE_URL).create(GetRequest_Interface.class);
        // c. 采用Observable<...>形式 对 网络请求 进行封装
        Observable<Translation> observable = request.getCall();

        observable.subscribeOn(Schedulers.io())// 切换到IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())// 切换回到主线程 处理请求结果
                .subscribe(new Observer<Translation>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Translation translation) {
                        translation.show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "request对Complete事件作出响应");
                    }
                });
    }

    private void initObserver() {
        // 观察者接收事件前，默认最先调用复写 onSubscribe（）
// 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
// 当被观察者生产Error事件& 观察者接收到时，会调用该复写方法 进行响应
// 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
        integerObserver = new Observer<Integer>() {

            // 观察者接收事件前，默认最先调用复写 onSubscribe（）
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "开始采用subscribe连接");
            }

            // 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "对Next事件作出响应" + value);
            }

            // 当被观察者生产Error事件& 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");

            }

            // 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        };

        longObserver = new Observer<Long>() {

            // 观察者接收事件前，默认最先调用复写 onSubscribe（）
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "开始采用subscribe连接");
            }

            // 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onNext(Long value) {
                Log.d(TAG, "对Next事件作出响应" + value);
            }

            // 当被观察者生产Error事件& 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");

            }

            // 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        };
    }

    Integer integer = 10;

    private void three() {
        //延迟创建

        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(integer);
            }
        });

        integer = 15;

        observable.subscribe(integerObserver);

        Observable.timer(2, TimeUnit.SECONDS)
                .subscribe(longObserver);

//        Observable.interval(3, 1, TimeUnit.SECONDS)
//                .subscribe(longObserver);

//        Observable.intervalRange(3, 10, 2, 1, TimeUnit.SECONDS)
//                .subscribe(longObserver);

        Observable.range(3, 10)
                .subscribe(integerObserver);

    }

    private void second() {
        //创建被观察者3种方式
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                // 通过 ObservableEmitter类对象产生事件并通知观察者
                // ObservableEmitter类介绍
                // a. 定义：事件发射器
                // b. 作用：定义需要发送的事件 & 向观察者发送事件
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onComplete();
            }
        });

        Observable observable1 = Observable.just("a", "b", "c");
        String[] words = {"A", "B", "C"};
        Observable observable2 = Observable.fromArray(words);

        //创建观察者2种方式
        Observer<Integer> observer = new Observer<Integer>() {

            // 观察者接收事件前，默认最先调用复写 onSubscribe（）
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "开始采用subscribe连接");
            }

            // 当被观察者生产Next事件 & 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "对Next事件作出响应" + value);
            }

            // 当被观察者生产Error事件& 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");

            }

            // 当被观察者生产Complete事件& 观察者接收到时，会调用该复写方法 进行响应
            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        };

        Subscriber<String> subscriber = new Subscriber<String>() {

            @Override
            public void onSubscribe(Subscription s) {
                Log.d(TAG, "开始采用subscribe连接");

            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "对Next事件作出响应" + s);

            }

            @Override
            public void onError(Throwable t) {
                Log.d(TAG, "对Error事件作出响应");

            }

            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");

            }
        };

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        Observable.fromIterable(list)
                .subscribe(observer);

//        observable.subscribe(observer);
//
//        observable1.subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String o) throws Exception {
//                Log.d(TAG, o);
//            }
//        });
//
//        observable2.subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String o) throws Exception {
//                Log.d(TAG, o);
//            }
//        });

    }

    private void first() {
        Observable.create(new ObservableOnSubscribe<Integer>() {//1. 初始化observable
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.e(TAG, "Observable emit 1" + "\n");
                e.onNext(1);
                Log.e(TAG, "Observable emit 2" + "\n");
                e.onNext(2);
                Log.e(TAG, "Observable emit 3" + "\n");
                e.onNext(3);
                e.onComplete();
                Log.e(TAG, "Observable emit 4" + "\n");
                e.onNext(4);


            }
        }).subscribe(new Observer<Integer>() {//3.订阅
            //2.初始化observer
            private int i;
            private Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
                Log.e(TAG, "onSubscribe: " + d.isDisposed());
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "onNext : value : " + integer + "\n");
                i++;
                if (i == 2) {
                    disposable.dispose();
                    Log.e(TAG, "onNext : isDisposadle : " + disposable.isDisposed() + "\n");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError : value : " + e.getMessage() + "\n");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete" + "\n");
            }
        });
    }
}
