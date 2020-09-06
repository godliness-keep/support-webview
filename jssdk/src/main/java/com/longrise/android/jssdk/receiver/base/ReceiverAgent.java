package com.longrise.android.jssdk.receiver.base;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by godliness on 2020-04-21.
 *
 * @author godliness
 */
public abstract class ReceiverAgent<T extends BaseReceiver> {

    public abstract void lifecycle(Activity lifecycle);

    public abstract void lifecycle(Fragment lifecycle);

    abstract String getEventName();

    abstract T getReceiver();
}
