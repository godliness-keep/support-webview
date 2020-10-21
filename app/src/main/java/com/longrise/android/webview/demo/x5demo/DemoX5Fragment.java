package com.longrise.android.webview.demo.x5demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.longrise.android.jssdk_x5.receiver.IParamsReceiver;
import com.longrise.android.jssdk_x5.receiver.base.EventName;
import com.longrise.android.webview.demo.R;
import com.longrise.android.webview.demo.mode.Bean;
import com.longrise.android.webview.demo.mode.Params;
import com.longrise.android.x5web.BaseWebFragment;
import com.longrise.android.x5web.internal.X5WebView;

/**
 * Created by godliness on 2020/9/12.
 *
 * @author godliness
 */
public final class DemoX5Fragment extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(Window.ID_ANDROID_CONTENT, new X5Fragment(), "x5-fragment").commit();
    }

    public static class X5Fragment extends BaseWebFragment<X5Fragment> {

        private static final String TAG = "X5Fragment";

        private X5WebView mWebView;

        /**
         * Returns the current layout resource id
         *
         * @param state {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
         * @return The current layout id
         */
        @Override
        protected int getLayoutResourceId(@Nullable Bundle state) {
            return R.layout.fragment_x5;
        }

        /**
         * Here the {@link #findViewById(int)}
         */
        @Override
        protected void initView() {
            mWebView = findViewById(R.id.webview);

            loadUrl("file:///android_asset/first.html");

            mParamsReceiver.alive().lifecycle(this);

        }

        /**
         * Register event or unregister
         *
         * @param regEvent Current state
         */
        @Override
        protected void regEvent(boolean regEvent) {

        }

        /**
         * Returns the current WebView instance
         *
         * @return {@link X5WebView}
         */
        @Override
        public X5WebView getWebView() {
            return mWebView;
        }

        @Override
        public boolean isFinishing() {
            return false;
        }

        /**
         * Current web page title
         *
         * @param title String
         */
        @Override
        public void onReceivedTitle(String title) {

        }

        /**
         * Current page loading progress
         *
         * @param newProgress progress
         */
        @Override
        public void onProgressChanged(int newProgress) {

        }

        /**
         * Loading the page
         */
        @Override
        public void loadSucceed() {

        }

        /**
         * Page loading failed
         */
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
