package js.lib.android.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

/**
 * Audio Control Methods
 *
 * @author Jun.Wang
 */
public class AudioFocusUtil {
    // TAG
    private String TAG = "AudioFocusUtil";
    private Context mContext;

    // Audio Manager
    private AudioManager mAudioManager;

    /**
     * Audio Focus Listener
     */
    private AudioFocusListener mAudioFocusListener;

    public interface AudioFocusListener {
        void respAudioFocusTransient();

        void respAudioFocusDuck();

        void respAudioFocusLoss();

        void respAudioFocusGain();
    }

    /**
     * Constructor
     *
     * @param context : {@link Context}
     * @param l       :{@link AudioFocusListener}
     */
    public AudioFocusUtil(Context context, AudioFocusListener l) {
        mContext = context;
        mAudioFocusListener = l;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 设置日志标签
     *
     * @param logTAG :如"-Radio"，最终TAG="callback-Radio"
     */
    public void setLogTAG(String logTAG) {
        TAG += logTAG;
    }

    /**
     * Get AudioManager
     *
     * @return {@link AudioManager}
     */
    public AudioManager getAudioManager() {
        return mAudioManager;
    }

    /**
     * Set Parameters
     *
     * @param params : 如"kill_others=true"
     */
    public void setParameters(String params) {
        mAudioManager.setParameters(params);
    }

    private OnAudioFocusChangeListener mAudioFocusChangeListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            Logs.i(TAG, "f*----focusChange----[" + focusChange + "]");
            // 暂时失去Audio Focus，并会很快再次获得。必须停止Audio的播放，
            // 但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源；
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                Logs.i(TAG, "a*----AudioManager.AUDIOFOCUS_LOSS_TRANSIENT----");
                respAfTransient();

                // 暂时失去AudioFocus，但是可以继续播放，不过要在降低音量。
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                Logs.i(TAG, "a*----AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK----");
                respAfDuck();

                // 失去了Audio Focus，并将会持续很长的时间。
                // 这里因为可能会停掉很长时间，所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。
                // 而因为停止播放Audio的时间会很长，如果程序因为这个原因而失去AudioFocus，
                // 最好不要让它再次自动获得AudioFocus而继续播放，不然突然冒出来的声音会让用户感觉莫名其妙，感受很不好。
                // 这里直接放弃AudioFocus，当然也不用再侦听远程播放控制【如下面代码的处理】。
                // 要再次播放，除非用户再在界面上点击开始播放，才重新初始化Media，进行播放
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Logs.i(TAG, "a*----AudioManager.AUDIOFOCUS_LOSS----");
                respAfLoss();

                // 获得了Audio Focus；
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Logs.i(TAG, "a*----AudioManager.AUDIOFOCUS_GAIN----");
                respAfGain();
            }
        }

        private void respAfTransient() {
            if (mAudioFocusListener != null) {
                mAudioFocusListener.respAudioFocusTransient();
            }
        }

        private void respAfDuck() {
            if (mAudioFocusListener != null) {
                mAudioFocusListener.respAudioFocusDuck();
            }
        }

        private void respAfLoss() {
            if (mAudioFocusListener != null) {
                mAudioFocusListener.respAudioFocusLoss();
            }
        }

        private void respAfGain() {
            if (mAudioFocusListener != null) {
                mAudioFocusListener.respAudioFocusGain();
            }
        }
    };

    /**
     * Register Audio Focus
     * <p>
     * if==1 : Register audio focus
     * <p>
     * if==2 : Abandon audio focus
     */
    public int registerAudioFocus(int flag) {
        if (flag == 1) {
            return mAudioManager.requestAudioFocus(mAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        } else if (flag == 2) {
            return mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
        }
        return -1;
    }
}
