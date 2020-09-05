package com.longrise.android.webview.demo.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020/9/5.
 *
 * @author godliness
 */
public class Params {

    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("age")
    public int age;

    @Expose
    @SerializedName("sex")
    public String sex;

    @Override
    public String toString() {
        return "Params{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                '}';
    }
}
