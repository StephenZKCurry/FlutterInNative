package com.example.flutterinnative;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.TextView;

/**
 * @description: Android原生页面
 * @author: zhukai
 * @date: 2019/5/28 14:31
 */
public class NativePageActivity extends AppCompatActivity {

    private TextView tvText;
    private Button btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_page);

        tvText = findViewById(R.id.tv_text);
        // Flutter页面传过来的参数
        tvText.setText("Flutter页面传过来的参数：name=" + getIntent().getStringExtra("name"));

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            if (getIntent().getBooleanExtra("isFromFlutterActivity", false)) {
                // 上一个页面是FlutterActivity
                MainActivity mainActivity = (MainActivity) ActivityManager.getInstance().activities.get(0);
                mainActivity.setResult("我从原生页面回来了");
            } else {
                // 上一个页面是FlutterPageActivity
                Intent intent = new Intent();
                intent.putExtra("message", "我从原生页面回来了");
                setResult(RESULT_OK, intent);
            }
            finish();
        });
    }
}
