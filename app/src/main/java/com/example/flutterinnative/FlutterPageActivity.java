package com.example.flutterinnative;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.android.FlutterFragment;
import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodChannel;

/**
 * @description: Flutter页面
 * @author: zhukai
 * @date: 2019/5/26 14:27
 */
public class FlutterPageActivity extends AppCompatActivity {

    private FlutterEngine flutterEngine;

    private static final String CHANNEL_NATIVE = "com.example.flutter/native";
    private static final String CHANNEL_FLUTTER = "com.example.flutter/flutter";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flutter_page);

        // 创建FlutterEngine对象
        flutterEngine = createFlutterEngine();

        // 通过FlutterView引入Flutter编写的页面
//        FlutterView flutterView = new FlutterView(this);
//        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
////        lp.leftMargin = 100;
////        lp.topMargin = 200;
//        FrameLayout flContainer = findViewById(R.id.fl_container);
//        flContainer.addView(flutterView, lp);
//        flutterView.attachToFlutterEngine(flutterEngine);

        // 通过FlutterFragment引入Flutter编写的页面
//        FlutterFragment flutterFragment = FlutterFragment.createDefault();
//        FlutterFragment flutterFragment = FlutterFragment.withNewEngine()
//                .initialRoute("route1?{\"name\":\"" + getIntent().getStringExtra("name") + "\"}")
//                .build();
        FlutterFragment flutterFragment = FlutterFragment.withCachedEngine("my_engine_id")
                .build();
//        MyFlutterFragment flutterFragment = MyFlutterFragment.newInstance("route1?{\"name\":\"" + getIntent().getStringExtra("name") + "\"}",
//                flutterEngine);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_container, flutterFragment)
                .commit();

        MethodChannel nativeChannel = new MethodChannel(flutterEngine.getDartExecutor(), CHANNEL_NATIVE);
        nativeChannel.setMethodCallHandler((methodCall, result) -> {
            switch (methodCall.method) {
                case "goBack":
                    // 返回上一页
                    finish();
                    break;
                case "goBackWithResult":
                    // 返回上一页，携带数据
                    Intent backIntent = new Intent();
                    backIntent.putExtra("message", (String) methodCall.argument("message"));
                    setResult(RESULT_OK, backIntent);
                    finish();
                    break;
                case "jumpToNative":
                    // 跳转原生页面
                    Intent jumpToNativeIntent = new Intent(FlutterPageActivity.this, NativePageActivity.class);
                    jumpToNativeIntent.putExtra("name", (String) methodCall.argument("name"));
                    startActivityForResult(jumpToNativeIntent, 0);
                    break;
                default:
                    result.notImplemented();
                    break;
            }
        });
    }

    /**
     * 创建可缓存的FlutterEngine
     *
     * @return FlutterEngine对象
     */
    private FlutterEngine createFlutterEngine() {
        // 实例化FlutterEngine对象
        FlutterEngine flutterEngine = new FlutterEngine(this);
        // 设置初始路由
        flutterEngine.getNavigationChannel().setInitialRoute("route1?{\"name\":\"" + getIntent().getStringExtra("name") + "\"}");
        // 开始执行dart代码来pre-warm FlutterEngine
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );
        // 缓存FlutterEngine
        FlutterEngineCache.getInstance().put("my_engine_id", flutterEngine);
        return flutterEngine;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (data != null) {
                    String message = data.getStringExtra("message");
                    Map<String, Object> result = new HashMap<>();
                    result.put("message", message);
                    // 创建MethodChannel，这里的flutterView即Flutter.createView所返回的View
                    MethodChannel flutterChannel = new MethodChannel(flutterEngine.getDartExecutor(), CHANNEL_FLUTTER);
                    flutterChannel.invokeMethod("onActivityResult", result);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        MethodChannel flutterChannel = new MethodChannel(flutterEngine.getDartExecutor(), CHANNEL_FLUTTER);
        flutterChannel.invokeMethod("goBack", null);
    }
}
