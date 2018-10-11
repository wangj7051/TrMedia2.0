package js.lib.android.media.player;

import android.annotation.SuppressLint;
import android.util.SparseArray;

/**
 * 播放模式
 * <p>
 * 音乐播放器播放模式，如"单曲循环/随机模式/循环模式/顺序模式"
 *
 * @author Jun.Wang
 */
public enum PlayMode {
    /**
     * 单曲循环
     */
    SINGLE(1),
    /**
     * 随机模式
     */
    RANDOM(2),
    /**
     * 循环模式
     */
    LOOP(3),
    /**
     * 顺序模式
     */
    ORDER(4);

    private int mValue;

    PlayMode(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    private static SparseArray<PlayMode> mSaModes;

    @SuppressLint("UseSparseArrays")
    public static PlayMode getMode(int modeVal) {
        if (mSaModes == null) {
            mSaModes = new SparseArray<>();
            mSaModes.put(SINGLE.getValue(), SINGLE);
            mSaModes.put(RANDOM.getValue(), RANDOM);
            mSaModes.put(LOOP.getValue(), LOOP);
            mSaModes.put(ORDER.getValue(), ORDER);
        }
        PlayMode playMode = mSaModes.get(modeVal);
        return (playMode == null) ? LOOP : playMode;
    }
}
