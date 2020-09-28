package com.longrise.android.photowall.album;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.longrise.android.photowall.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by godliness on 2020/9/8.
 *
 * @author godliness
 */
final class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderHolder> {

    private final Context mCxt;
    private final LayoutInflater mInflater;

    private final List<Folder> mFolders;
    private OnFolderClickListener mFolderListener;

    public FolderAdapter(Context cxt) {
        this.mCxt = cxt;
        this.mInflater = LayoutInflater.from(cxt);
        this.mFolders = new ArrayList<>(8);
    }

    public void setData(List<Folder> folders) {
        mFolders.addAll(folders);
        notifyDataSetChanged();
    }

    public void setFolderClickListener(OnFolderClickListener folderClickListener) {
        this.mFolderListener = folderClickListener;
    }

    public interface OnFolderClickListener {

        /**
         * 目录点击事件
         *
         * @param folder {@link Folder}
         */
        void onFolderClick(Folder folder);
    }

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FolderHolder(mInflater.inflate(R.layout.item_view_window_folder, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final FolderHolder folderHolder, int position) {
        final Folder folder = mFolders.get(position);
        Glide.with(mCxt).load(folder.getFirstFilePath()).into(folderHolder.mPreview);
        folderHolder.mName.setText(folder.getFolderName());
        folderHolder.mCount.setText(String.format("%s", folder.getCount()));
        if (!folderHolder.hasOnClickListeners()) {
            folderHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFolderListener != null) {
                        mFolderListener.onFolderClick(folder);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFolders != null ? mFolders.size() : 0;
    }

    static class FolderHolder extends RecyclerView.ViewHolder {

        final ImageView mPreview;
        final TextView mName;
        final TextView mCount;

        private final View mRoot;

        FolderHolder(@NonNull View itemView) {
            super(itemView);
            this.mRoot = itemView;
            this.mPreview = itemView.findViewById(R.id.tv_item_preview);
            this.mName = itemView.findViewById(R.id.tv_item_name);
            this.mCount = itemView.findViewById(R.id.tv_item_count);
        }

        boolean hasOnClickListeners() {
            return mRoot.hasOnClickListeners();
        }

        void setOnClickListener(View.OnClickListener onClickListener) {
            mRoot.setOnClickListener(onClickListener);
        }
    }
}
