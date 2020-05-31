package com.example.flutterinnative;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends AppCompatActivity {

    private EditText etName;

    private FlutterEngine flutterEngine;

    private static final String CHANNEL_NATIVE = "com.example.flutter/native";
    private static final String CHANNEL_FLUTTER = "com.example.flutter/flutter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.et_name);

        Button btnJumpToFlutterUseActivity = findViewById(R.id.btn_jump_to_flutter_use_activity);
        btnJumpToFlutterUseActivity.setOnClickListener(v -> {
            // 创建FlutterEngine对象
            flutterEngine = createFlutterEngine();
            // 创建MethodChannel
            createMethodChannel();
            // 跳转FlutterActivity
            startActivity(
                    FlutterActivity
                            .withCachedEngine("my_engine_id")
                            .build(this)
            );
        });

        Button btnJumpToFlutterUseFragment = findViewById(R.id.btn_jump_to_flutter_use_fragment);
        btnJumpToFlutterUseFragment.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FlutterPageActivity.class);
            intent.putExtra("name", etName.getText().toString());
            startActivityForResult(intent, 0);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Toast.makeText(this, data.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        }
    }

    private FlutterEngine createFlutterEngine() {
        // 实例化FlutterEngine对象
        FlutterEngine flutterEngine = new FlutterEngine(this);
        // 设置初始路由
        flutterEngine.getNavigationChannel().setInitialRoute("route1?{\"name\":\"" + etName.getText().toString() + "\"}");
        // 开始执行dart代码来pre-warm FlutterEngine
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
        );
        // 缓存FlutterEngine
        FlutterEngineCache.getInstance().put("my_engine_id", flutterEngine);
        return flutterEngine;
    }

    private void createMethodChannel() {
        MethodChannel nativeChannel = new MethodChannel(flutterEngine.getDartExecutor(), CHANNEL_NATIVE);
        nativeChannel.setMethodCallHandler((methodCall, result) -> {
            switch (methodCall.method) {
                case "goBackWithResult":
                    // 返回上一页，携带数据
                    ActivityManager.getInstance().finishActivity(FlutterActivity.class);
                    Toast.makeText(this, (String) methodCall.argument("message"), Toast.LENGTH_SHORT).show();
                    break;
                case "jumpToNative":
                    // 跳转原生页面
                    Activity currentActivity = ActivityManager.getInstance().currentActivity();
                    Intent jumpToNativeIntent = new Intent(currentActivity, NativePageActivity.class);
                    jumpToNativeIntent.putExtra("name", (String) methodCall.argument("name"));
                    jumpToNativeIntent.putExtra("isFromFlutterActivity", true);
                    startActivityForResult(jumpToNativeIntent, 1);
                    break;
                default:
                    result.notImplemented();
                    break;
            }
        });
    }

    public void setResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        // 创建MethodChannel
        MethodChannel flutterChannel = new MethodChannel(flutterEngine.getDartExecutor(), CHANNEL_FLUTTER);
        flutterChannel.invokeMethod("onActivityResult", result);
    }
}
