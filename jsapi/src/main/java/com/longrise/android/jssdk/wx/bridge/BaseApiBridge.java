package com.longrise.android.jssdk.wx.bridge;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.longrise.android.jssdk.Request;
import com.longrise.android.jssdk.Response;
import com.longrise.android.jssdk.core.bridge.BaseBridge;
import com.longrise.android.jssdk.wx.BridgeApi;
import com.longrise.android.jssdk.wx.PermissionConst;
import com.longrise.android.jssdk.wx.R;
import com.longrise.android.jssdk.wx.mode.ChooseImage;
import com.longrise.android.jssdk.wx.mode.CropImage;
import com.longrise.android.jssdk.wx.mode.GetLocation;
import com.longrise.android.jssdk.wx.mode.LocationFailed;
import com.longrise.android.jssdk.wx.mode.LocationResult;
import com.longrise.android.jssdk.wx.mode.PreviewImage;
import com.longrise.android.jssdk.wx.mode.QrCode;
import com.longrise.android.jssdk.wx.mode.ShareTo;
import com.longrise.android.jssdk.wx.utils.NetUtil;
import com.longrise.android.jssdk.wx.utils.ResUtil;
import com.longrise.android.location.ILocationListener;
import com.longrise.android.location.LocationManager;
import com.longrise.android.location.mode.LocationParams;
import com.longrise.android.permission.IPermissionHelper;
import com.longrise.android.permission.RequestPermission;
import com.longrise.android.photowall.Album;
import com.longrise.android.qr.scan.IScanResultCallback;
import com.longrise.android.qr.scan.QrScan;
import com.longrise.android.share.onekeyshare.OneKeyShare;

/**
 * Created by godliness on 2020-04-16.
 *
 * @author godliness
 * 兼容Wechat API
 */
@SuppressWarnings("unused")
public abstract class BaseApiBridge<T> extends BaseBridge<T> {

    private static final String TAG = "BaseApiBridge";

    @Override
    protected abstract WebView getWebView();

    protected abstract void post(Runnable task);

    protected abstract void postDelayed(Runnable task, int delayMillis);

    /**
     * 权限提示，重写该方法实现自定义权限提示
     *
     * @param what {@link com.longrise.android.jssdk.wx.PermissionConst} 权限类型
     * @param desc 默认权限提示
     */
    protected void onShowPermissionRationale(int what, String desc) {
        Toast.makeText(BridgeApi.get(), desc, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public final void closeWindow() {
        final Activity t = getActivity();
        t.finish();
    }

    @JavascriptInterface
    public final void showShareUI(String message) {
        final Request<ShareTo> request = Request.parseRequest(message, ShareTo.class);
        final ShareTo shareTo = request.getParams();

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                OneKeyShare.ui(new OneKeyShare.IShareListener() {
                    @Override
                    public void onComplete() {
                        Response.create(request.getCallbackId()).result(true)
                                .notify(getWebView());
                    }

                    @Override
                    public void onError() {
                        Response.create(request.getCallbackId()).result(false).notify(getWebView());
                    }

                    @Override
                    public void onCancel() {
                        Response.create(request.getCallbackId()).state(0).notify(getWebView());
                    }
                }).params(shareTo.title, shareTo.desc, shareTo.link, shareTo.imgUrl).show(getActivity());
            }
        };
        post(task);
    }

