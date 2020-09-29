package com.longrise.android.photowall.album;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.longrise.android.photowall.AlbumParams;
import com.longrise.android.photowall.Chooser;
import com.longrise.android.photowall.R;

import java.util.List;

/**
 * Created by godliness on 2020/9/8.
 *
 * @author godliness
 * 照片墙
 */
public final class PhotosActivity extends AppCompatActivity implements
        View.OnClickListener, PhotosAdapter.OnChooseListener, Handler.Callback, FolderWindow.OnFolderListener {

    private static final int MSG_LOAD_COMPLETED = 1;
    private static final int MSG_LOAD_FAILED = 0;
    private static final int MSG_SHOW_FOLDERS = 2;

    private View mBack;
    private TextView mFolder;
    private TextView mNumber;
    private TextView mConfig;
    private View mShade;

    private RecyclerView mRcv;
    private PhotosAdapter mAdapter;

    private View mLoading;
    private TextView mTips;
    private Runnable mTipsTask;

    private final Handler mHandler = new Handler(this);
    private ArraySet<String> mChooseFiles;

    private List<Folder> mFolders;
    private int mVisibleHeight;
    private AlbumParams mParams;

    //    private PhotoAlbumContentObserver mContentObserver;
    private int mCurrentFolderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Utils.register(this);
        super.onCreate(savedInstanceState);
        getExtraData(savedInstanceState);
        setContentView(R.layout.activity_photos);
        initView();
//        this.mContentObserver = PhotoAlbumContentObserver.registerObserver(this);

        loadMedia();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (isFinishing()) {
            return true;
        }
        switch (msg.what) {
            case MSG_LOAD_COMPLETED:
                final MediaData data = (MediaData) msg.obj;
                data.trimToSize();
                this.mFolders = data.getFolders();
                notifyData(Utils.getString(R.string.lib_album_string_all_images), data.getTotalFiles(), data.getFileSize());
                return true;

            case MSG_LOAD_FAILED:
                notifyFailed();
                return true;

            case MSG_SHOW_FOLDERS:
                showFolderWindow();
                return true;

            default:
                return false;
        }
    }

