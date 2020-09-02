package com.longrise.android.webview.demo.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.longrise.android.webview.demo.R;
import com.longrise.android.webview.demo.WebX5DemoActivity;

/**
 * Created by godliness on 2020/9/1.
 *
 * @author godliness
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.webview_native).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final Intent intent = new Intent(MainActivity.this, WebDemoActivity.class);
//                startActivity(intent);
                Toast.makeText(MainActivity.this, "暂未提供示例", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.webview_x5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, WebX5DemoActivity.class);
                startActivity(intent);
            }
        });
    }
}
