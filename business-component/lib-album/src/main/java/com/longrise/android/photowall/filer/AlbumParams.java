package com.longrise.android.photowall.filer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by godliness on 2020/9/11.
 *
 * @author godliness
 */
public final class AlbumParams implements Parcelable {

    public static final String EXTRA_PARAMS = "extra_album_params";

    public int count = 9;
    public int sizeType;
    public int sourceType;

    AlbumParams() {

    }

    AlbumParams(Parcel in) {
        count = in.readInt();
        sizeType = in.readInt();
        sourceType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(calcCount());
        dest.writeInt(sizeType);
        dest.writeInt(sourceType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AlbumParams> CREATOR = new Creator<AlbumParams>() {
        @Override
        public AlbumParams createFromParcel(Parcel in) {
            return new AlbumParams(in);
        }

        @Override
        public AlbumParams[] newArray(int size) {
            return new AlbumParams[size];
        }
    };

    private int calcCount() {
        if (count > 9) {
            this.count = 9;
        } else {
            this.count = Math.max(count, 1);
        }
        return count;
    }
}
