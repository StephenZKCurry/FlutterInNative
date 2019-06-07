package com.example.flutterinnative;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

import io.flutter.facade.Flutter;
import io.flutter.facade.FlutterFragment;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

/**
 * @description: Flutter页面
 * @author: zhukai
 * @date: 2019/5/26 14:27
 */
public class FlutterPageActivity extends AppCompatActivity {

    private FlutterView flutterView;

    private static final String CHANNEL_NATIVE = "com.example.flutter/native";
    private static final String CHANNEL_FLUTTER = "com.example.flutter/flutter";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flutter_page);
        // 通过FlutterView引入Flutter编写的页面
        flutterView = Flutter.createView(this, getLifecycle(),
                "route1?{\"name\":\"" + getIntent().getStringExtra("name") + "\"}");
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
//        layout.leftMargin = 100;
//        layout.topMargin = 200;
        addContentView(flutterView, layout);

        // 通过FlutterFragment引入Flutter编写的页面
//        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
//        FlutterFragment flutterFragment = Flutter.createFragment("route1?{\"name\":\"" + getIntent().getStringExtra("name") + "\"}");
//        MyFlutterFragment flutterFragment = MyFlutterFragment.newInstance("route1?{\"name\":\"" + getIntent().getStringExtra("name") + "\"}");
//        tx.replace(R.id.fl_container, flutterFragment);
//        tx.commit();
//        // 这里获取到的flutterView为null
//        flutterView = (FlutterView) flutterFragment.getView();

        MethodChannel nativeChannel = new MethodChannel(flutterView, CHANNEL_NATIVE);
        nativeChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
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
            }
        });
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
                    MethodChannel flutterChannel = new MethodChannel(flutterView, CHANNEL_FLUTTER);
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
        MethodChannel flutterChannel = new MethodChannel(flutterView, CHANNEL_FLUTTER);
        flutterChannel.invokeMethod("goBack", null);
    }
}