//    @Override
//    public void onChanged(Uri uri) {
//        if (!isFinishing()) {
//            loadMedia();
//        }
//    }

    @Override
    public void onFileClick(String filePath) {
        if (mChooseFiles == null) {
            mChooseFiles = new ArraySet<>(mParams.count);
        }
        exChangeSelectedStatus(filePath);
        changeSelectedStatus();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.folder) {
            mHandler.removeMessages(MSG_SHOW_FOLDERS);
            obtainMessage(MSG_SHOW_FOLDERS).sendToTarget();
        } else if (id == R.id.tv_config) {
            if (mChooseFiles != null) {
                setResultToFinish();
            }
            finish();
        }
    }

    @Override
    public void onFolderShow() {
        mShade.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFolderDismiss() {
        mShade.setVisibility(View.GONE);
    }

    @Override
    public void onFolderClick(Folder folder) {
        if (mCurrentFolderId != folder.getFolderId()) {
            notifyData(folder.getFolderName(), folder.getFiles(), folder.getCount());
            this.mCurrentFolderId = folder.getFolderId();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AlbumParams.EXTRA_PARAMS, mParams);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
//        if (mContentObserver != null) {
//            mContentObserver.unregisterObserver();
//        }
        super.onDestroy();
    }

    private void notifyData(String folderName, List<String> files, int size) {
        mLoading.setVisibility(View.GONE);
        if (mAdapter != null) {
            mAdapter.setData(files);
        }
        mNumber.setText(String.format(Utils.getString(R.string.lib_album_string_num), size));
        mFolder.setText(folderName);
    }

    private void notifyFailed() {
        mLoading.setVisibility(View.GONE);
        showTips(Utils.getString(R.string.lib_album_string_load_failed));
    }

    private void initView() {
        mBack = findViewById(R.id.iv_back);
        mRcv = findViewById(R.id.rcv_photos);
        findViewById(R.id.folder).setOnClickListener(this);
        mFolder = findViewById(R.id.tv_folder);
        mNumber = findViewById(R.id.tv_total);
        mConfig = findViewById(R.id.tv_config);
        mShade = findViewById(R.id.view_shade);
        mTips = findViewById(R.id.tv_tips);
        mLoading = findViewById(R.id.loading);
        regEvent();
        bindAdapter();
    }

    private void regEvent() {
        mBack.setOnClickListener(this);
        mConfig.setOnClickListener(this);
    }

    private void loadMedia() {
        new LoadMediaTask(this, new LoadMediaTask.OnMediaLoadListener() {
            @Override
            public void onMediaLoaded(@Nullable MediaData mediaData) {
                if (isFinishing()) {
                    return;
                }
                final Message msg = obtainMessage(mediaData == null ? MSG_LOAD_FAILED : MSG_LOAD_COMPLETED);
                msg.obj = mediaData;
                msg.sendToTarget();
            }
        }).start();
    }

    private Message obtainMessage(int what) {
        return mHandler.obtainMessage(what);
    }

    private void bindAdapter() {
        mAdapter = new PhotosAdapter(this, mParams.count);
        mRcv.setAdapter(mAdapter);
        mRcv.setLayoutManager(new GridLayoutManager(this, 4));
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        mRcv.addItemDecoration(new GridSpaceItemDecoration(4, 6, 6, screenWidth));
    }

    private void showFolderWindow() {
        if (mFolders == null) {
            return;
        }
        if (mVisibleHeight <= 0) {
            final int validHeight = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
            this.mVisibleHeight = validHeight - (Utils.dip2px(93));
        }
        FolderWindow folderWindow = new FolderWindow(this);
        folderWindow.applyMaxHeight(mVisibleHeight);
        folderWindow.showAtLocation(mFolder, Gravity.BOTTOM, 0, mFolder.getHeight());
        folderWindow.setData(mFolders);
    }

    private void showTips(String value) {
        if (mTipsTask != null) {
            mHandler.removeCallbacks(mTipsTask);
        }
        setTips(value);
        mHandler.postDelayed(getTipsTask(), 3000);
    }

    private Runnable getTipsTask() {
        if (mTipsTask == null) {
            mTipsTask = new Runnable() {
                @Override
                public void run() {
                    setTips("");
                }
            };
        }
        return mTipsTask;
    }

    private void setTips(String value) {
        mTips.setVisibility(TextUtils.isEmpty(value) ? View.GONE : View.VISIBLE);
        mTips.setText(value);
    }

    private void getExtraData(Bundle state) {
        if (state != null) {
            this.mParams = state.getParcelable(AlbumParams.EXTRA_PARAMS);
        } else {
            this.mParams = getIntent().getParcelableExtra(AlbumParams.EXTRA_PARAMS);
        }
    }

    private void changeSelectedStatus() {
        final int chooseSize;
        if ((chooseSize = mChooseFiles.size()) > 0) {
            mConfig.setText(String.format(Utils.getString(R.string.lib_album_string_confirm), chooseSize, mParams.count));
            mConfig.setVisibility(View.VISIBLE);
        } else {
            mConfig.setVisibility(View.GONE);
        }
    }

    private void exChangeSelectedStatus(String filePath) {
        if (mChooseFiles.contains(filePath)) {
            mChooseFiles.remove(filePath);
        } else {
            if (mChooseFiles.size() >= mParams.count) {
                showTips(String.format(Utils.getString(R.string.lib_album_string_max_select), mParams.count));
                return;
            }
            mChooseFiles.add(filePath);
        }
    }

    private void setResultToFinish() {
        final int size = mChooseFiles.size();
        final String[] files = new String[size];
        for (int i = 0; i < size; i++) {
            files[i] = "file://" + mChooseFiles.valueAt(i);
        }
        Chooser.notifyChooseResult(this, files);
    }
}
