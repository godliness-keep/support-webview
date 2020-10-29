package com.longrise.android.webview.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.longrise.android.webview.demo.webdemo.WebDemoActivity;
import com.longrise.android.webview.demo.webdemo.WebDemoFragment;
import com.longrise.android.webview.demo.x5demo.DemoX5Fragment;
import com.longrise.android.webview.demo.x5demo.WebX5DemoActivity;

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

        /*原生 WebView Activity*/
        findViewById(R.id.webview_native).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, WebDemoActivity.class);
                startActivity(intent);
            }
        });

        /*原生 WebView Fragment*/
        findViewById(R.id.webview_native_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, WebDemoFragment.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.webview_x5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, WebX5DemoActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.webview_x5_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, DemoX5Fragment.class);
                startActivity(intent);
            }
        });
    }
}
