package com.longrise.android.location;

import android.content.Context;
import android.support.annotation.NonNull;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.longrise.android.location.mode.LocationParams;

/**
 * Created by godliness on 2020/9/23.
 *
 * @author godliness
 */
public final class LocationManager extends BDAbstractLocationListener {

    private final LocationClient mClient;

    private ILocationListener mCallback;
    private volatile boolean mRegistered;
    private final boolean mOnce;

    public LocationManager(@NonNull Context cxt) {
        this(cxt, LocationParams.getDefault());
    }

    public LocationManager(@NonNull Context cxt, LocationParams params) {
        this.mClient = new LocationClient(cxt.getApplicationContext());
        this.mClient.registerLocationListener(this);
        this.mRegistered = true;
        this.mOnce = params.isOnce();

        configLocationOption(params);
    }

    /**
     * 监听定位结果
     */
    public LocationManager onLocationChange(ILocationListener locationListener) {
        this.mCallback = locationListener;
        return this;
    }

    /**
     * 启动定位SDK，调用之后只需要等待定位结果自动回调即可，开发者定位场景如果是单次定位的场景，在收到定位结果之后直接调用stop()函数即可。
     */
    public void start() {
        if (mClient != null) {
            mClient.start();
        }
    }

    /**
     * 快速触发一次定位
     */
    public void requestLocation() {
        start();
        if (mClient != null) {
            mClient.requestLocation();
        }
    }

    /**
     * 重新启动定位
     */
    public void restart() {
        if (mClient != null) {
            mClient.restart();

            if (!mRegistered) {
                mClient.registerLocationListener(this);
                mRegistered = true;
            }
        }
    }

    /**
     * 关闭定位SDK，如果stop()之后仍然想进行定位，可以再次start()等待定位结果回调即可。
     */
    public void stop() {
        if (mClient != null) {
            mClient.stop();

            if (mRegistered) {
                mClient.unRegisterLocationListener(this);
                mRegistered = false;
            }
        }
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
        final int locType = bdLocation.getLocType();
        if (locType == 61 || locType == 161) {
            if (mCallback != null) {
                mCallback.onReceiveLocation(bdLocation);
            }
        }

        if (mOnce) {
            // 表示仅定位一次
            stop();
        }
    }

    /**
     * 定位错误码：http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/addition-func/error-code
     */
    @Override
    public void onLocDiagnosticMessage(int i, int i1, String s) {
        if(mCallback != null){
            mCallback.onLocationFailed(i, i1, s);
        }
    }

    private void configLocationOption(LocationParams params) {
        final LocationClientOption option = new LocationClientOption();

        if (params.isHighAccuracy) {
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        } else {
            option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        }
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType(params.type);
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(params.highAccuracyExpireTime);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(params.isHighAccuracy);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setNeedNewVersionRgc(true);
        //可选，设置是否需要最新版本的地址信息。默认需要，即参数为true

        option.setIsNeedAltitude(params.isAltitude);
        //可选，设置是否需要高地信息，由于获取高度需要较高精确度，会减慢接口返回速度

        option.setIsNeedAddress(params.isAddress);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true

        mClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }
}
