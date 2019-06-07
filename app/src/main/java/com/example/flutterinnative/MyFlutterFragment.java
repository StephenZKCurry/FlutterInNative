package com.example.flutterinnative;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import io.flutter.facade.FlutterFragment;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

/**
 * @description: 自定义FlutterFragment
 * @author: zhukai
 * @date: 2019/6/3 10:29
 */
public class MyFlutterFragment extends FlutterFragment {

    private static final String CHANNEL_NATIVE = "com.example.flutter/native";

    public static MyFlutterFragment newInstance(String initialRoute) {
        MyFlutterFragment fragment = new MyFlutterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ROUTE, initialRoute);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 这里保证了getView()返回值不为null
        MethodChannel nativeChannel = new MethodChannel((FlutterView) getView(), CHANNEL_NATIVE);
        nativeChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                switch (methodCall.method) {
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
            }
        });
    }
}
