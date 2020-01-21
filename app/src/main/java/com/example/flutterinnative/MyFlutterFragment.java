package com.example.flutterinnative;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import io.flutter.embedding.android.FlutterFragment;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodChannel;

/**
 * @description: 自定义FlutterFragment
 * @author: zhukai
 * @date: 2019/6/3 10:29
 */
public class MyFlutterFragment extends FlutterFragment {

    private static FlutterEngine mFlutterEngine;
    private static final String CHANNEL_NATIVE = "com.example.flutter/native";

    public static MyFlutterFragment newInstance(String initialRoute, FlutterEngine flutterEngine) {
        MyFlutterFragment fragment = new MyFlutterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INITIAL_ROUTE, initialRoute);
        fragment.setArguments(args);
        mFlutterEngine = flutterEngine;
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 这里保证了getView()返回值不为null
        MethodChannel nativeChannel = new MethodChannel(getFlutterEngine().getDartExecutor(), CHANNEL_NATIVE);
        nativeChannel.setMethodCallHandler((methodCall, result) -> {
            switch (methodCall.method) {
                case "goBack":
                    // 返回上一页
                    getActivity().finish();
                    break;
                case "goBackWithResult":
                    // 返回上一页，携带数据
                    Intent backIntent = new Intent();
                    backIntent.putExtra("message", (String) methodCall.argument("message"));
                    getActivity().setResult(Activity.RESULT_OK, backIntent);
                    getActivity().finish();
                    break;
                case "jumpToNative":
                    // 跳转原生页面
                    Intent jumpToNativeIntent = new Intent(getActivity(), NativePageActivity.class);
                    jumpToNativeIntent.putExtra("name", (String) methodCall.argument("name"));
                    startActivityForResult(jumpToNativeIntent, 0);
                    break;
                default:
                    result.notImplemented();
                    break;
            }
        });
    }

    @Nullable
    @Override
    public FlutterEngine provideFlutterEngine(@NonNull Context context) {
        return mFlutterEngine;
    }
}
