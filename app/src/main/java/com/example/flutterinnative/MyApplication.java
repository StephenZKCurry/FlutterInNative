package com.example.flutterinnative;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

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

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ActivityManager.getInstance().activities.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                ActivityManager.getInstance().activities.remove(activity);
            }
        });
    }

    public static FlutterEngine getFlutterEngine() {
        return mFlutterEngine;
    }
}
