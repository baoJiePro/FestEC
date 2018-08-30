package com.baojie.retrofitlearn.rxjavalearn.activity;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @Description: TODO
 * @Author baojie@qding.me
 * @Date 2018/8/24 下午2:55
 * @Version TODO
 */

public class FilterActivity extends AppCompatActivity{

    public static String TAG = "FilterActivity";
    private Observer<Integer> observer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        //filterFun();
        //onTypeFun();
        //skipFun();
        //distinctFun();

        //takeFun();

        //throttleFun();

        //throttleWithTimeoutFun();

        //elementFun();

        //allFun();

        //takeWhileFun();

        //skipWhileFun();

        //takeUntilFun();

        //sequenceEqualFun();

        //ambFun();

        //isEmptyFun();

        //beiyaOne();

        //beiyaTwo();

        beiyaThree();



    }

    private void beiyaThree() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "发送事件 1");
                e.onNext(1);
                Log.d(TAG, "发送事件 2");
                e.onNext(2);
                Log.d(TAG, "发送事件 3");
                e.onNext(3);
                Log.d(TAG, "发送事件 4");
                e.onNext(4);
                Log.d(TAG, "发送完成");
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        //s.request(3);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "接收到了事件" + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    private void beiyaTwo() {
        Flowable<Integer> upStream = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR);

        Subscriber<Integer> downStream = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                // 对比Observer传入的Disposable参数，Subscriber此处传入的参数 = Subscription
                // 相同点：Subscription具备Disposable参数的作用，即Disposable.dispose()切断连接, 同样的调用Subscription.cancel()切断连接
                // 不同点：Subscription增加了void request(long n)
                Log.d(TAG, "onSubscribe");
                s.request(3);
                // 作用：决定观察者能够接收多少个事件
                // 如设置了s.request(3)，这就说明观察者能够接收3个事件（多出的事件存放在缓存区）
                // 官方默认推荐使用Long.MAX_VALUE，即s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.w(TAG, "onError: ", t);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };

        upStream.subscribe(downStream);
    }

    private void beiyaOne() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    Log.d(TAG, "发送了事件" + i);
                    Thread.sleep(10);
                    e.onNext(i);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        try {
                            Thread.sleep(5000);
                            Log.d(TAG, "接收到了事件"+ integer);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }
                });
    }

    private void isEmptyFun() {

    }

    private void ambFun() {
        //当需要发送多个 Observable时，只发送 先发送数据的Observable的数据，而其余 Observable则被丢弃.
        List<ObservableSource<Integer>> list = new ArrayList<>();

        list.add(Observable.just(1,2,3).delay(1, TimeUnit.SECONDS));

        list.add(Observable.just(4,5,6));

        Observable.amb(list).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "get things : " + integer);
            }
        });

    }

    private void sequenceEqualFun() {
        Observable.sequenceEqual(Observable.just(1,2,3,4),
                Observable.just(1,2,3,4))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.d(TAG,"2个Observable是否相同："+ aBoolean);
                    }
                });
    }

    private void takeUntilFun() {
        //执行到某个条件时，停止发送事件
        Observable.interval(1, TimeUnit.SECONDS)
                .takeUntil(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        return (aLong > 3);
                    }
                }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {
                Log.d(TAG,"发送了事件 "+ aLong);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void skipWhileFun() {
        Observable.interval(1, TimeUnit.SECONDS)
                .skipWhile(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        return (aLong<5);
                        // 直到判断条件不成立 = false = 发射的数据≥5，才开始发送数据
                    }
                }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void takeWhileFun() {
        //判断发送的每项数据是否满足 设置函数条件,若发送的数据满足该条件，则发送该项数据；否则不发送
        Observable.interval(1, TimeUnit.SECONDS)
                .takeWhile(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        return (aLong < 3);
                    }
                }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {
                Log.d(TAG,"发送了事件 "+ aLong);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void allFun() {
        Observable.just(1,2,3,4,5,6)
                .all(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return (integer <= 10);
                        // 该函数用于判断Observable发送的10个数据是否都满足integer<=10
                    }
                }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                Log.d(TAG, "result is : " + aBoolean);
            }
        });
    }

    private void elementFun() {
        //仅选取第1个元素 / 最后一个元素
        Observable.just(1,2,3,4,5)
                .firstElement()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "first thing is : " + integer);
                    }
                });

        Observable.just(1,2,3,4,5)
                .lastElement()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "last things is : " + integer);
                    }
                });
    }

    private void throttleWithTimeoutFun() {
        //发送数据事件时，若2次发送事件的间隔＜指定时间，就会丢弃前一次的数据，直到指定时间内都没有新数据发射时才会发送后一次的数据
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                Thread.sleep(500);
                e.onNext(2);
                Thread.sleep(1500);
                e.onNext(3);
                Thread.sleep(1500);
                e.onNext(4);
                Thread.sleep(500);
                e.onNext(5);
                Thread.sleep(500);
                e.onNext(6);
                Thread.sleep(1500);
            }
        }).throttleWithTimeout(1, TimeUnit.SECONDS)
                .subscribe(observer);
    }

    private void throttleFun() {
        //在某段时间内，只发送该段时间内第1次事件 / 最后1次事件
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                Thread.sleep(500);
                e.onNext(2);
                Thread.sleep(500);
                e.onNext(3);
                Thread.sleep(500);
                e.onNext(4);
                Thread.sleep(500);
                e.onNext(5);
                Thread.sleep(500);
                e.onNext(6);
                Thread.sleep(500);
                e.onNext(7);
                Thread.sleep(500);
            }
        }).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(observer);
    }

    private void takeFun() {
        //指定观察者最多能接收到的事件数量
        Observable.just(1,2,3,5,4)
                .take(2)
                .subscribe(observer);

        Observable.just(1,2,3,4,5)
                .takeLast(3)
                .subscribe(observer);
    }

    private void distinctFun() {
        //过滤事件序列中重复的事件 / 连续重复的事件
        Observable.just(1,2,3,1,2)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "不重复的元素为: " + integer);
                    }
                });

        // 使用2：过滤事件序列中 连续重复的事件
        // 下面序列中，连续重复的事件 = 3、4
        Observable.just(1,2,3,1,2,3,3,4,4 )
                .distinctUntilChanged()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept( Integer integer) throws Exception {
                        Log.d(TAG,"不连续重复的整型事件元素是： "+ integer);
                    }
                });
    }

    private void skipFun() {
        //跳过某个事件
        Observable.just(1,2,3,4,5)
                .skip(1)
                .skipLast(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "获取到的数据元素：" + integer);
                    }
                });

        // 使用2：根据时间跳过数据项
        // 发送事件特点：发送数据0-5，每隔1s发送一次，每次递增1；第1次发送延迟0s
        Observable.intervalRange(0, 5, 0, 1, TimeUnit.SECONDS)
                .skip(1, TimeUnit.SECONDS) // 跳过第1s发送的数据
                .skipLast(1, TimeUnit.SECONDS) // 跳过最后1s发送的数据
                .subscribe(new Consumer<Long>() {

                    @Override
                    public void accept( Long along ) throws Exception {
                        Log.d(TAG,"获取到的整型事件元素是： "+ along);
                    }
                });

    }

    private void onTypeFun() {
        //过滤 特定数据类型的数据
        Observable.just(1, "a", 2, "c",4)
                .ofType(Integer.class)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "获取到的数据是：" + integer);
                    }
                });
    }

    private void init() {
        observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "开始采用subscribe连接");
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "过滤后得到的事件是："+ integer  );
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        };
    }

    private void filterFun() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onNext(5);
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer > 3;
            }
        }).subscribe(observer);
    }
}
