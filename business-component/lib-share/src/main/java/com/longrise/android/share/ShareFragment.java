package com.longrise.android.share;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longrise.android.share.onekeyshare.OneKeyShare;

/**
 * Created by godliness on 2020/9/21.
 *
 * @author godliness
 * 分享对话框
 * <p>
 * ShareSDK for Android 集成文档：https://www.mob.com/wiki/detailed?wiki=ShareSDK_Android_Title_ksjc&id=14
 * https://www.mob.com/download?tabs=0&entry=ShareSDK#download_hd
 */
public final class ShareFragment extends DialogFragment implements View.OnClickListener {

    private OneKeyShare.IShareListener mShareListener;
    private String mTitle;
    private String mDesc;
    private String mLink;
    private String mImageUrl;

    public static ShareFragment newInstance() {
        return new ShareFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.moduleshare_fragment_share, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    public void show(FragmentActivity host) {
        this.show(host.getSupportFragmentManager(), "share-ui");
    }

    public ShareFragment params(String title, String desc, String url, String imageUrl) {
        this.mTitle = title;
        this.mDesc = desc;
        this.mLink = url;
        this.mImageUrl = imageUrl;
        return this;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        final OneKeyShare.BaseShare<?> share;
        if (id == R.id.wechat) {
            share = OneKeyShare.wechat(mShareListener).url(mLink);
        } else if (id == R.id.wechat_moment) {
            share = OneKeyShare.wechatMoments(mShareListener).url(mLink);
        } else if (id == R.id.qq) {
            share = OneKeyShare.qq(mShareListener).titleUrl(mLink);
        } else if (id == R.id.qzone) {
            share = OneKeyShare.qZone(mShareListener).titleUrl(mLink);
        } else {
            throw new IllegalStateException("Unknown share type");
        }

        dismiss();
        share.title(mTitle).desc(mDesc).imageUrl(mImageUrl).toShare();
    }

    private void initView() {
        findViewById(R.id.wechat).setOnClickListener(this);
        findViewById(R.id.wechat_moment).setOnClickListener(this);
        findViewById(R.id.qq).setOnClickListener(this);
        findViewById(R.id.qzone).setOnClickListener(this);
    }

    @NonNull
    private <V extends View> V findViewById(@IdRes int id) {
        final View view = getView();
        if (view == null) {
            throw new NullPointerException("getView == null");
        }
        return view.findViewById(id);
    }

    public void setShareCallback(OneKeyShare.IShareListener callback) {
        this.mShareListener = callback;
    }
}
