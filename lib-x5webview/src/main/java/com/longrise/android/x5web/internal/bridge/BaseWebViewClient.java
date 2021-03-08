package com.longrise.android.x5web.internal.bridge;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.longrise.android.x5web.BaseWebActivity;
import com.longrise.android.x5web.BuildConfig;
import com.longrise.android.x5web.internal.IBridgeAgent;
import com.longrise.android.x5web.internal.OnBridgeListener;
import com.longrise.android.x5web.internal.SchemeConsts;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebBackForwardList;
import com.tencent.smtt.sdk.WebHistoryItem;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 * 如果不设置 WebViewClient 则由系统（Activity Manager）处理该 URL
 * 通常是使用浏览器打开或弹出选择对话框
 */
public abstract class BaseWebViewClient<T extends IBridgeAgent<T>> extends WebViewClient {

    private static final String TAG = "BaseWebViewClient";

    private Handler mHandler;
    private WeakReference<T> mTarget;
    private OnBridgeListener mClientBridge;

    private boolean mBlockImageLoad;
    private boolean mFirstFinished = true;

    /**
     * 是否优先加载 DOM 资源，暂时阻塞图片加载，有助于提升页面首次加载的响应时间
     */
    protected boolean isPriorityResourceLoad() {
        return false;
    }

    /**
     * 是否拦截 url 加载 {@link #shouldOverrideUrlLoading(WebView, String)}
     * <p>
     * WebView.getHitTestResult() 返回 HitTestResult，一般会根据打开的链接的类型
     * 返回对应的 extra 信息，如果打开链接不是一个 url (标准)，或者打开的链接是 JavaScript 的 url
     * 它的类型是 UNKNOWN_TYPE，这个 url 就会通过 requestFocusNodeHref(Message)异步重定向。
     * 返回的 extra 为 null，或者没有返回 extra。根据此方法的返回值，判断是否为 null，可以用于解决
     * 网页重定向问题。
     * <p>
     * final WebView.HitTestResult result = view.getHitTestResult();
     * if(result == null || result.getExtra() != null){
     * <p>
     * }
     * 关于页面打开类型可以参考 {@see https://blog.csdn.net/qq_33689414/article/details/51111541}
     */
    protected boolean shouldInterceptUrlLoading(WebView view, String url) {
        return false;
    }

    /**
     * 获取当前所依附的 Activity {?} extends {@link BaseWebActivity}
     */
    @SuppressWarnings("unchecked")
    @Nullable
    protected final T getTarget() {
        return (T) mTarget.get();
    }

    /**
     * 判断当前所依附的 Activity 是否已经 Finished
     */
    protected final boolean isFinished() {
        final T target = getTarget();
        return target == null || target.isFinishing();
    }

    /**
     * 获取 Handler 实例
     * <p>
     * 每个 Web 页面实例都有且共用一个 Handler 实例
     * 即在 Activity、WebChromeClient、WebViewClient、FileChooser、Bridge 之间
     * 发送的消息可以相互接收到{@link #onHandleMessage(Message)}
     */
    protected final Handler getHandler() {
        return mHandler;
    }

    /**
     * 拦截通过 Handler 发送的消息 {@link #getHandler()}
     */
    public boolean onHandleMessage(Message msg) {
        return false;
    }

    /**
     * 通过 Handler 发送任务
     */
    protected final void post(Runnable action) {
        postDelayed(action, 0);
    }

    /**
     * 通过 Handler 发送一个 Delay 任务
     */
    protected final void postDelayed(Runnable action, int delay) {
        if (!isFinished()) {
            mHandler.postDelayed(action, delay);
        }
    }

    /**
     * 如果需要重写该方法请 {@link #shouldInterceptUrlLoading}
     * <p>
     * 该方法的规则较为繁琐，简单整理如下：
     * 1、通过 loadUrl 该方法不会被回调
     * 2、WebView 的前进、后退、刷新、以及 POST 请求都不会回调
     * 3、重定向不会回调（例如：window.location.href）
     * <p>
     * 4、只有页面中超链接才会回调该方法
     */
    @Override
    public final boolean shouldOverrideUrlLoading(WebView webView, String url) {
        // 1、若没有设置 WebViewClient 则由系统（Activity Manager）处理该 URL，通常是使用浏览器打开或弹出浏览器选择对话框
        // 2、若设置 WebViewClient 且该方法返回 true，则由应用的代码处理该 URL，WebView 不处理（即开发人员自行处理）
        // 3、若设置 WebViewClient 且该方法返回 false，则由 WebView 处理该 URL，即 WebView.loadUrl()
        return canInterceptUrlLoading(webView, url);
    }