    @JavascriptInterface
    public final void shareToWechat(String message) {
        final Request<ShareTo> request = Request.parseRequest(message, ShareTo.class);
        final ShareTo shareTo = request.getParams();

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                final OneKeyShare.WechatShare wechatShare = OneKeyShare.wechat(new OneKeyShare.IShareListener() {
                    @Override
                    public void onComplete() {
                        Response.create(request.getCallbackId()).result(true).notify(getWebView());
                    }

                    @Override
                    public void onError() {
                        Response.create(request.getCallbackId()).result(false).notify(getWebView());
                    }

                    @Override
                    public void onCancel() {
                        Response.create(request.getCallbackId()).state(0).notify(getWebView());
                    }
                });
                final int type = wechatShare.convertToShareType(shareTo.type);
                wechatShare.title(shareTo.title).desc(shareTo.desc).url(shareTo.link).imageUrl(shareTo.imgUrl).shareType(type).typeUrl(shareTo.dataUrl).toShare();
            }
        };
        post(task);
    }

    @JavascriptInterface
    public final void shareToWechatMoment(String message) {
        final Request<ShareTo> request = Request.parseRequest(message, ShareTo.class);
        final ShareTo shareTo = request.getParams();

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                OneKeyShare.wechatMoments(new OneKeyShare.IShareListener() {
                    @Override
                    public void onComplete() {
                        Response.create(request.getCallbackId()).result(true).notify(getWebView());
                    }

                    @Override
                    public void onError() {
                        Response.create(request.getCallbackId()).result(false).notify(getWebView());
                    }

                    @Override
                    public void onCancel() {
                        Response.create(request.getCallbackId()).state(0).notify(getWebView());
                    }
                }).title(shareTo.title)
                        .url(shareTo.link)
                        .imageUrl(shareTo.imgUrl)
                        .toShare();
            }
        };
        post(task);
    }

    @JavascriptInterface
    public final void shareToQQ(String message) {
        final Request<ShareTo> request = Request.parseRequest(message, ShareTo.class);
        final ShareTo shareTo = request.getParams();

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                OneKeyShare.qq(new OneKeyShare.IShareListener() {
                    @Override
                    public void onComplete() {
                        Response.create(request.getCallbackId()).result(true).notify(getWebView());
                    }

                    @Override
                    public void onError() {
                        Response.create(request.getCallbackId()).result(false).notify(getWebView());
                    }

                    @Override
                    public void onCancel() {
                        Response.create(request.getCallbackId()).state(0).notify(getWebView());
                    }
                }).title(shareTo.title)
                        .desc(shareTo.desc)
                        .titleUrl(shareTo.link)
                        .imageUrl(shareTo.imgUrl).toShare();
            }
        };
        post(task);
    }

    @JavascriptInterface
    public final void shareToQZone(String message) {
        final Request<ShareTo> request = Request.parseRequest(message, ShareTo.class);
        final ShareTo shareTo = request.getParams();

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                OneKeyShare.qZone(new OneKeyShare.IShareListener() {
                    @Override
                    public void onComplete() {
                        Response.create(request.getCallbackId()).result(true).notify(getWebView());
                    }

                    @Override
                    public void onError() {
                        Response.create(request.getCallbackId()).result(false).notify(getWebView());
                    }

                    @Override
                    public void onCancel() {
                        Response.create(request.getCallbackId()).state(0).notify(getWebView());
                    }
                }).title(shareTo.title)
                        .desc(shareTo.desc)
                        .titleUrl(shareTo.link)
                        .imageUrl(shareTo.imgUrl).toShare();
            }
        };
        post(task);
    }

    /**
     * 系统相册
     */
    @JavascriptInterface
    public final void startGallery(String message) {
        final Request<CropImage> request = Request.parseRequest(message, CropImage.class);
        final CropImage crop = request.getParams();

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                Album.galleryOf(new Album.IGalleryListener() {
                    @Override
                    public void onSelected(@Nullable Uri uri) {
                        final int id = request.getCallbackId();
                        if (uri == null) {
                            Response.create(id).state(0).desc(ResUtil.getString(R.string.gallery_failed_to_get)).notify(getWebView());
                            return;
                        }
                        if (crop.crop) {
                            cropOf(id, uri, crop);
                        } else {
                            Response.create(id).result(uri.toString()).notify(getWebView());
                        }
                    }
                }).start(getActivity());
            }
        };
        post(task);
    }

    /**
     * 系统拍照
     */
    @JavascriptInterface
    public final void takePicture(String message) {
        final Request<CropImage> request = Request.parseRequest(message, CropImage.class);
        final CropImage picture = request.getParams();

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                RequestPermission.of(getActivity())
                        .onPermissionResult(new IPermissionHelper() {
                            @Override
                            protected void onResult(boolean isGranted) {
                                if (!isGranted) {
                                    return;
                                }
                                Album.takeOf(new Album.ITakeListener() {
                                    @Override
                                    public void onTaken(@Nullable Uri uri) {
                                        final int id = request.getCallbackId();
                                        if (uri == null) {
                                            Response.create(id).state(0).desc(ResUtil.getString(R.string.take_failed_to_get)).notify(getWebView());
                                            return;
                                        }
                                        if (picture.crop) {
                                            cropOf(id, uri, picture);
                                        } else {
                                            Response.create(id).result(uri.toString()).notify(getWebView());
                                        }
                                    }
                                }).start(getActivity());
                            }

                            @Override
                            protected void onManuallyRationale() {
                                final String title = ResUtil.getPermissionTitle(R.string.string_permission_camera);
                                final String desc = ResUtil.getPermissionSettingDesc(R.string.string_permission_camera);
                                showManuallyOnDialog(title, desc);
                            }
                        }).request(Manifest.permission.CAMERA);
            }
        };
        post(task);
    }

    /**
     * 图片剪裁
     */
    @JavascriptInterface
    public final void startCrop(String message) {
        final Request<CropImage> request = Request.parseRequest(message, CropImage.class);
        final CropImage crop = request.getParams();
        final Uri src = crop.getSource();
        if (src == null || src.getScheme() == null) {
            return;
        }

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                cropOf(request.getCallbackId(), src, crop);
            }
        };
        post(task);
    }

    private void cropOf(final int id, @NonNull Uri src, CropImage crop) {
        Album.cropOf(src, new Album.ICropListener() {
            @Override
            public void onCrop(@Nullable Uri uri) {
                if (uri == null) {
                    Response.create(id).state(0).desc(ResUtil.getString(R.string.crop_failed)).notify(getWebView());
                } else {
                    Response.create(id).result(uri.toString()).notify(getWebView());
                }
            }
        }).withMaxSize(crop.getWidth(), crop.getHeight()).
                withAspect(crop.getX(), crop.getY())
                .start(getActivity());
    }

    @JavascriptInterface
    public final void previewImage(String message) {
        final Request<PreviewImage> request = Request.parseRequest(message, PreviewImage.class);
        final PreviewImage previewImage = request.getParams();

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                RequestPermission.of(getActivity())
                        .onPermissionResult(new IPermissionHelper() {
                            @Override
                            protected void onResult(boolean isGranted) {
                                if (isGranted) {
                                    Album.previewOf(previewImage.getCurrent(), previewImage.getUrls()).start(getActivity());
                                }
                            }

                            @Override
                            protected void onManuallyRationale() {
                                final String title = ResUtil.getPermissionTitle(R.string.string_permission_storage);
                                final String desc = ResUtil.getPermissionSettingDesc(R.string.string_permission_storage);
                                showManuallyOnDialog(title, desc);
                            }
                        }).request(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        };
        post(task);
    }

    @JavascriptInterface
    public final void chooseImage(String message) {
        final Request<ChooseImage> request = Request.parseRequest(message, ChooseImage.class);
        final ChooseImage chooseImage = request.getParams();

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                RequestPermission.of(getActivity())
                        .onPermissionResult(new IPermissionHelper() {
                            @Override
                            protected void onResult(boolean b) {
                                if (!b) {
                                    return;
                                }
                                Album.chooseOf(new Album.IChooserCallback() {
                                    @Override
                                    public void onSelected(String[] values) {
                                        if (values == null) {
                                            return;
                                        }
                                        Response.create(request.getCallbackId()).result(values).notify(getWebView());
                                    }
                                }).params(chooseImage.getCount(), Album.SizeType.ALL, Album.SourceType.ALL)
                                        .to(getActivity());
                            }

                            @Override
                            protected void onManuallyRationale() {
                                final String title = ResUtil.getPermissionTitle(R.string.string_permission_storage);
                                final String desc = ResUtil.getPermissionSettingDesc(R.string.string_permission_storage);
                                showManuallyOnDialog(title, desc);
                            }
                        }).request(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        };
        post(task);
    }

    @JavascriptInterface
    public final void getNetworkType(String message) {
        final Request<String> request = Request.parseRequest(message, String.class);
        final String networkType = NetUtil.getNetworkType(getActivity());

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                Response.create(request.getCallbackId())
                        .result(networkType)
                        .state(Response.RESULT_OK)
                        .notify(getWebView());
            }
        };
        post(task);
    }

    @JavascriptInterface
    public final void getLocation(String message) {
        final Request<GetLocation> request = Request.parseRequest(message, GetLocation.class);
        final GetLocation location = request.getParams();
        final LocationParams params = location.toLocationParams();
        final Runnable task = new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    final android.location.LocationManager lm = (android.location.LocationManager) BridgeApi.get().getSystemService(Context.LOCATION_SERVICE);
                    if (!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                        onShowPermissionRationale(PermissionConst.LOCATION_SERVICE, ResUtil.getString(R.string.string_service_location));
                        return;
                    }
                }
                RequestPermission.of(getActivity())
                        .onPermissionResult(new IPermissionHelper() {
                            @Override
                            protected void onResult(boolean isGranted) {
                                if (!isGranted) {
                                    return;
                                }
                                final LocationManager manager = new LocationManager(getActivity(), params);
                                manager.onLocationChange(new ILocationListener() {
                                    @Override
                                    public void onReceiveLocation(BDLocation location) {
                                        final LocationResult result = LocationResult.toResult(location);
                                        Response.create(request.getCallbackId())
                                                .result(result).notify(getWebView());
                                    }

                                    @Override
                                    public void onLocationFailed(int what, int type, String desc) {
                                        final LocationFailed failed = new LocationFailed();
                                        failed.what = what;
                                        failed.type = type;
                                        failed.desc = desc;
                                        Response.create(request.getCallbackId())
                                                .result(failed).notify(getWebView());
                                    }
                                }).start();
                            }

                            @Override
                            protected void onManuallyRationale() {
                                final String title = ResUtil.getPermissionTitle(R.string.string_permission_location);
                                final String desc = ResUtil.getPermissionSettingDesc(R.string.string_permission_location);
                                showManuallyOnDialog(title, desc);
                            }
                        }).request(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        };
        post(task);
    }

    @JavascriptInterface
    public final void scanQRCode(String message) {
        final Request<QrCode> request = Request.parseRequest(message, QrCode.class);
        final QrCode qrCode = request.getParams();

        final Runnable task = new Runnable() {
            @Override
            public void run() {
                RequestPermission.of(getActivity())
                        .onPermissionResult(new IPermissionHelper() {
                            @Override
                            protected void onResult(boolean isGranted) {
                                if (!isGranted) {
                                    return;
                                }
                                QrScan.of(new IScanResultCallback() {
                                    @Override
                                    public void onScanResult(String result) {
                                        Response.create(request.getCallbackId()).result(result).notify(getWebView());
                                    }
                                }).withTip(qrCode.tip).withAnimTime(qrCode.animTime)
                                        .withBarCode(qrCode.barCode)
                                        .withWidth(qrCode.width)
                                        .start(getActivity());
                            }

                            @Override
                            protected void onManuallyRationale() {
                                final String title = ResUtil.getPermissionTitle(R.string.string_permission_camera);
                                final String desc = ResUtil.getPermissionSettingDesc(R.string.string_permission_camera);
                                showManuallyOnDialog(title, desc);
                            }
                        }).request(Manifest.permission.CAMERA);
            }
        };
        post(task);
    }

    public final void attachTarget(T target) {
        super.bindTarget(target);
        BridgeApi.register(getContext());
    }

    private FragmentActivity getActivity() {
        return BridgeApi.getCurrentActivity(getTarget());
    }

    private Context getContext() {
        return BridgeApi.getCurrentContext(getTarget());
    }
}
