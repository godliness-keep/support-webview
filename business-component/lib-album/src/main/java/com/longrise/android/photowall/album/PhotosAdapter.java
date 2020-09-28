package com.longrise.android.photowall.album;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.longrise.android.photowall.R;

import java.util.List;

/**
 * Created by godliness on 2020/9/9.
 *
 * @author godliness
 */
final class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotosHolder> {

    private final LayoutInflater mInflater;

    private final int mMaxSize;
    private final ArrayMap<String, Count> mIndexes;

    private List<String> mFiles;
    private OnChooseListener mChooseListener;

    interface OnChooseListener {

        /**
         * 选中的文件
         *
         * @param filePath 文件路径（本地）
         */
        void onFileClick(String filePath);
    }

    PhotosAdapter(Context cxt) {
        this(cxt, 3);
    }

    PhotosAdapter(Context cxt, int maxSize) {
        this.mInflater = LayoutInflater.from(cxt);
        this.mMaxSize = maxSize;
        this.mIndexes = new ArrayMap<>(maxSize);

        if (cxt instanceof OnChooseListener) {
            setOnChooserListener((OnChooseListener) cxt);
        }
    }

    void setOnChooserListener(OnChooseListener chooserListener) {
        this.mChooseListener = chooserListener;
    }

    void setData(List<String> files) {
        this.mFiles = files;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotosHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PhotosHolder(mInflater.inflate(R.layout.item_view_photo_wall, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotosHolder holder, int position) {
        final String filePath = mFiles.get(position);

        final Count count = getIndex(filePath);
        if (count != null) {
            holder.setTips(String.format("%s", count.value));
            count.adjustPosition(position);
        } else {
            holder.setTips("");
        }

        holder.setFlag(filePath);
        holder.setPosition(position);
        if (!holder.hasOnClickListeners()) {
            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String flag = ((PhotoView) v).getFlag();
                    final int position = ((PhotoView) v).getPosition();

                    if (mChooseListener != null) {
                        mChooseListener.onFileClick(flag);
                    }

                    final Count count = removeIndex(flag);
                    if (count == null) {
                        putCount(holder, flag, position);
                    } else {
                        changeMinusOne(count);
                    }
                }
            });
        }

        Glide.with(holder.mThum).load(filePath).into(holder.mThum);
    }

    private void putCount(PhotosHolder holder, String flag, int position) {
        final int size = getIndexSize();
        if (size < mMaxSize) {
            final int newSize = size + 1;
            putIndex(flag, new Count(newSize, position));
            holder.setTips(String.format("%s", newSize));
        }
    }

    @Override
    public int getItemCount() {
        return mFiles != null ? mFiles.size() : 0;
    }

    static final class PhotosHolder extends RecyclerView.ViewHolder {

        private final PhotoView mThum;
        private final TextView mTips;

        PhotosHolder(@NonNull View itemView) {
            super(itemView);
            this.mThum = itemView.findViewById(R.id.iv_item_photo_adapter);
            this.mTips = itemView.findViewById(R.id.tv_count_item_photo_adapter);
        }

        void setTips(String tips) {
            mTips.setText(tips);
            if (TextUtils.isEmpty(tips)) {
                mTips.setVisibility(View.GONE);
            } else {
                mTips.setVisibility(View.VISIBLE);
            }
        }

        void setFlag(String flag) {
            mThum.setFlag(flag);
        }

        void setPosition(int position) {
            mThum.setPosition(position);
        }

        boolean hasOnClickListeners() {
            return mThum.hasOnClickListeners();
        }

        void setOnClickListener(View.OnClickListener onClickListener) {
            mThum.setOnClickListener(onClickListener);
        }
    }

    static final class Count {

        int position;
        int value;

        Count(int value, int position) {
            this.value = value;
            this.position = position;
        }

        void adjustPosition(int newPosition) {
            if (position != newPosition) {
                this.position = newPosition;
            }
        }
    }

    private void changeMinusOne(Count count) {
        final int size = mIndexes.size();
        for (int i = 0; i < size; i++) {
            final Count value = mIndexes.valueAt(i);
            if (value.value > count.value) {
                value.value--;
                notifyItemChanged(value.position);
            }
        }
        notifyItemChanged(count.position);
    }

    private Count getIndex(String file) {
        return mIndexes.get(file);
    }

    private Count removeIndex(String file) {
        return mIndexes.remove(file);
    }

    private void putIndex(String file, Count count) {
        mIndexes.put(file, count);
    }

    private int getIndexSize() {
        return mIndexes.size();
    }
}
