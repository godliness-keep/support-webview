package com.longrise.android.compattoast;

import android.app.Activity;
import android.view.View;

/**
 * Created by godliness on 2020/10/16.
 *
 * @author godliness
 */
public interface IToast {

    IToast setText(String text);

    IToast setDuration(int duration);

    IToast setView(View childView);

    View getView();

    IToast setGravity(int gravity, int xOffset, int yOffset);

    void show(Activity binding);

    void cancel();
}
