package js.lib.android.media;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

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

    private static Map<Integer, PlayMode> mMapModes;

    @SuppressLint("UseSparseArrays")
    public static PlayMode getMode(int modeVal) {
        if (mMapModes == null) {
            mMapModes = new HashMap<>();
            mMapModes.put(SINGLE.getValue(), SINGLE);
            mMapModes.put(RANDOM.getValue(), RANDOM);
            mMapModes.put(LOOP.getValue(), LOOP);
            mMapModes.put(ORDER.getValue(), ORDER);
        }
        return mMapModes.get(modeVal);
    }
}
