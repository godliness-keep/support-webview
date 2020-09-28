package com.longrise.android.share.onekeyshare;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.longrise.android.share.R;
import com.longrise.android.share.ShareFragment;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by godliness on 2020/9/21.
 *
 * @author godliness
 * 快速构建一键分享
 */
public final class OneKeyShare {

    public interface IShareListener {

        /**
         * 分享完成
         */
        void onComplete();

        /**
         * 分享失败
         */
        void onError();

        /**
         * 取消分享
         */
        void onCancel();
    }

    public interface IShareParamsListener {

        /**
         * 自定义分享参数
         *
         * @param params {@link Platform.ShareParams}
         */
        void linkParams(Platform.ShareParams params);
    }

    /**
     * 构建分享 UI
     */
    public static ShareFragment ui(IShareListener callback) {
        final ShareFragment fragment = ShareFragment.newInstance();
        fragment.setShareCallback(callback);
        return fragment;
    }

    /**
     * 构建微信分享
     */
    public static WechatShare wechat(IShareListener callback) {
        return new WechatShare(callback);
    }

    /**
     * 构建微信朋友圈分享
     */
    public static WechatMomentsShare wechatMoments(IShareListener callback) {
        return new WechatMomentsShare(callback);
    }

    /**
     * 构建QQ分享
     */
    public static QQShare qq(IShareListener callback) {
        return new QQShare(callback);
    }

    /**
     * 构建QQ空间分享
     */
    public static QZoneShare qZone(IShareListener callback) {
        return new QZoneShare(callback);
    }

    public static class WechatShare extends BaseShare<WechatShare> {

        private String mUrl;

        private WechatShare(IShareListener callback) {
            super(callback);
        }

        public WechatShare url(String url) {
            this.mUrl = url;
            return this;
        }

        @Override
        Platform createPlatform() {
            return ShareSDK.getPlatform(Wechat.NAME);
        }

        @Override
        void linkParams(Platform.ShareParams params) {
            params.setUrl(mUrl);
        }
    }

    public static class WechatMomentsShare extends BaseShare<WechatMomentsShare> {

        private String mUrl;

        private WechatMomentsShare(IShareListener callback) {
            super(callback);
        }

        public WechatMomentsShare url(String url) {
            this.mUrl = url;
            return this;
        }

        @Override
        Platform createPlatform() {
            return ShareSDK.getPlatform(WechatMoments.NAME);
        }

        @Override
        void linkParams(Platform.ShareParams params) {
            params.setUrl(mUrl);
        }
    }

    public static class QQShare extends BaseShare<QQShare> {

        private String mTitleUrl;

        private QQShare(IShareListener callback) {
            super(callback);
        }

        public QQShare titleUrl(String titleUrl) {
            this.mTitleUrl = titleUrl;
            return this;
        }

        @Override
        Platform createPlatform() {
            return ShareSDK.getPlatform(QQ.NAME);
        }

        @Override
        void linkParams(Platform.ShareParams params) {
            params.setTitleUrl(mTitleUrl);
        }
    }

    public static class QZoneShare extends BaseShare<QZoneShare> {

        private String mTitleUrl;

        private String mSiteUrl;

        private QZoneShare(IShareListener callback) {
            super(callback);
        }

        public QZoneShare titleUrl(String titleUrl) {
            this.mTitleUrl = titleUrl;
            return this;
        }

        public QZoneShare siteUrl(String siteUrl) {
            this.mSiteUrl = siteUrl;
            return this;
        }

        @Override
        Platform createPlatform() {
            return ShareSDK.getPlatform(QZone.NAME);
        }

        @Override
        void linkParams(Platform.ShareParams params) {
            params.setTitleUrl(mTitleUrl);
            params.setSiteUrl(mSiteUrl);
        }
    }

    @SuppressWarnings("unchecked")
    public static abstract class BaseShare<T extends BaseShare<T>> {

        private final IShareListener mCallback;

        private String mTitle;
        private String mDesc;
        private String mImageUrl;
        private String mImagePath;
        private int mShareType = Platform.SHARE_WEBPAGE;
        private String mTypeUrl;

        private final Platform.ShareParams mParams;

        /**
         * 创建分享平台
         *
         * @return {@link Platform}
         */
        abstract Platform createPlatform();

        /**
         * 拼装分享参数
         *
         * @param params {@link Platform.ShareParams}
         */
        abstract void linkParams(Platform.ShareParams params);

        /**
         * 分享标题
         */
        public T title(String title) {
            this.mTitle = title;
            return (T) this;
        }

        /**
         * 分享描述（所有平台都需要这个字段）
         */
        public T desc(String text) {
            this.mDesc = text;
            return (T) this;
        }

        /**
         * 分享图标
         */
        public T imageUrl(String imageUrl) {
            this.mImageUrl = imageUrl;
            return (T) this;
        }

        public T imagePath(String path) {
            this.mImagePath = path;
            return (T) this;
        }

        /**
         * 分享类型
         */
        public T shareType(int shareType) {
            this.mShareType = shareType;
            return (T) this;
        }

        /**
         * 分享类型地址
         */
        public T typeUrl(String url) {
            this.mTypeUrl = url;
            return (T) this;
        }

        /**
         * 执行分享
         */
        public final void toShare() {
            final Platform.ShareParams params = this.mParams;
            params.setText(mDesc);
            params.setImageUrl(mImageUrl);
            params.setImagePath(mImagePath);
            params.setShareType(mShareType);
            if (mShareType == Platform.SHARE_MUSIC) {
                params.setMusicUrl(mTypeUrl);
            } else if (mShareType == Platform.SHARE_VIDEO) {
                params.setVideoUri(Uri.parse(mTypeUrl));
            }
            params.setTitle(mTitle);
            linkParams(params);
            toShare(null);
        }

        /**
         * 执行分享，自定义任意分享参数
         */
        public final void toShare(IShareParamsListener paramsListener) {
            final Platform.ShareParams params = this.mParams;
            if (paramsListener != null) {
                paramsListener.linkParams(params);
            }
            final Platform platform = createPlatform();
            platform.setPlatformActionListener(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    mCallback.onComplete();
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    mCallback.onError();
                }

                @Override
                public void onCancel(Platform platform, int i) {
                    mCallback.onCancel();
                }
            });
            platform.share(params);
        }

        public final int convertToShareType(String type) {
            if (TextUtils.isEmpty(type)) {
                return Platform.SHARE_WEBPAGE;
            }
            switch (type) {
                case "music":
                    return Platform.SHARE_MUSIC;

                case "video":
                    return Platform.SHARE_VIDEO;

                default:
                    return Platform.SHARE_WEBPAGE;
            }
        }

        BaseShare(IShareListener callback) {
            this.mCallback = callback;
            this.mParams = new Platform.ShareParams();
        }
    }
}
