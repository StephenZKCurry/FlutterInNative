package com.example.flutterinnative;

import android.app.Application;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;

/**
 * desc   : 全局Application
 * author : zhukai
 * date   : 2020/4/5
 */
public class MyApplication extends Application {

    private static FlutterEngine mFlutterEngine;

    @Override
    public void onCreate() {
        super.onCreate();
        // 实例化FlutterEngine对象
        mFlutterEngine = new FlutterEngine(this);
        // 设置初始路由
        mFlutterEngine.getNavigationChannel().setInitialRoute("route1");
        // 开始执行dart代码来pre-warm FlutterEngine
        mFlutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );
        // 缓存FlutterEngine
        FlutterEngineCache
                .getInstance()
                .put("my_engine_id", mFlutterEngine);
    }

    public static FlutterEngine getFlutterEngine() {
        return mFlutterEngine;
    }
}
