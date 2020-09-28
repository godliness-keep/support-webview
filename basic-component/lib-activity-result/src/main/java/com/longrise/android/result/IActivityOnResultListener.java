package com.longrise.android.result;

import android.content.Intent;

/**
 * Created by godliness on 2020/9/14.
 *
 * @author godliness
 */
public interface IActivityOnResultListener {

    void onActivityResult(int resultCode, Intent data);
}
