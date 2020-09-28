package com.longrise.android.location;

import com.baidu.location.BDLocation;

/**
 * Created by godliness on 2020/9/23.
 *
 * @author godliness
 */
public interface ILocationListener {

    void onReceiveLocation(BDLocation location);

    void onLocationFailed(int what, int type, String desc);
}