    /**
     * 5.0 之后执行该方法
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public final boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
        // 5.0 后会执行该方法，系统会兼容旧版本的回调
        // 故这里直接 super 即可并 final
        return super.shouldOverrideUrlLoading(webView, webResourceRequest);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (isFinished()) {
            return;
        }

        if (mClientBridge != null) {
            mClientBridge.onPageStarted();
        }

        if (isPriorityResourceLoad()) {
            blockImageLoad(view);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (isFinished()) {
            return;
        }

        if (mClientBridge != null) {
            mClientBridge.onPageFinished();
        }

        if (mFirstFinished) {
            clearForwardHistory(view);
            mFirstFinished = false;
        }

        if (isPriorityResourceLoad()) {
            toImageLoad(view);
        }
    }

    /**
     * File not found, no network connection, server not found for the main resource
     * 当加载页面出现错误时，回调该方法。这里是展示加载错误最合适的位置，然后一刀切的展示错误页面的话，很有可能带来误判，
     * 给用户造成经常加载页面失败的错误。由于不同的 WebView 实现可能不一样，所以需要排除几种可能出错的场景，详情见{@link #interceptErrorState(WebView, String, int)}
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//        if (interceptErrorState(view, failingUrl, errorCode)) {
//            return;
//        }
//        if (mClientBridge != null) {
//            mClientBridge.onReceivedError();
//        }

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "errorCode: " + errorCode + " description: " + description);
        }
    }

    /**
     * File not found, no network connection, server not found for the main resource
     * 当加载页面出现错误时，回调该方法。这里是展示加载错误最合适的位置，然后一刀切的展示错误页面的话，很有可能带来误判，
     * * 给用户造成经常加载页面失败的错误。由于不同的 WebView 实现可能不一样，所以需要排除几种可能出错的场景，详情见{@link #interceptErrorState(WebView, String, int)}
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        // Android WebView 默认的错误页面比较简陋，如果替换默认的错误页面，通常的做法是页面加载出错的情况下，使用自定义的错误
        // 页面覆盖在 WebView 上面、或者使用 javaScript 操作 DOM 修改页面。使用这个方法的前提是能够准确的知道页面加载失败，
        // 遗憾的是 WebView、WebViewClient、WebChromeClient 都没有提供这个方法。

        // 常见的处理方式是在 onReceivedError，但是问题在于该方法在发生 HTTP 错误的情况下就会触发，比如页面内的某个资源加载失败
        // 但这时页面已经加载成功。而且 errorCode 和 HTTP 错误并不相同，errorCode 和 WebView 内核的实现是相关的。想通过 errorCode
        // 来区分错误也很困难。采用这个方法经常发生某些页面始终加载不出来的情况，这个办法实际的体验和可行性并不高！！！

//        final int errorCode = error.getErrorCode();
//        final String failingUrl = request.getUrl().toString();
//        if (interceptErrorState(view, failingUrl, errorCode)) {
//            return;
//        }
//        if (mClientBridge != null) {
//            mClientBridge.onReceivedError();
//        }

        if (BuildConfig.DEBUG) {
            final int errorCode = error.getErrorCode();
            final String desc = error.getDescription().toString();
            Log.e(TAG, "errorCode: " + errorCode + " description: " + desc);
        }
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        // WebView 中所有的资源加载都会走这里
        super.onLoadResource(view, url);
    }

    /**
     * Api Level 23 Capture http error
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        // 任何 HTTP 请求产生的错误都会回调这个方法，包括主页面的html文档请求，iframe、图片等资源请求。
        // 在这个回调中，由于混杂了很多请求，不适合用来展示加载错误的页面，而适合做监控报警。
        // 当某个 URL，或者某个资源收到大量报警时，说明页面或资源可能存在问题，这时候可以让相关运营及时响应修改。
    }

    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler handler, SslError sslError) {
        if (handler != null) {
            handler.proceed();
        }
//        if (UrlUtils.isHost(getUrl())) {
//            handler.proceed();
//        } else {
//            super.onReceivedSslError(view, handler, error);
//        }
    }

    public final void invokeClientBridge(OnBridgeListener agent) {
        this.mClientBridge = agent;
    }

    public final void attachTarget(T target, WebView view) {
        view.setWebViewClient(this);
        this.mHandler = target.getHandler();
        this.mTarget = new WeakReference<>(target);
    }

    private void blockImageLoad(WebView view) {
        if (!mBlockImageLoad) {
            final WebSettings settings = view.getSettings();
            if (settings != null) {
                settings.setBlockNetworkImage(true);
            }
            mBlockImageLoad = true;
        }
    }

    private void toImageLoad(WebView view) {
        if (mBlockImageLoad) {
            final WebSettings settings = view.getSettings();
            if (settings != null) {
                settings.setBlockNetworkImage(false);
            }
            mBlockImageLoad = false;
        }
    }

    /**
     * 出错条件排除：
     * 1、加载失败的url跟WebView里的url不是同一个url，排除；
     * 2、errorCode=-1，表明是ERROR_UNKNOWN的错误，为了保证不误判，排除；（这里仅排除联网状态下的 -1，因测试发现在华为ROM有误判）
     * 3、failingUrl=null&errorCode=-12，由于错误的url是空而不是ERROR_BAD_URL，排除。
     * <p>
     * 关于 errorCode 的列举参考这里 {@see https://blog.csdn.net/weixin_34413065/article/details/85959652}
     **/
    private boolean interceptErrorState(WebView view, String failingUrl, int errorCode) {
        final String originalUrl = view.getOriginalUrl();
        // X5WebView 这里有一个漏洞
        // 首次加载（WebView 首次创建）页面如果出错 originalUrl 为 null，这里与原生 WebView 有区别
        final boolean isErrorUrl = originalUrl != null && !TextUtils.equals(failingUrl, view.getUrl()) && !TextUtils.equals(failingUrl, originalUrl);
        // -1 ERROR_UNKNOWN，为了保证不误判，排除
        // -12 EventHandle.ERROR_BAD_URL
        // 在华为 ROM 出现网络关闭后为 -1 的场景，增加网络是否连接的状态判断：仅拦截联网（不表示有网）状态下的 -1 状态
        final boolean error = (failingUrl == null && errorCode != -12);
        return isErrorUrl || error || errorCode == -1;
    }

