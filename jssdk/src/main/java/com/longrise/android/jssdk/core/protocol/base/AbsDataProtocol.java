package com.longrise.android.jssdk.core.protocol.base;

import android.webkit.WebView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.longrise.android.jssdk.BuildConfig;
import com.longrise.android.jssdk.Request;
import com.longrise.android.jssdk.gson.JsonHelper;
import com.longrise.android.jssdk.receiver.base.ReceiversManager;

/**
 * Created by godliness on 2020-04-13.
 *
 * @author godliness
 * 协议层
 */
public abstract class AbsDataProtocol {

    @Expose
    @SerializedName("version")
    private int version = BuildConfig.VERSION_CODE;
    @Expose
    @SerializedName("id")
    private int callbackId;
    @Expose
    @SerializedName("eventName")
    private String eventName;

    private transient String javaScriptMethodName;

    public final void setVersion(int version) {
        this.version = version;
    }

    public final int getVersion() {
        return version;
    }

    public final void setCallbackId(int callbackId) {
        this.callbackId = callbackId;
    }

    public final int getCallbackId() {
        return callbackId;
    }

    protected final void setMethodName(String methodName) {
        this.javaScriptMethodName = methodName;
    }

    public final String getMethodName() {
        return javaScriptMethodName;
    }

    public final void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public final String getEventName() {
        return eventName;
    }

    @SuppressWarnings("unchecked")
    public void dispatchReceiver(WebView webView) {
        ReceiversManager.getManager().dispatchReceiver((Request<String>) this, webView);
    }

    public final String toJson() {
        return JsonHelper.toJson(this);
    }

}
