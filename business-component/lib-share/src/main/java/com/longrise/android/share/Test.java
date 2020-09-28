package com.longrise.android.share;

import com.longrise.android.share.onekeyshare.OneKeyShare;

/**
 * Created by godliness on 2020/9/21.
 *
 * @author godliness
 */
class Test {

    public static void  test(){
        OneKeyShare.ui(new OneKeyShare.IShareListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onCancel() {

            }
        }).show(null);
    }
}
