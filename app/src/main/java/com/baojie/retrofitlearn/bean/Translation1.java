package com.baojie.retrofitlearn.bean;

import android.util.Log;

/**
 * @Description: TODO
 * @Author baojie@qding.me
 * @Date 2018/8/22 上午9:14
 * @Version TODO
 */

public class Translation1 {
    private int status;

    private content content;
    private static class content {
        private String from;
        private String to;
        private String vendor;
        private String out;
        private int errNo;
    }

    //定义 输出返回数据 的方法
    public void show() {
        Log.d("DemoActivity", content.out);
    }

    public String getOut(){
        return "第一次" + content.out;
    }
}
