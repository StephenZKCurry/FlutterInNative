package com.example.flutterinnative;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private Button btnJumpToFlutter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.et_name);

        btnJumpToFlutter = findViewById(R.id.btn_jump_to_flutter);
        btnJumpToFlutter.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FlutterPageActivity.class);
            intent.putExtra("name", etName.getText().toString());
            startActivityForResult(intent, 0);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, data.getStringExtra("message"), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
