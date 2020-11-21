package com.longrise.android.x5web.internal.filer;

import com.longrise.android.result.IActivityOnResultListener;

/**
 * Created by godliness on 2020/11/20.
 *
 * @author godliness
 */
public interface IFilerListener extends IActivityOnResultListener {

    /**
     * Close input label event {@link FileChooser#onReceiveValueEnd()}
     */
    void onReceiveValueEnd();
}
