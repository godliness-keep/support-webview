package com.longrise.android.photowall.album;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by godliness on 2020/9/10.
 *
 * @author godliness
 */
final class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mSpanCount;
    private final int mRowSpacing;
    private final int mColumnSpacing;

    private final int mWidth;

    GridSpaceItemDecoration(int spanCount, int rowSpacing, int columnSpacing, int screenWidth) {
        this.mSpanCount = spanCount;
        this.mRowSpacing = rowSpacing;
        this.mColumnSpacing = columnSpacing;
        this.mWidth = (screenWidth - (spanCount - 1) * columnSpacing) / spanCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        final ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.height = mWidth;

        final int position = parent.getChildAdapterPosition(view);
        if (position >= mSpanCount) {
            outRect.top = mRowSpacing;
        }

        final int column = position % mSpanCount;
        if (column >= 1) {
            outRect.left = mColumnSpacing;
        }
    }
}
