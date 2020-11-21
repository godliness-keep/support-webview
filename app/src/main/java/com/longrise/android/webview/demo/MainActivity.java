package com.longrise.android.webview.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.longrise.android.webview.demo.webdemo.WebDemoActivity;
import com.longrise.android.webview.demo.webdemo.WebDemoFragment;
import com.longrise.android.webview.demo.x5demo.WebX5DemoActivity;

import java.io.File;

/**
 * Created by godliness on 2020/9/1.
 *
 * @author godliness
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101){
            if(resultCode == Activity.RESULT_OK){
                Log.e(TAG, "uri: " + data.getData());
            }
        }
    }

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

//                final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("video/*");
//                startActivity(intent);

//                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
//                startActivityForResult(intent, 101);

//                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
////                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
//                File tempFile = new File(getExternalFilesDir("video"),  System.currentTimeMillis()+".mp4");
//                Uri fileVUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".fileprovider", tempFile);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileVUri);
//                startActivityForResult(intent,101);
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
//                final Intent intent = new Intent(MainActivity.this, DemoX5Fragment.class);
//                startActivity(intent);
            }
        });
    }
}