    /**
     * 是否是有效的 scheme，如果是自定义动作 {@link #beforeUrlLoading(String)}
     */
    private boolean isEffectiveScheme(String url) {
        return url.startsWith(SchemeConsts.HTTP)
                || url.startsWith(SchemeConsts.HTTPS)
                || url.startsWith(SchemeConsts.FILE);
    }

    private boolean interceptAddressType(String url) {
        return url.endsWith(".apk")
                || url.endsWith(".css")
                || url.endsWith(".png")
                || url.endsWith(".jpg")
                || url.endsWith(".gif")
                || url.endsWith(".js");
    }

    private void clearForwardHistory(WebView view) {
        final WebBackForwardList forwardList = view.copyBackForwardList();
        if (forwardList != null && forwardList.getSize() > 1) {
            final WebHistoryItem item = forwardList.getItemAtIndex(0);
            if (item != null && TextUtils.equals(item.getUrl(), SchemeConsts.BLANK)) {
                view.clearHistory();
            }
        }
    }

    private boolean canInterceptUrlLoading(WebView view, String url) {
        if (isFinished()) {
            return true;
        }
        return isInterceptUrlLoading(view, url);
    }

    private boolean isInterceptUrlLoading(WebView view, String url) {
        final boolean intercept = beforeUrlLoading(url);
        if (intercept || TextUtils.isEmpty(url) || !isEffectiveScheme(url)) {
            // Unable to process Url address
            return true;
        }
        return shouldInterceptUrlLoading(view, url);
    }

    private boolean beforeUrlLoading(String url) {
        if (mClientBridge != null) {
            // 给外部一次拦截的机会
            // 可以重写 BaseWebActivity的shouldOverrideUrlLoading
            return mClientBridge.beforeUrlLoading(url);
        }
        return false;
    }
}
