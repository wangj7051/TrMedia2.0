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

import com.tricheer.radio.MainActivity;
import com.tricheer.radio.R;

import js.lib.android.fragment.BaseAppV4Fragment;

/**
 * Collect frequency page
 *
 * @author Jun.Wang
 */
public class TabFreqCollectFragment extends BaseAppV4Fragment {
    //TAG
    private final String TAG = "FreqCollectFrag";

    public TabFreqCollectFragment() {
    }

    /**
     * ==========Variables in this Activity==========
     */
    private MainActivity mAttachedActivity;


    /**
     * ==========Widgets in this Activity==========
     */
    private View contentV;
    private TextView tvItem0, tvItem1, tvItem2, tvItem3, tvItem4, tvItem5;

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
        tvItem0 = (TextView) contentV.findViewById(R.id.tv_collect1);
        tvItem0.setOnClickListener(mFilterViewOnClick);
        tvItem0.setOnLongClickListener(mOnLongClick);

        tvItem1 = (TextView) contentV.findViewById(R.id.tv_collect2);
        tvItem1.setOnClickListener(mFilterViewOnClick);
        tvItem1.setOnLongClickListener(mOnLongClick);

        tvItem2 = (TextView) contentV.findViewById(R.id.tv_collect3);
        tvItem2.setOnClickListener(mFilterViewOnClick);
        tvItem2.setOnLongClickListener(mOnLongClick);

        tvItem3 = (TextView) contentV.findViewById(R.id.tv_collect4);
        tvItem3.setOnClickListener(mFilterViewOnClick);
        tvItem3.setOnLongClickListener(mOnLongClick);

        tvItem4 = (TextView) contentV.findViewById(R.id.tv_collect5);
        tvItem4.setOnClickListener(mFilterViewOnClick);
        tvItem4.setOnLongClickListener(mOnLongClick);

        tvItem5 = (TextView) contentV.findViewById(R.id.tv_collect6);
        tvItem5.setOnClickListener(mFilterViewOnClick);
        tvItem5.setOnLongClickListener(mOnLongClick);

        //
        loadFavored();
    }

    private void loadFavored() {
//        int pageIdx = getFragIdx();
//        int band = getCurrBand();
//        int freq = getCurrFreq();

//        int faveFreq0 = PreferUtils.getCollect(false, band, pageIdx, 0, freq);
//        tvItem0.setText(String.valueOf(faveFreq0 / 100d));
//
//        int faveFreq1 = PreferUtils.getCollect(false, band, pageIdx, 1, freq);
//        tvItem1.setText(String.valueOf(faveFreq1 / 100d));
//
//        int faveFreq2 = PreferUtils.getCollect(false, band, pageIdx, 2, freq);
//        tvItem2.setText(String.valueOf(faveFreq2 / 100d));
//
//        int faveFreq3 = PreferUtils.getCollect(false, band, pageIdx, 3, freq);
//        tvItem3.setText(String.valueOf(faveFreq3 / 100d));
//
//        int faveFreq4 = PreferUtils.getCollect(false, band, pageIdx, 4, freq);
//        tvItem4.setText(String.valueOf(faveFreq4 / 100d));
//
//        int faveFreq5 = PreferUtils.getCollect(false, band, pageIdx, 5, freq);
//        tvItem5.setText(String.valueOf(faveFreq5 / 100d));
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

        //Set enable
        tvItem0.setEnabled(!isScanning);
        tvItem1.setEnabled(!isScanning);
        tvItem2.setEnabled(!isScanning);
        tvItem3.setEnabled(!isScanning);
        tvItem4.setEnabled(!isScanning);
        tvItem5.setEnabled(!isScanning);

        //Set text color
        tvItem0.setTextColor(txtColor);
        tvItem1.setTextColor(txtColor);
        tvItem2.setTextColor(txtColor);
        tvItem3.setTextColor(txtColor);
        tvItem4.setTextColor(txtColor);
        tvItem5.setTextColor(txtColor);
    }

    View.OnLongClickListener mOnLongClick = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            Log.i(TAG, "mOnLongClick> onLongClick");
            if (v == tvItem0) {
                favorCurr(tvItem0, 0);
            } else if (v == tvItem1) {
                favorCurr(tvItem1, 1);
            } else if (v == tvItem2) {
                favorCurr(tvItem2, 2);
            } else if (v == tvItem3) {
                favorCurr(tvItem3, 3);
            } else if (v == tvItem4) {
                favorCurr(tvItem4, 4);
            } else if (v == tvItem5) {
                favorCurr(tvItem5, 5);
            }
            return true;
        }

        private void favorCurr(TextView tv, int position) {
//            int currFreq = getCurrFreq();
//            PreferUtils.getCollect(true, getCurrBand(), getFragIdx(), position, currFreq);
//            tv.setText(String.valueOf(currFreq / 100d));
        }
    };

    private View.OnClickListener mFilterViewOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.i(TAG, "mFilterViewOnClick> onClick");
            switchFilter(v);
            if (v == tvItem0) {
                playFavored(0);
            } else if (v == tvItem1) {
                playFavored(1);
            } else if (v == tvItem2) {
                playFavored(2);
            } else if (v == tvItem3) {
                playFavored(3);
            } else if (v == tvItem4) {
                playFavored(4);
            } else if (v == tvItem5) {
                playFavored(5);
            }
        }

        private void switchFilter(View v) {
            setBg(tvItem0, v == tvItem0);
            setBg(tvItem1, v == tvItem1);
            setBg(tvItem2, v == tvItem2);
            setBg(tvItem3, v == tvItem3);
            setBg(tvItem4, v == tvItem4);
            setBg(tvItem5, v == tvItem5);
        }

        private void setBg(View v, boolean selected) {
            if (selected) {
                v.setBackgroundResource(R.drawable.bg_title_item_c);
            } else {
                v.setBackgroundResource(R.drawable.btn_collect_selector);
            }
        }

        private void playFavored(int freq) {
            Log.i(TAG, "playFavored(" + freq + ")");
//            mAttachedActivity.playFavored(freq);
        }
    };
}
