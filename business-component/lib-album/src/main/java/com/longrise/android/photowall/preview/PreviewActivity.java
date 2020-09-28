package com.longrise.android.photowall.preview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.longrise.android.photowall.R;

/**
 * Created by godliness on 2020/9/12.
 *
 * @author godliness
 */
public final class PreviewActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{

    public static final String EXTRA_URLS = "extra_urls";

    private ViewPager mPager;
    private TextView mProgress;
    private String[] mUrls;
    private int mLength;

    @Override
    protected void onCreate(@Nullable Bundle state) {
        getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.white));
        super.onCreate(state);
        beforeSetContentView();
        getExtraData(state);
        setContentView(R.layout.activity_preview);
        initView();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_back){
            finish();
        }
    }

    private void initView() {
        mPager = findViewById(R.id.vp_preview);
        mProgress = findViewById(R.id.tv_progress);
        findViewById(R.id.iv_back).setOnClickListener(this);
        final View titleBar = findViewById(R.id.title_bar);
        final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) titleBar.getLayoutParams();
        lp.topMargin = getStatusBarHeight(this);

        initAdapter();
    }

    private void getExtraData(Bundle state) {
        if (state == null) {
            this.mUrls = getIntent().getStringArrayExtra(EXTRA_URLS);
        } else {
            onRestoreState(state);
        }
    }

    private void onRestoreState(Bundle state) {
        this.mUrls = state.getStringArray(EXTRA_URLS);
    }

    private void initAdapter() {
        final PreviewAdapter adapter = new PreviewAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mPager.addOnPageChangeListener(this);
        adapter.setUrls(mUrls);

        if (mUrls != null) {
            mLength = mUrls.length;
            setPageProgress(1);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        setPageProgress(++i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private void setPageProgress(int current) {
        mProgress.setText(String.format("(%s/%s)", current, mLength));
    }

    private static final class PreviewAdapter extends FragmentPagerAdapter {

        private String[] mUrls;

        PreviewAdapter(FragmentManager fm) {
            super(fm);
        }

        void setUrls(String[] urls) {
            this.mUrls = urls;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int i) {
            return PreviewFragment.createInstance(mUrls[i]);
        }

        @Override
        public int getCount() {
            return mUrls != null ? mUrls.length : 0;
        }
    }

    public static final class PreviewFragment extends Fragment {

        private static final String EXTRA_INDEX = "index";

        private View mRetry;
        private ImageView mPreview;
        private View mLoading;

        private String mUrl;

        public static PreviewFragment createInstance(String url) {
            final PreviewFragment previewFragment = new PreviewFragment();
            final Bundle extra = new Bundle();
            extra.putString(EXTRA_INDEX, url);
            previewFragment.setArguments(extra);
            return previewFragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            if (savedInstanceState == null) {
                mUrl = getPath();
            } else {
                mUrl = savedInstanceState.getString(EXTRA_INDEX);
            }
            return inflater.inflate(R.layout.item_preview_fragment, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            initView();
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString(EXTRA_INDEX, mUrl);
        }

        private void initView() {
            mRetry = getView().findViewById(R.id.tv_retry);
            mPreview = getView().findViewById(R.id.iv_preview);
            mLoading = getView().findViewById(R.id.progress);

            preview();
        }

        private void preview() {
            if (TextUtils.isEmpty(mUrl)) {
                loadFailed();
                return;
            }
            Glide.with(this).load(Uri.parse(mUrl)).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    mLoading.setVisibility(View.GONE);
                    loadFailed();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    mLoading.setVisibility(View.GONE);
                    return false;
                }
            }).into(mPreview);
        }

        private void loadFailed() {
            if (!mRetry.hasOnClickListeners()) {
                mRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.GONE);
                        preview();
                    }
                });
            }
            mRetry.setVisibility(View.VISIBLE);
        }

        @Nullable
        private String getPath() {
            final Bundle extra = getArguments();
            if (extra != null) {
                return extra.getString(EXTRA_INDEX);
            }
            return null;
        }
    }

    private void beforeSetContentView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(Color.parseColor("#AA000000"));
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private static int getStatusBarHeight(Context context) {
        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
}
