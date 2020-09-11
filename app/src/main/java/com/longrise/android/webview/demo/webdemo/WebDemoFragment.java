package com.longrise.android.webview.demo.webdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.longrise.android.jssdk.receiver.IParamsReceiver;
import com.longrise.android.jssdk.receiver.base.EventName;
import com.longrise.android.web.BaseWebFragment;
import com.longrise.android.web.internal.BaseWebView;
import com.longrise.android.webview.demo.R;
import com.longrise.android.webview.demo.mode.Bean;
import com.longrise.android.webview.demo.mode.Params;

/**
 * Created by godliness on 2020/9/11.
 *
 * @author godliness
 * <p>
 * 测试 BaseWebFragment
 */
public final class WebDemoFragment extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(Window.ID_ANDROID_CONTENT, new DemoWebFragment(), "Web-Fragment").commit();
    }

    public static final class DemoWebFragment extends BaseWebFragment<DemoWebFragment> {

        private static final String TAG = DemoWebFragment.class.getSimpleName();

        private BaseWebView mWebView;

        @Override
        protected int getLayoutResourceId(@Nullable Bundle state) {
            return R.layout.fragment_web;
        }

        @Override
        protected void initView() {
            mWebView = findViewById(R.id.webview);

            loadUrl("file:///android_asset/main.html");

            mParamsReceiver.alive().lifecycle(this);
        }

        @Override
        protected void regEvent(boolean regEvent) {

        }

        @Override
        public BaseWebView getWebView() {
            return mWebView;
        }

        @Override
        public void onReceivedTitle(String title) {

        }

        @Override
        public void onProgressChanged(int newProgress) {

        }

        @Override
        public void loadSucceed() {

        }

        @Override
        public void loadFailed() {

        }

        /**
         * 含有参数的接收者
         */
        private final IParamsReceiver<Params> mParamsReceiver = new IParamsReceiver<Params>() {
            @Override
            @EventName("shareFeiji")
            public void onEvent(Params params) {
                Log.e(TAG, "接收到JS传递参数：" + params);

                final Bean[] beans = new Bean[1];
                for (int i = 0; i < 1; i++) {
                    final Bean bean = new Bean();
                    bean.like = "心，是一个容器，不停地累积，关于你的点点滴滴";
                    bean.sex = "boy " + i;
                    beans[i] = bean;
                }

                // 如果需要返回，可以选择使用
                callback(beans);
            }
        };
    }
}
