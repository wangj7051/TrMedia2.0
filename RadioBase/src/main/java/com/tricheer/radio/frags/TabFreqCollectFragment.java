package com.tricheer.radio.frags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tri.lib.radio.engine.BandCategoryEnum;
import com.tricheer.radio.MainActivity;
import com.tricheer.radio.R;
import com.tricheer.radio.utils.FreqFormatUtil;
import com.tricheer.radio.utils.TrRadioPreferUtils;

import js.lib.android.fragment.BaseAppV4Fragment;

/**
 * Collect frequency page
 *
 * @author Jun.Wang
 */
public class TabFreqCollectFragment extends BaseAppV4Fragment {
    //TAG
    private final String TAG = "FreqCollectFrag";

    /**
     * ==========Variables in this Activity==========
     */
    private MainActivity mAttachedActivity;


    /**
     * ==========Widgets in this Activity==========
     */
    private View contentV;
    private TextView tvItems[] = new TextView[6];
    private int mPageIdx;

    public void setPageIdx(int pageIdx) {
        mPageIdx = pageIdx;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAttachedActivity = (MainActivity) activity;
        Log.i(TAG, "onAttach(Activity)");
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentV = inflater.inflate(R.layout.frag_collect, null);
        return contentV;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()" + this.toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        // ---- Widgets ----
        tvItems[0] = (TextView) contentV.findViewById(R.id.tv_collect1);
        tvItems[0].setOnClickListener(mFilterViewOnClick);
        tvItems[0].setOnLongClickListener(mOnLongClick);

        tvItems[1] = (TextView) contentV.findViewById(R.id.tv_collect2);
        tvItems[1].setOnClickListener(mFilterViewOnClick);
        tvItems[1].setOnLongClickListener(mOnLongClick);

        tvItems[2] = (TextView) contentV.findViewById(R.id.tv_collect3);
        tvItems[2].setOnClickListener(mFilterViewOnClick);
        tvItems[2].setOnLongClickListener(mOnLongClick);

        tvItems[3] = (TextView) contentV.findViewById(R.id.tv_collect4);
        tvItems[3].setOnClickListener(mFilterViewOnClick);
        tvItems[3].setOnLongClickListener(mOnLongClick);

        tvItems[4] = (TextView) contentV.findViewById(R.id.tv_collect5);
        tvItems[4].setOnClickListener(mFilterViewOnClick);
        tvItems[4].setOnLongClickListener(mOnLongClick);

        tvItems[5] = (TextView) contentV.findViewById(R.id.tv_collect6);
        tvItems[5].setOnClickListener(mFilterViewOnClick);
        tvItems[5].setOnLongClickListener(mOnLongClick);

        //
        loadCollected();
    }

    private void loadCollected() {
        BandCategoryEnum band = getCurrBand();
        int loop = tvItems.length;
        for (int idx = 0; idx < loop; idx++) {
            TextView tv = tvItems[idx];
            int collectedFreq = TrRadioPreferUtils.getCollect(false, band, mPageIdx, idx, 0);
            if (collectedFreq != -1) {
                tv.setTag(collectedFreq);
                tv.setText(FreqFormatUtil.getFreqStr(band, collectedFreq));
            }
        }
        refreshItemsBgByCurrFreq();
    }

    public void refreshItemsBgByCurrFreq() {
        //Flag first position that has the same frequency as current.
        boolean isCollectedBgSelected = false;
        int currFreq = getCurrFreq();
        for (TextView tv : tvItems) {
            if (tv == null) {
                continue;
            }
            if (isCollectedBgSelected) {
                setBg(tv, false);
            } else {
                Object objTag = tv.getTag();
                if (objTag != null) {
                    isCollectedBgSelected = ((int) objTag == currFreq);
                    setBg(tv, isCollectedBgSelected);
                } else {
                    setBg(tv, false);
                }
            }
        }
    }

    /**
     * Refresh page style when page is scanning
     *
     * @param isScanning true-Scanning
     */
    public void refreshPageOnScanning(boolean isScanning) {
        //Set text color
        int txtColor;
        if (isScanning) {
            txtColor = getResources().getColor(R.color.txt_on_searching);
        } else {
            txtColor = getResources().getColor(android.R.color.white);
        }

        for (TextView tv : tvItems) {
            tv.setEnabled(!isScanning);
            tv.setTextColor(txtColor);
        }
    }

    View.OnLongClickListener mOnLongClick = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            Log.i(TAG, "mOnLongClick> onLongClick");
            int loop = tvItems.length;
            for (int idx = 0; idx < loop; idx++) {
                TextView tv = tvItems[idx];
                if (tv == v) {
                    collectFreq(tv, idx);
                    break;
                }
            }
            return true;
        }

        private void collectFreq(TextView tv, int position) {
            int currFreq = getCurrFreq();
            BandCategoryEnum currBand = getCurrBand();
            TrRadioPreferUtils.getCollect(true, currBand, mPageIdx, position, currFreq);
            tv.setText(FreqFormatUtil.getFreqStr(currBand, currFreq));
            tv.setTag(currFreq);
        }
    };

    private BandCategoryEnum getCurrBand() {
        if (isAdded()) {
            return mAttachedActivity.getCurrBand();
        }
        return BandCategoryEnum.FM;
    }

    private int getCurrFreq() {
        if (isAdded()) {
            if (mAttachedActivity != null) {
                return mAttachedActivity.getCurrFreq();
            }
        }
        return -1;
    }

    private View.OnClickListener mFilterViewOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.i(TAG, "mFilterViewOnClick> onClick");
            for (TextView tv : tvItems) {
                if (tv == v) {
                    setBg(tv, true);
                    playCollected(tv);
                } else {
                    setBg(tv, false);
                }
            }
        }

        private void playCollected(View v) {
            Log.i(TAG, "playCollected(View)");
            Object objTag = v.getTag();
            if (objTag != null && objTag instanceof Integer) {
                int collectedFreq = (int) objTag;
                if (collectedFreq != getCurrFreq()) {
                    if (isAdded()) {
                        mAttachedActivity.playCollected((Integer) objTag);
                    }
                }
            }
        }
    };

    private void setBg(View v, boolean selected) {
        if (selected) {
            v.setBackgroundResource(R.drawable.bg_title_item_c);
        } else {
            v.setBackgroundResource(R.drawable.btn_collect_selector);
        }
    }
}
