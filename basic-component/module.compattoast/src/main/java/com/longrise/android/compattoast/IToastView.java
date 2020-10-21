package com.longrise.android.compattoast;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by godliness on 2020/10/16.
 *
 * @author godliness
 */
public interface IToastView {

    IToastView setText(String title);

    IToastView setView(View childView);

    View getView();

    IToastView setGravity(int gravity, int xOffset, int yOffset);

    void show(ViewGroup parent);

    void cancel();
}
