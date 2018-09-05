package com.tricheer.radio;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Service;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tricheer.radio.activity.BaseKeyEventActivity;
import com.tricheer.radio.engine.BandInfos.BandType;
import com.tricheer.radio.frags.TabFreqCollectFragment;
import com.tricheer.radio.utils.SettingsSysUtil;

import java.util.ArrayList;
import java.util.List;

import js.lib.android.fragment.BaseAppV4Fragment;

/**
 * FM Main Page
 *
 * @author Jun.Wang
 */
public class MainActivity extends BaseKeyEventActivity {
    // TAG
    private static final String TAG = "MainActivity";

    //==========Widgets in this Activity==========
    private TextView tvFreq;
    private SeekBar seekBarFreq;
    private ImageView ivTower;
    private TextView tvBand;
    private ImageView ivPrev, ivExit, ivNext;
    private TextView tvUpdate;

    private RelativeLayout vPointsContainer;
    private ViewPager viewPager;
    private LinearLayout vPoints;
    private View vSelectedPoint;

    //==========Variables in this Activity==========
    private TabFragOnPageChange mTabFragOnPageChange;
    private VPFragPagerAdapter mFragAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SettingsSysUtil.setFmState(this, 1);
        init();
    }

    private void init() {
        //
        vPointsContainer = (RelativeLayout) findViewById(R.id.v_points_container);
        viewPager = (ViewPager) findViewById(R.id.vpager);
        viewPager.setAdapter(mFragAdapter = new VPFragPagerAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener((mTabFragOnPageChange = new TabFragOnPageChange()));

        tvFreq = (TextView) findViewById(R.id.v_freq);
        tvFreq.setText("");

        seekBarFreq = (SeekBar) findViewById(R.id.seekbar_freqs);
        seekBarFreq.setOnSeekBarChangeListener(new SeekBarOnChange());

        tvBand = (TextView) findViewById(R.id.v_band);
        ivTower = (ImageView) findViewById(R.id.v_radio_tower);
        ivTower.setOnClickListener(mViewOnClick);

        ivPrev = (ImageView) findViewById(R.id.iv_play_pre);
        ivPrev.setOnClickListener(mViewOnClick);

        ivNext = (ImageView) findViewById(R.id.iv_play_next);
        ivNext.setOnClickListener(mViewOnClick);

        ivExit = (ImageView) findViewById(R.id.iv_exit);
        ivExit.setOnClickListener(mViewOnClick);

        tvUpdate = (TextView) findViewById(R.id.v_update);
        tvUpdate.setOnClickListener(mViewOnClick);

        // Initialize page data
        int lastBand = getLastBand();
        setBandTxt(lastBand);
        loadPointContainer(lastBand);
        loadViewPager();

        // Bind Service
        bindAndCreateControlService(1, 2);
    }

    private void setBandTxt(int band) {
        String bandTxt = "";
        switch (band) {
            case BandType.FM:
                bandTxt = getString(R.string.band_fm);
                break;
            case BandType.AM:
                bandTxt = getString(R.string.band_am);
                break;
        }
        tvBand.setText(bandTxt);
    }

    @SuppressLint("InflateParams")
    private void loadPointContainer(int band) {
        vPointsContainer.removeAllViews();
        switch (band) {
            case BandType.FM:
                View vPointsFM = getLayoutInflater().inflate(R.layout.v_points_fm, null);
                vPoints = (LinearLayout) vPointsFM.findViewById(R.id.v_bg_points);
                vSelectedPoint = vPointsFM.findViewById(R.id.v_select_point);
                vPointsContainer.addView(vPointsFM);
                break;
            case BandType.AM:
                View vPointsAM = getLayoutInflater().inflate(R.layout.v_points_am, null);
                vPoints = (LinearLayout) vPointsAM.findViewById(R.id.v_bg_points);
                vSelectedPoint = vPointsAM.findViewById(R.id.v_select_point);
                vPointsContainer.addView(vPointsAM);
                break;
        }
    }

    private void loadViewPager() {
        List<BaseAppV4Fragment> mListPages = new ArrayList<BaseAppV4Fragment>();
        int loop = getPageSum();
        for (int idx = 0; idx < loop; idx++) {
            mListPages.add(new TabFreqCollectFragment());
        }
        mFragAdapter.refresh(mListPages);
    }

    @Override
    protected void onServiceStatusChanged(Service service, boolean isConnected) {
        if (isConnected) {
            initSeekBar();
            register(this);
            execOpenRadio();
        }
    }

    private View.OnClickListener mViewOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == ivTower) {
                switchBand();
            } else if (v == ivPrev) {
                scanAndPlayPrev();
            } else if (v == ivNext) {
                scanAndPlayNext();
            } else if (v == ivExit) {
                closeFm();
                finish();
            } else if (v == tvUpdate) {
                scanAll();
            }
        }

        void switchBand() {
            ivTower.setEnabled(false);
            ObjectAnimator objAnim = ObjectAnimator.ofFloat(ivTower, "rotationY", 0, 180);
            objAnim.setInterpolator(new LinearInterpolator());
            objAnim.setDuration(300);
            objAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    execSwitchBand();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            objAnim.start();
        }
    };

    @Override
    protected void execSwitchBand() {
        super.execSwitchBand();
        initSeekBar();
        setBandTxt(getCurrBand());
        ivTower.setEnabled(true);
    }

    @Override
    public void onFreqChanged(int freq, int band) {
        super.onFreqChanged(freq, band);
//        setBandTxt(band);
        setFreqTxt(freq, band);
        seekBarFreq.setProgress(freq - getMinFreq());
    }

    private void setFreqTxt(int freq, int band) {
        if (tvFreq == null || tvBand == null) {
            return;
        }
        switch (band) {
            case BandType.FM:
                String txt = tvBand.getText() + String.valueOf(freq / 100d);
                tvFreq.setText(txt);
                break;
            case BandType.AM:
                txt = tvBand.getText() + String.valueOf(freq);
                tvFreq.setText(txt);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        SettingsSysUtil.setFmState(this, 0);
        unregister(this);
        closeFm();
        bindAndCreateControlService(3, 4);
        super.onDestroy();
    }

    public class VPFragPagerAdapter extends FragmentPagerAdapter {

        /**
         * Fragment List
         */
        private List<BaseAppV4Fragment> mListFms;

        VPFragPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        void setListFrags(List<BaseAppV4Fragment> listFms) {
            this.mListFms = listFms;
        }

        void refresh(List<BaseAppV4Fragment> listFms) {
            setListFrags(listFms);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            if (mListFms == null || mListFms.size() == 0) {
                return null;
            }
            return mListFms.get(position);
        }

        @Override
        public int getCount() {
            if (mListFms == null) {
                return 0;
            }
            return mListFms.size();
        }
    }

    private class TabFragOnPageChange implements ViewPager.OnPageChangeListener {

        /**
         * 记录上一次页面索引
         */
        private int mmLastPageIdx = 0;

        /**
         * @param pos 1 开始滑动 2 滑动完毕 0 保持不变
         */
        @Override
        public void onPageScrollStateChanged(int pos) {
            // Log.i(TAG, "TabFragOnPageChange> onPageScrollStateChanged(" + pos + ")");
        }

        /**
         * @param pos             表示的当前屏幕显示的左边页面的position
         * @param posOffset       表示的当前屏幕显示的左边页面偏移的百分比
         * @param posOffsetPixels 向右滑动到头再向左滑动到头变化规律
         */
        @Override
        public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) {
            Log.i(TAG, "TabFragOnPageChange> onPageScrolled(" + pos + "," + posOffset + "," +
                    posOffsetPixels + ")");
        }

        /**
         * @param pos 滑动停止后调用，表示当前选中页面的索引
         */
        @Override
        public void onPageSelected(int pos) {
            Log.i(TAG, "TabFragOnPageChange> onPageSelected(" + pos + ")");
            // 如果圆点背景视图未加载
            if (vPoints == null) {
                return;
            }

            // 多圆点背景宽
            int layoutW = vPoints.getWidth();
            // 单个圆点宽
            int pointW = vSelectedPoint.getWidth();
            // 背景有几个圆点
            int pointSum = vPoints.getChildCount();
            // 背景圆点间距
            int distance = (layoutW - pointSum * pointW) / (pointSum - 1);
            // 每次圆点应该位移的距离
            int delta = distance + pointW;

            // 实心圆点起始位置
            float fromXDelta = mmLastPageIdx * delta;
            // 向右
            if (pos > mmLastPageIdx) {
                Log.i(TAG, ">>>>>>>>>>>>");
                transPoint(fromXDelta, fromXDelta + delta);
                // 向左
            } else if (pos < mmLastPageIdx) {
                Log.i(TAG, "<<<<<<<<<<<<");
                transPoint(fromXDelta, fromXDelta - delta);
            }

            // 记录上一次页面索引
            mmLastPageIdx = pos;
        }

        private void transPoint(final float fromXDelta, final float toXDelta) {
            if (vSelectedPoint != null) {
                ObjectAnimator objAnim = ObjectAnimator.ofFloat(vSelectedPoint, "x", fromXDelta, toXDelta);
                objAnim.setInterpolator(new LinearInterpolator());
                objAnim.setDuration(300);
                objAnim.start();
            }
        }

        int getPageIdx() {
            return mmLastPageIdx;
        }
    }

    private class SeekBarOnChange implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "onStartTrackingTouch");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "onStopTrackingTouch");
            int targetFreq = getMinFreq() + seekBar.getProgress();
            setFreqTxt(targetFreq, getCurrBand());
            play(targetFreq);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setFreqTxt(getMinFreq() + progress, getCurrBand());
        }
    }

    private void initSeekBar() {
        seekBarFreq.setMax(getSeekBarMax());
        seekBarFreq.setProgress(0);
    }

    /**
     * FM has 6 pages. AM has 3 pages.
     *
     * @return int
     */
    private int getPageSum() {
        final int lastBand = getLastBand();
        switch (lastBand) {
            case BandType.FM:
                return 6;
            case BandType.AM:
                return 3;
        }
        return 0;
    }

//    public int getTabFragIdx() {
//        return mTabFragOnPageChange.getPageIdx();
//    }
//
//    public void playFavored(int freq) {
//        Log.i(TAG, "playFavored(" + freq + ")");
//    }
}