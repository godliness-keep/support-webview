package com.longrise.android.result;

import android.content.Intent;
import android.support.annotation.NonNull;


/**
 * Created by godliness on 2020/9/14.
 *
 * @author godliness
 */
public interface OnActivityResultListener {

    void onActivityResult(int resultCode, @NonNull Intent data);
}
